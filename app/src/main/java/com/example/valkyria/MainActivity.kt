package com.example.valkyria

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.text.TextWatcher
import android.text.Editable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val olvido = findViewById<TextView>(R.id.txt_olvido_contraseña)

        olvido.setOnClickListener {
            val intent = Intent(this, Recuperacion_contrasenas::class.java)
            startActivity(intent)
        }

        val email = findViewById<TextInputEditText>(R.id.entrada_email_edit)
        val password = findViewById<TextInputEditText>(R.id.entrada_contrasena_edit)
        val boton = findViewById<MaterialButton>(R.id.btn_ingresar)
        val layoutPassword = findViewById<TextInputLayout>(R.id.layout_contrasena)
        val layoutEmail = findViewById<TextInputLayout>(R.id.layout_email)

        email.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                layoutEmail.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                layoutPassword.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        boton.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passText = password.text.toString().trim()

            if (emailText.isEmpty() || passText.isEmpty()) {
                if (emailText.isEmpty()) {
                    layoutEmail.error = "Ingresa tu correo"
                }

                if (passText.isEmpty()) {
                    layoutPassword.error = "Ingresa tu contraseña"
                }
            } else {
                val intent = Intent(this, Baul_contrasenas::class.java)
                startActivity(intent)
            }
        }

        val registro = findViewById<TextView>(R.id.txt_registro)

        registro.setOnClickListener {
            val intent = Intent(this, crearCuenta::class.java)
            startActivity(intent)
        }

        val texto = findViewById<TextView>(R.id.txt_registro)

        val textoCompleto = "¿Nuevo en Valkyria? Crea una cuenta"
        val spannable = SpannableString(textoCompleto)

        val inicio = textoCompleto.indexOf("Crea una cuenta")
        val fin = inicio + "Crea una cuenta".length

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.azul_1)),
            inicio,
            fin,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        texto?.let {
            it.text = spannable
            it.movementMethod = LinkMovementMethod.getInstance()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}
