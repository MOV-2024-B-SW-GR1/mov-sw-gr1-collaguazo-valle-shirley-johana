package com.example.deber01

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnJardin: Button = findViewById(R.id.btnJardin)
        val btnFlor: Button = findViewById(R.id.btnFlor)

        // Botón para ir a la gestión de jardines
        btnJardin.setOnClickListener {
            val intent = Intent(this, JardinActivity::class.java)
            startActivity(intent)
        }

        // Botón para ir a la gestión de flores
        btnFlor.setOnClickListener {
            val intent = Intent(this, SeleccionarJardinParaFloresActivity::class.java)
            startActivity(intent)
        }
    }
}
