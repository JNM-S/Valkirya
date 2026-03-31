package com.example.valkyria

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetallesContrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_contrasena)

        // Botón volver
        findViewById<android.widget.ImageView>(R.id.btn_back_detalles).setOnClickListener {
            finish()
        }
    }
}
