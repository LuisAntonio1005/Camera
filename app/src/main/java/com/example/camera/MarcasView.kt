package com.example.camera

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MarcasView : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: AdminSQLiteOpenHelper

    data class marcasData(val telar: String, val marca: String, val planta: String, val turno: String, val fecha: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marcas_view)

        recyclerView = findViewById(R.id.recyclerView)
        dbHelper = AdminSQLiteOpenHelper(this, "administracion", null, 1)

        val data = fetchDataFromUrdimbresTable()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MarcasAdapter(data)
    }

    @SuppressLint("Range")
    private fun fetchDataFromUrdimbresTable(): List<marcasData> {
        val dataList = mutableListOf<marcasData>()
        val database = dbHelper.readableDatabase
        val cursor: Cursor = database.rawQuery("SELECT * FROM marcas", null)

        while (cursor.moveToNext()) {
            val telarC = cursor.getString(cursor.getColumnIndex("telar"))
            val marcaC = cursor.getString(cursor.getColumnIndex("marca"))
            val plantaC = cursor.getString(cursor.getColumnIndex("planta"))
            val turnoC = cursor.getString(cursor.getColumnIndex("turno"))
            val fecha = cursor.getString(cursor.getColumnIndex("fecha"))

            dataList.add(marcasData(telarC, marcaC, plantaC, turnoC, fecha))
        }

        cursor.close()
        database.close()
        return dataList
    }

    inner class MarcasAdapter(private val data: List<marcasData>) :
        RecyclerView.Adapter<MarcasAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val telar: TextView = itemView.findViewById(R.id.textViewTelar)
            val marca: TextView = itemView.findViewById(R.id.textViewMarca)
            val planta: TextView = itemView.findViewById(R.id.textViewPlanta)
            val turno: TextView = itemView.findViewById(R.id.textViewTurno)
            val fech: TextView = itemView.findViewById(R.id.textViewFecha)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_marcas, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = data[position]
            holder.telar.text = "Telar: ${item.telar}"
            holder.marca.text = "Marca: ${item.marca}"
            holder.planta.text = "Planta: ${item.planta}"
            holder.turno.text = "Turno: ${item.turno}"
            holder.fech.text = "fecha: ${item.fecha}"
        }

        override fun getItemCount() = data.size
    }
}
