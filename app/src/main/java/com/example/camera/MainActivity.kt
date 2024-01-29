package com.example.camera

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerPlanta: Spinner
    private lateinit var txtTurno1: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerPlanta = findViewById(R.id.spinnerPlanta)
        txtTurno1 = findViewById(R.id.txtTurno1)

        // Crear un ArrayAdapter para el Spinner de Planta y establecer los elementos
        val plantaOptions = arrayOf("03", "1a", "1b")
        val plantaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, plantaOptions)
        plantaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlanta.adapter = plantaAdapter


        val continuar = findViewById<Button>(R.id.cont)
        continuar.setOnClickListener {
            val plantaValueActivity = spinnerPlanta.selectedItem.toString()
            val turnoValueActivity = txtTurno1.text.toString()

            if (isValidNumber(turnoValueActivity)) {
                val intent = Intent(this, FormUno::class.java)
                intent.putExtra("FROM", "FROM_MAIN")
                intent.putExtra("plantaValueActivity", plantaValueActivity)
                intent.putExtra("turnoValueActivity", turnoValueActivity)
                Toast.makeText(this, "Planta: $plantaValueActivity Turno: $turnoValueActivity", Toast.LENGTH_LONG).show()
                startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                Toast.makeText(this, "Ingrese un valor numérico válido en el campo Turno", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
            finishAffinity()
    }

    private fun isValidNumber(input: String): Boolean {
        return try {
            input.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}