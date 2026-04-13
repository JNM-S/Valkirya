package com.example.valkyria

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Aplicar modo claro/oscuro guardado (default: claro)
        val appPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = appPrefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else        AppCompatDelegate.MODE_NIGHT_NO
        )

        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val logueado = prefs.getBoolean("logueado", false)
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    val intent = Intent(this@MainActivity, Baul_contrasenas::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@MainActivity, "Huella incorrecta", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación biométrica")
            .setSubtitle("Usa tu huella para ingresar")
            .setNegativeButtonText("Cancelar")
            .build()

        if (logueado) {
            val biometricManager = BiometricManager.from(this)

            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                == BiometricManager.BIOMETRIC_SUCCESS) {

                biometricPrompt.authenticate(promptInfo)
            }
        }

        val email = findViewById<TextInputEditText>(R.id.entrada_email_edit)
        val password = findViewById<TextInputEditText>(R.id.entrada_contrasena_edit)
        val boton = findViewById<MaterialButton>(R.id.btn_ingresar)

        val layoutEmail = findViewById<TextInputLayout>(R.id.layout_email)
        val layoutPassword = findViewById<TextInputLayout>(R.id.layout_contrasena)


        email.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                val texto = s.toString().trim()

                val prefsUsuarios = getSharedPreferences("usuarios", MODE_PRIVATE)

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(texto).matches()) {
                    boton.text = "Ingresar"
                } else {
                    val existe = prefsUsuarios.contains(texto)
                    boton.text = if (existe) "Ingresar" else "Crear cuenta"
                }

                when {
                    texto.isEmpty() -> {
                        layoutEmail.error = null
                    }

                    !texto.contains("@") -> {
                        layoutEmail.error = "Debe contener @"
                    }

                    !android.util.Patterns.EMAIL_ADDRESS.matcher(texto).matches() -> {
                        layoutEmail.error = "Correo inválido"
                    }

                    else -> {
                        layoutEmail.error = null
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        boton.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passText = password.text.toString().trim()

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                layoutEmail.error = "Correo inválido"
                return@setOnClickListener
            }

            if (passText.isEmpty()) {
                layoutPassword.error = "Ingresa tu contraseña"
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("usuarios", MODE_PRIVATE)
            val savedPassword = prefs.getString(emailText, null)

            if (savedPassword == null) {
                Toast.makeText(this, "Usuario no existe, crea una cuenta", Toast.LENGTH_SHORT)
                    .show()

                val intent = Intent(this, crearCuenta::class.java)
                intent.putExtra("email", emailText)
                startActivity(intent)

            } else {
                if (savedPassword == passText) {
                    getSharedPreferences("sesion", MODE_PRIVATE)
                        .edit()
                        .putBoolean("logueado", true)
                        .apply()

                    val intent = Intent(this, Baul_contrasenas::class.java)
                    startActivity(intent)

                } else {
                    layoutPassword.error = "Contraseña incorrecta"
                }
            }
        }
        val btnBiometria = findViewById<ConstraintLayout>(R.id.btn_biometria)

        btnBiometria.setOnClickListener {

            val logueado = prefs.getBoolean("logueado", false)

            if (!logueado) {
                Toast.makeText(this, "Primero inicia sesión", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val biometricManager = BiometricManager.from(this)

            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                == BiometricManager.BIOMETRIC_SUCCESS) {

                biometricPrompt.authenticate(promptInfo)

            } else {
                Toast.makeText(this, "Biometría no disponible", Toast.LENGTH_SHORT).show()
            }
        }

        val olvido = findViewById<TextView>(R.id.txt_olvido_contraseña)

        olvido.setOnClickListener {
            startActivity(Intent(this, Recuperacion_contrasenas::class.java))
        }

        val texto = findViewById<TextView>(R.id.txt_registro)

        texto.setOnClickListener {
            startActivity(Intent(this, crearCuenta::class.java))
        }
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

        texto.text = spannable
    }
}