package com.example.valkyria

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

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
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Toast.makeText(this, "Error: no hay usuario activo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Recargar el estado del usuario para verificar si ya confirmó el email
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful && user.isEmailVerified) {
                    getSharedPreferences("sesion", MODE_PRIVATE)
                        .edit()
                        .putBoolean("logueado", true)
                        .apply()

                    Toast.makeText(this, "Cuenta verificada correctamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Baul_contrasenas::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Aún no has verificado tu correo. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show()
                }
            }
        }

        val btnReenviar = findViewById<MaterialButton>(R.id.btn_reenviar)
        btnReenviar.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            user?.sendEmailVerification()
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Correo de verificación reenviado", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener {
                    Toast.makeText(this, "Error al reenviar: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
                ?: Toast.makeText(this, "No hay usuario activo", Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
