package com.example.valkyria

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import android.text.TextWatcher
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class crearCuenta : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_cuenta)

        val atrascc = findViewById<ImageView>(R.id.atras)
        atrascc.setOnClickListener {
            finish()
        }

        val prefijo = findViewById<AutoCompleteTextView>(R.id.input_prefijo)

        val prefijos = listOf("+57", "+1", "+34", "+52", "+54")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, prefijos)
        prefijo.setAdapter(adapter)

        prefijo.setOnClickListener {
            prefijo.showDropDown()
        }


        val paises = listOf(
            "Colombia",
            "México",
            "Argentina",
            "España",
            "Chile"
        )

        val inputPais = findViewById<AutoCompleteTextView>(R.id.entrada_pais)

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, paises)
        inputPais.setAdapter(adapter2)

        inputPais.setOnClickListener {
            inputPais.showDropDown()
        }

        val texto = findViewById<TextView>(R.id.text_legal)

        val textoCompleto = "Al registrarte, aceptas nuestros Términos de Servicio \ny \nPolítica de Privacidad."
        val spannable = SpannableString(textoCompleto)

        val inicio = textoCompleto.indexOf("Términos de Servicio \ny \nPolítica de Privacidad.")
        val fin = inicio + "Términos de Servicio \ny \nPolítica de Privacidad.".length

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

        val usuario = findViewById<TextInputEditText>(R.id.entrada_usuario)
        val layoutUsuario = findViewById<TextInputLayout>(R.id.layout_nombre_usuario)
        val correocc = findViewById<TextInputEditText>(R.id.entrada_email_edit_c_c)
        val layoutCorreocc = findViewById<TextInputLayout>(R.id.layout_email_c_c)
        val telefono = findViewById<TextView>(R.id.entrada_telefono)
        val layoutTelefono = findViewById<TextInputLayout>(R.id.layout_numero_telefono)
        val passwordcc = findViewById<TextInputEditText>(R.id.entrada_contrasena_edit_c_c)
        val layoutPasswordcc = findViewById<TextInputLayout>(R.id.layout_contrasena_c_c)
        val layoutPais = findViewById<TextInputLayout>(R.id.layout_pais)
        val botoncc = findViewById<MaterialButton>(R.id.btn_registrar)

        usuario.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                layoutUsuario.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        correocc.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                layoutCorreocc.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        telefono.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                layoutTelefono.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        passwordcc.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                layoutPasswordcc.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputPais.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                layoutPais.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        botoncc.setOnClickListener {

            val usuarioText = usuario.text.toString().trim()
            val correoccText = correocc.text.toString().trim()
            val telefonoText = telefono.text.toString().trim()
            val passccText = passwordcc.text.toString().trim()
            val paisText = inputPais.text.toString().trim()

            if (usuarioText.isEmpty() || correoccText.isEmpty() || telefonoText.isEmpty() || passccText.isEmpty() || paisText.isEmpty()) {

                if (usuarioText.isEmpty()) layoutUsuario.error = "Ingresa tu nombre"
                if (correoccText.isEmpty()) layoutCorreocc.error = "Ingresa tu correo"
                if (telefonoText.isEmpty()) layoutTelefono.error = "Ingresa tu teléfono"
                if (passccText.isEmpty()) layoutPasswordcc.error = "Ingresa tu contraseña"
                if (paisText.isEmpty()) layoutPais.error = "Selecciona un país"

                return@setOnClickListener
            }


            val prefs = getSharedPreferences("usuarios", MODE_PRIVATE)

            prefs.edit()
                // Datos activos del usuario actual
                .putString("nombre_usuario", usuarioText)
                .putString("correo_usuario", correoccText)
                .putString("telefono_usuario", telefonoText)
                .putString("prefijo_usuario", prefijo.text.toString().trim())
                // Datos persistentes por correo (para restaurar al hacer login)
                .putString("${correoccText}_nombre", usuarioText)
                .putString("${correoccText}_telefono", telefonoText)
                .putString("${correoccText}_prefijo", prefijo.text.toString().trim())
                // Contraseña
                .putString(correoccText, passccText)
                .apply()

            val intent = Intent(this, Verificacion::class.java)
            intent.putExtra("email", correoccText)
            intent.putExtra("password", passccText)

            startActivity(intent)
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
