package com.example.valkirya

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
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.TextPaint
import android.view.View
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main) // 👈 PRIMERO ESTO

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

        val olvido = findViewById<TextView>(R.id.txt_olvido_contraseña)

        olvido.setOnClickListener {
            val intent = Intent(this, Recuperacion_contrasenas::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}
