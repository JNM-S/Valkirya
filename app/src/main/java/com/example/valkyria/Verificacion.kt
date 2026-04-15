package com.example.valkyria

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton


class Verificacion : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verificacion)

        val btnAtras = findViewById<ImageView>(R.id.atras_v)
        btnAtras.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
            finish()
        }


        val btnVerificar = findViewById<MaterialButton>(R.id.btn_verificar)

        btnVerificar.setOnClickListener {

            val email = intent.getStringExtra("email")
            val password = intent.getStringExtra("password")

            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(this, "Error en los datos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("usuarios", MODE_PRIVATE)

            if (prefs.contains(email)) {
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit().putString(email, password).apply()

            getSharedPreferences("sesion", MODE_PRIVATE)
                .edit()
                .putBoolean("logueado", true)
                .apply()

            Toast.makeText(this, "Cuenta verificada correctamente", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, Baul_contrasenas::class.java))
            finish()
        }

        val btnReenviar = findViewById<MaterialButton>(R.id.btn_reenviar)

        btnReenviar.setOnClickListener {
            Toast.makeText(this, "Correo reenviado (simulación)", Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
