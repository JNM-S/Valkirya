package com.example.valkyria

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class CambioContrasena : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cambio_contrasena)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<android.widget.ImageView>(R.id.btn_back_cambio).setOnClickListener {
            finish()
        }

        findViewById<android.widget.TextView>(R.id.link_volver_config).setOnClickListener {
            finish()
        }

        val email = findViewById<TextInputEditText>(R.id.entrada_email_cambio)
        val layoutEmail = findViewById<TextInputLayout>(R.id.layout_email_cambio)
        val boton = findViewById<MaterialButton>(R.id.btn_enviar_cambio)

        // Pre-llenar con el correo del usuario actual
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.email?.let { email.setText(it) }

        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { layoutEmail.error = null }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        boton.setOnClickListener {
            val emailText = email.text.toString().trim()
            if (emailText.isEmpty()) {
                layoutEmail.error = "Ingresa tu correo"
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                layoutEmail.error = "Correo inválido"
            } else {
                // Enviar correo real de cambio de contraseña via Firebase
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailText)
                    .addOnSuccessListener {
                        MaterialAlertDialogBuilder(this)
                            .setTitle("Cambio de contraseña")
                            .setMessage("Te hemos enviado un correo a $emailText con las instrucciones para cambiar tu contraseña. Revisa tu bandeja de entrada o de spam.")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar") { dialog, _ ->
                                dialog.dismiss()
                                finish()
                            }
                            .show()
                    }
                    .addOnFailureListener { e ->
                        MaterialAlertDialogBuilder(this)
                            .setTitle("Error")
                            .setMessage(e.localizedMessage ?: "No se pudo enviar el correo")
                            .setPositiveButton("Aceptar", null)
                            .show()
                    }
            }
        }
    }
}
