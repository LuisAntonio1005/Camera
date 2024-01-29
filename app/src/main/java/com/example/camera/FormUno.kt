package com.example.camera //nombre del paquete

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator //libreria del escaner
import com.google.zxing.integration.android.IntentResult
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FormUno : AppCompatActivity() {
    private lateinit var mText2: EditText
    private lateinit var planta: EditText
    private lateinit var turno: EditText
    private lateinit var telar: EditText
    private lateinit var permi: Button
    //********************onCreate***********************************************************************************
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_uno)
        //********elemnetos*****************************************************************************************************
        val capturar = findViewById<Button>(R.id.button)
        val guardar=findViewById<Button>(R.id.button3)
        val ver = findViewById<Button>(R.id.button8)
        val csv = findViewById<Button>(R.id.csv)
        permi = findViewById(R.id.per)
        val cancel = findViewById<Button>(R.id.can)
        telar=findViewById(R.id.txtTelar)
        mText2=findViewById(R.id.text2)
        planta=findViewById(R.id.txtPlanta)
        turno=findViewById(R.id.txtTurno)
        //**********recogerMarcaDeCamera****************************************************************************************
        val intent = intent
        val from = intent.getStringExtra("FROM")
        if (from == "FROM_MAIN") {
            // El Intent proviene de MainActivity
            // Muestrar los valores en los EditText
            val plantaValueActivity = intent.getStringExtra("plantaValueActivity")
            val turnoalueActivity = intent.getStringExtra("turnoValueActivity")
            planta.setText(plantaValueActivity)
            turno.setText(turnoalueActivity)

        }else if (from == "FROM_2") {
            val parametros = this.intent.extras

            if (parametros != null) {
                val datos = parametros.getString("datos")
                val plantaValue = intent.getStringExtra("planta")
                planta.setText(plantaValue)
                val turnoValue = intent.getStringExtra("turno")
                turno.setText(turnoValue)
                val telatValue = intent.getStringExtra("telar")
                telar.setText(telatValue)

                mText2.setText(datos)
            }
        }
        //****************CAPTURAR**************************************************************************************
        capturar.setOnClickListener {
            val plantaValue = planta.text.toString()
            val turnoValue = turno.text.toString()
            val telatValue = telar.text.toString()
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("planta", plantaValue)
            intent.putExtra("turno", turnoValue)
            intent.putExtra("telar", telatValue)
            startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        //**************** verMarcas **************************************************************************************************
        ver.setOnClickListener{
            val q = Intent(this, MarcasView::class.java)
            startActivity(q,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        //*************INSERTbD*******************************************************************************************
        guardar.setOnClickListener {
            val textToSave1 = telar.text.toString()
            val textToSave2 = mText2.text.toString()
            val plantaValue = planta.text.toString()
            val turnoValue = turno.text.toString()
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

            if (textToSave1.isNotEmpty() && textToSave2.isNotEmpty() ) {
                val registro = ContentValues()
                registro.put("telar", textToSave1)
                registro.put("marca", textToSave2)
                registro.put("planta", plantaValue)
                registro.put("turno", turnoValue)

                // Verificar el valor de turno y establecer la fecha en consecuencia
                if (turnoValue.toInt() >= 3) {
                    // Si el turno es mayor o igual a 3, establece la fecha de ayer
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                }

                val fecha = dateFormat.format(calendar.time)
                registro.put("fecha", fecha)
                val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
                val bd = admin.writableDatabase

                try {
                    val cursor = bd.rawQuery(
                        "SELECT * FROM marcas WHERE telar = ? AND turno = ?",
                        arrayOf(textToSave1, turnoValue)
                    )

                    if (cursor.count > 0) {
                        Toast.makeText(this, "Ya existe un registro con el mismo telar y turno", Toast.LENGTH_SHORT).show()
                    } else {
                        val resultado = bd.insert("marcas", null, registro)

                        if (resultado != -1L) {
                            Toast.makeText(this, "Se cargaron los datos de la marca", Toast.LENGTH_SHORT).show()
                            telar.setText("")
                            mText2.setText("")
                        } else {
                            Toast.makeText(this, "Error al insertar los datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                    cursor.close()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al insertar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    bd.close()
                }
            } else {
                Toast.makeText(this, "Verifica los datos", Toast.LENGTH_SHORT).show()
            }
        }
        //***************************************************************************************************************************************
        //boton de escaner en proceso
        val capt: Button = findViewById(R.id.cap)
        capt.setOnClickListener {
            // Inicia el lector de código de barras
            startBarcodeScanner(telar)
        }
        //***************************************************************************************************************************************
        cancel.setOnClickListener{
            telar.setText("")
            mText2.setText("")
        }
        //*******************************************************************************************************************************
        csv.setOnClickListener{
            exportDataToCSV1()
        }
        //*******************************************************************************************************************************
        permi.setOnClickListener{
            val p = Intent(this, permisos::class.java)
            startActivity(p,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }//oncreate ******************************************************************************************************
    override fun onBackPressed() {
        val plantaValue = planta.text.toString()
        val turnoValue = turno.text.toString()
        val uno = Intent(this, MainActivity::class.java)
        intent.putExtra("planta", plantaValue)
        intent.putExtra("turno", turnoValue)
        startActivity(uno,
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
    //******************************************************************************************************************
    @SuppressLint("Range")
    private fun exportDataToCSV1() {
        val turnoValue = turno.text.toString()
        val dbHelper = AdminSQLiteOpenHelper(this, "administracion", null, 1)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM marcas", null)
        //busca el directorio predefinido
        val carpeta = File(getExternalFilesDir(null)!!.absolutePath + "/MarcasCSV")
        val currentDateTime = SimpleDateFormat("yyyy.MM.dd_HH:mm:ss", Locale.getDefault()).format(Date())
        val archivoCB = "$carpeta/Marca_$turnoValue.csv"
        val file = File(archivoCB)

        if (!carpeta.exists()) {
            carpeta.mkdirs()
            Toast.makeText(this, "Se creó el directorio, $carpeta", Toast.LENGTH_SHORT).show()
        }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Estás seguro de que deseas realizar esta acción?")
            builder.setMessage("Se exportaran los datos y se borraran")
            builder.setPositiveButton("Sí") { dialog, which ->
                var csvWriter: CSVWriter? = null
                try {

                    csvWriter = CSVWriter(FileWriter(file, true))
                    while (cursor.moveToNext()) {

                        val telar = cursor.getString(cursor.getColumnIndex("telar"))
                        val marca = cursor.getString(cursor.getColumnIndex("marca"))
                        val planta = cursor.getString(cursor.getColumnIndex("planta"))
                        val turno = cursor.getString(cursor.getColumnIndex("turno"))
                        val fecha = cursor.getString(cursor.getColumnIndex("fecha"))
                        csvWriter.writeNext(arrayOf(telar,marca,planta,turno,fecha))
//                        fileWriter.append("$telar,$marca,$planta,$turno,$fecha\n")

                    }

                    Toast.makeText(this, "Datos exportados a CSV EN:\n $archivoCB", Toast.LENGTH_LONG).show()

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al exportar datos a CSV", Toast.LENGTH_SHORT).show()
                } finally {
                    cursor.close()
                    db.close()
                    csvWriter?.close()
                }
            }
            builder.setNegativeButton("No") { dialog, which ->
                // Código para manejar la cancelación o rechazo del usuario (puede ser simplemente cerrar el cuadro de diálogo)
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
    }
    //******************************************************************************************************************
    private fun borrarTodosLosDatos() {
        val dbHelper = AdminSQLiteOpenHelper(this, "administracion", null, 1)
        val db = dbHelper.readableDatabase
        val query = "DELETE FROM marcas"
        db.execSQL(query)
        db.close()
    }
   // escaner en proceso
    //***************************************************************************************************************************************************
    private fun startBarcodeScanner(targetEditText: EditText) {
        currentTargetEditText = targetEditText // Configura el EditText de destino
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Escanea un código de barras")
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }
    //***************************************************************************************************************************************
    // Maneja el resultado del escaneo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents != null) {
                // Determina cuál EditText debe llenarse
                if (currentTargetEditText == telar) {
                    telar.setText(result.contents)
                }
            }else{
                Toast.makeText(this, "lee de nuevo", Toast.LENGTH_SHORT).show()
            }
        }
    }
//************************************************************************************************************************************
    companion object {
        private var currentTargetEditText: EditText? = null
    }
}//class

