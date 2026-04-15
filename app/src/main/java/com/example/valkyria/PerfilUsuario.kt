package com.example.valkyria

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class PerfilUsuario : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón volver
        findViewById<android.widget.ImageView>(R.id.btn_back_perfil).setOnClickListener {
            finish()
        }

        // ── Cargar datos del usuario ───────────────────────────────────────
        val prefs = getSharedPreferences("usuarios", MODE_PRIVATE)

        val nombreOriginal   = prefs.getString("nombre_usuario", "") ?: ""
        val correoOriginal   = prefs.getString("correo_usuario", "") ?: ""
        val telefonoOriginal = prefs.getString("telefono_usuario", "") ?: ""
        val prefijoOriginal  = prefs.getString("prefijo_usuario", "+57") ?: "+57"

        val txtNombre  = findViewById<TextView>(R.id.txt_nombre_completo)
        val txtEmail   = findViewById<TextView>(R.id.txt_email_header)
        val inputNombre   = findViewById<TextInputEditText>(R.id.entrada_usuario_perfil)
        val inputEmail    = findViewById<TextInputEditText>(R.id.entrada_email_perfil)
        val inputTelefono = findViewById<EditText>(R.id.entrada_telefono_perfil)
        val inputPrefijo  = findViewById<AutoCompleteTextView>(R.id.input_prefijo_perfil)

        // Header
        txtNombre.text = nombreOriginal.ifEmpty { "Usuario" }
        txtEmail.text  = correoOriginal.ifEmpty { "Sin correo" }

        // Campos
        inputNombre.setText(nombreOriginal)
        inputEmail.setText(correoOriginal)
        inputTelefono.setText(telefonoOriginal)
        inputPrefijo.setText(prefijoOriginal)

        // ── Salir de la cuenta ────────────────────────────────────────────
        findViewById<MaterialButton>(R.id.btn_salir_cuenta).setOnClickListener {
            com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Salir de la cuenta")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Salir") { _, _ ->
                    getSharedPreferences("sesion", MODE_PRIVATE).edit()
                        .putBoolean("logueado", false)
                        .apply()
                    val intent = android.content.Intent(this, MainActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                                   android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // ── Cambiar cuenta ─────────────────────────────────────────────────
        findViewById<MaterialButton>(R.id.btn_cambiar_cuenta).setOnClickListener {
            // Limpiar sesión activa (no borra datos del usuario)
            getSharedPreferences("sesion", MODE_PRIVATE).edit()
                .putBoolean("logueado", false)
                .apply()
            // Limpiar datos de perfil en memoria (no vault)
            getSharedPreferences("usuarios", MODE_PRIVATE).edit()
                .remove("nombre_usuario")
                .remove("correo_usuario")
                .remove("telefono_usuario")
                .remove("prefijo_usuario")
                .apply()
            // Ir a login limpiando el back stack
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                           android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // ── Guardar cambios ────────────────────────────────────────────────
        findViewById<MaterialButton>(R.id.btn_guardar_perfil).setOnClickListener {
            val nuevoNombre   = inputNombre.text.toString().trim()
            val nuevoCorreo   = inputEmail.text.toString().trim()
            val nuevoTelefono = inputTelefono.text.toString().trim()
            val nuevoPrefijo  = inputPrefijo.text.toString().trim()

            // Verificar que ningún campo esté vacío
            if (nuevoNombre.isEmpty() || nuevoCorreo.isEmpty() || nuevoTelefono.isEmpty() || nuevoPrefijo.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar que al menos un campo haya cambiado
            val sinCambios = nuevoNombre == nombreOriginal &&
                             nuevoCorreo == correoOriginal &&
                             nuevoTelefono == telefonoOriginal &&
                             nuevoPrefijo == prefijoOriginal

            if (sinCambios) {
                Toast.makeText(this, "La información es igual, no hay cambios que guardar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar
            prefs.edit()
                .putString("nombre_usuario", nuevoNombre)
                .putString("correo_usuario", nuevoCorreo)
                .putString("telefono_usuario", nuevoTelefono)
                .putString("prefijo_usuario", nuevoPrefijo)
                // Actualizar también las claves persistentes por correo
                .putString("${nuevoCorreo}_nombre",   nuevoNombre)
                .putString("${nuevoCorreo}_telefono", nuevoTelefono)
                .putString("${nuevoCorreo}_prefijo",  nuevoPrefijo)
                .apply()

            // Actualizar header
            txtNombre.text = nuevoNombre
            txtEmail.text  = nuevoCorreo

            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
        }
    }
}
