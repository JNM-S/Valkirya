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
import com.google.firebase.auth.FirebaseAuth

class PerfilUsuario : BaseActivity() {

    private var nombreOriginal = ""
    private var correoOriginal = ""
    private var telefonoOriginal = ""
    private var prefijoOriginal = "+57"

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

        val txtNombre  = findViewById<TextView>(R.id.txt_nombre_completo)
        val txtEmail   = findViewById<TextView>(R.id.txt_email_header)
        val inputNombre   = findViewById<TextInputEditText>(R.id.entrada_usuario_perfil)
        val inputEmail    = findViewById<TextInputEditText>(R.id.entrada_email_perfil)
        val inputTelefono = findViewById<EditText>(R.id.entrada_telefono_perfil)
        val inputPrefijo  = findViewById<AutoCompleteTextView>(R.id.input_prefijo_perfil)

        // ── Cargar datos desde Firestore ───────────────────────────────────
        PasswordRepository.loadProfile { nombre, correo, telefono, prefijo ->
            nombreOriginal = nombre
            correoOriginal = correo
            telefonoOriginal = telefono
            prefijoOriginal = prefijo

            // Si Firestore no tiene datos, usar los de Firebase Auth
            val displayName = nombre.ifEmpty {
                FirebaseAuth.getInstance().currentUser?.displayName ?: ""
            }
            val email = correo.ifEmpty {
                FirebaseAuth.getInstance().currentUser?.email ?: ""
            }

            txtNombre.text = displayName.ifEmpty { "Usuario" }
            txtEmail.text  = email.ifEmpty { "Sin correo" }

            inputNombre.setText(displayName)
            inputEmail.setText(email)
            inputTelefono.setText(telefono)
            inputPrefijo.setText(prefijo)
        }

        // ── Salir de la cuenta ────────────────────────────────────────────
        findViewById<MaterialButton>(R.id.btn_salir_cuenta).setOnClickListener {
            com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Salir de la cuenta")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Salir") { _, _ ->
                    SessionManager.removeCurrentSession(this)
                    FirebaseAuth.getInstance().signOut()
                    getSharedPreferences("sesion", MODE_PRIVATE).edit()
                        .putBoolean("logueado", false)
                        .apply()
                    getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                        .remove("dark_mode")
                        .remove("2fa_enabled")
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
            SessionManager.removeCurrentSession(this)
            FirebaseAuth.getInstance().signOut()
            getSharedPreferences("sesion", MODE_PRIVATE).edit()
                .putBoolean("logueado", false)
                .apply()
            getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                .remove("dark_mode")
                .remove("2fa_enabled")
                .apply()
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

            if (nuevoNombre.isEmpty() || nuevoCorreo.isEmpty() || nuevoTelefono.isEmpty() || nuevoPrefijo.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sinCambios = nuevoNombre == nombreOriginal &&
                             nuevoCorreo == correoOriginal &&
                             nuevoTelefono == telefonoOriginal &&
                             nuevoPrefijo == prefijoOriginal

            if (sinCambios) {
                Toast.makeText(this, "La información es igual, no hay cambios que guardar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar en Firestore
            PasswordRepository.saveProfile(nuevoNombre, nuevoCorreo, nuevoTelefono, nuevoPrefijo)

            // Actualizar header
            txtNombre.text = nuevoNombre
            txtEmail.text  = nuevoCorreo

            // Actualizar originales para futuras comparaciones
            nombreOriginal = nuevoNombre
            correoOriginal = nuevoCorreo
            telefonoOriginal = nuevoTelefono
            prefijoOriginal = nuevoPrefijo

            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
        }
    }
}
