package com.example.valkyria

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Verificacion2FA : BaseActivity() {

    private var codigoGenerado = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verificacion_2fa)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email ?: ""
        val uid = user?.uid ?: ""

        findViewById<TextView>(R.id.txt_email_2fa).text = email

        findViewById<android.widget.ImageView>(R.id.btn_back_2fa).setOnClickListener {
            // Cerrar sesión si no completa 2FA
            FirebaseAuth.getInstance().signOut()
            finish()
        }

        val inputCodigo = findViewById<TextInputEditText>(R.id.entrada_codigo_2fa)
        val layoutCodigo = findViewById<TextInputLayout>(R.id.layout_codigo_2fa)
        val btnVerificar = findViewById<MaterialButton>(R.id.btn_verificar_2fa)
        val btnReenviar = findViewById<TextView>(R.id.btn_reenviar_2fa)

        // Generar y guardar código
        generarYGuardarCodigo(uid)

        btnVerificar.setOnClickListener {
            val codigoIngresado = inputCodigo.text.toString().trim()
            if (codigoIngresado.isEmpty()) {
                layoutCodigo.error = "Ingresa el código"
                return@setOnClickListener
            }
            if (codigoIngresado.length != 6) {
                layoutCodigo.error = "El código debe tener 6 dígitos"
                return@setOnClickListener
            }

            // Verificar código contra Firestore
            FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val codigoGuardado = doc.getString("2fa_code") ?: ""
                    val timestamp = doc.getLong("2fa_code_timestamp") ?: 0L
                    val expirado = System.currentTimeMillis() - timestamp > 5 * 60 * 1000 // 5 min

                    if (expirado) {
                        layoutCodigo.error = "Código expirado. Reenvía uno nuevo."
                    } else if (codigoIngresado == codigoGuardado) {
                        // Código correcto — limpiar y entrar
                        FirebaseFirestore.getInstance()
                            .collection("users").document(uid)
                            .update("2fa_code", "", "2fa_code_timestamp", 0L)

                        getSharedPreferences("sesion", MODE_PRIVATE)
                            .edit()
                            .putBoolean("logueado", true)
                            .apply()

                        SessionManager.registerSession(this)

                        Toast.makeText(this, "Verificación exitosa", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Baul_contrasenas::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        layoutCodigo.error = "Código incorrecto"
                    }
                }
        }

        btnReenviar.setOnClickListener {
            generarYGuardarCodigo(uid)
            inputCodigo.text?.clear()
            layoutCodigo.error = null
            Toast.makeText(this, "Nuevo código enviado a $email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generarYGuardarCodigo(uid: String) {
        codigoGenerado = (100000..999999).random().toString()

        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .update(
                mapOf(
                    "2fa_code" to codigoGenerado,
                    "2fa_code_timestamp" to System.currentTimeMillis()
                )
            )
            .addOnFailureListener {
                // Si el documento no tiene esos campos, usar set con merge
                FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .set(
                        mapOf(
                            "2fa_code" to codigoGenerado,
                            "2fa_code_timestamp" to System.currentTimeMillis()
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            }

        // Mostrar código en un diálogo (en producción se enviaría por email)
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Código de verificación")
            .setMessage("Tu código es:\n\n$codigoGenerado\n\n(En producción se enviaría a tu correo)")
            .setCancelable(false)
            .setPositiveButton("Entendido", null)
            .show()
    }
}
