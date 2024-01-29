package com.example.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class permisos : AppCompatActivity() {

    private lateinit var permisosButton: Button
    private lateinit var checjeText: TextView
    private val permissionLauncher: ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // Todos los permisos otorgados
            Toast.makeText(this, "Permisos otorgados", Toast.LENGTH_SHORT).show()
            permisosButton.visibility = View.INVISIBLE
            checjeText.visibility = View.VISIBLE
        } else {
            // Al menos un permiso no otorgado
            Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permisos)

        checjeText = findViewById(R.id.chec)
        permisosButton = findViewById(R.id.permisos)

        permisosButton.setOnClickListener {
            // Verificar si se tienen los permisos de cámara y almacenamiento
            val permissionsToCheck = arrayOf(
                Manifest.permission.CAMERA,

            )

            val permissionsToRequest = permissionsToCheck.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()

            if (permissionsToRequest.isEmpty()) {
                // Los permisos ya han sido otorgados, puedes realizar acciones aquí
                Toast.makeText(this, "Permisos ya otorgados", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, ":)", Toast.LENGTH_SHORT).show()
                permisosButton.visibility = View.INVISIBLE
                checjeText.visibility = View.VISIBLE
            } else {
                // Solicitar los permisos de cámara y almacenamiento
                permissionLauncher.launch(permissionsToRequest)
            }
        }
    }
}
