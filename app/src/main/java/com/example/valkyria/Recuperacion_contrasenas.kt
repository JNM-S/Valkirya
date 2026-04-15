package com.example.valkyria

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Recuperacion_contrasenas : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperacion_contrasenas)

        val atras = findViewById<ImageView>(R.id.bot_back)
        atras.setOnClickListener {
            finish()
        }

        val volver = findViewById<TextView>(R.id.volver_inicio_r_c)
        volver.setOnClickListener {
            finish()
        }


        val email2 = findViewById<TextInputEditText>(R.id.entrada_email_edit_r_c)
        val boton2 = findViewById<MaterialButton>(R.id.btn_enviar_intrucciones)
        val layoutEmail2 = findViewById<TextInputLayout>(R.id.layout_email_r_c)

        email2.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                layoutEmail2.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        boton2.setOnClickListener {
            val email2Text = email2.text.toString().trim()
            if (email2Text.isEmpty()) {
                if (email2Text.isEmpty()) {
                    layoutEmail2.error = "Ingresa tu correo"
                }
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Recuperación de contraseña")
                    .setMessage("Te hemos enviado un correo para recuperar tu contraseña. Revisa tu bandeja de entrada o de spam")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
