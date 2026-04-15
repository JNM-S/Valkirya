package com.example.valkyria

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DetallesContrasena : BaseActivity() {

    private var passwordVisible = false
    private var contrasena = ""

    companion object {
        private const val REQUEST_EDIT = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalles_contrasena)

        val nombre            = intent.getStringExtra("nombre")     ?: "Servicio"
        val usuario           = intent.getStringExtra("usuario")    ?: ""
        val icono             = intent.getIntExtra("icono", R.drawable.ic_service_placeholder)
        val iconName          = intent.getStringExtra("iconName")   ?: "ic_service_placeholder"
        contrasena            = intent.getStringExtra("contrasena") ?: ""
        val fechaCreacion     = intent.getLongExtra("fechaCreacion", System.currentTimeMillis())
        val fechaModificacion = intent.getLongExtra("fechaModificacion", System.currentTimeMillis())

        findViewById<TextView>(R.id.txt_nombre_servicio).text      = nombre
        findViewById<TextView>(R.id.txt_url_servicio).text         = nombre.lowercase() + ".com"
        findViewById<TextView>(R.id.txt_nombre_usuario_valor).text = usuario
        findViewById<ImageView>(R.id.img_servicio_icono).setImageResource(icono)

        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale("es"))
        findViewById<TextView>(R.id.txt_fecha_creacion).text     = sdf.format(java.util.Date(fechaCreacion))
        findViewById<TextView>(R.id.txt_fecha_modificacion).text = formatRelativeTime(fechaModificacion)

        val txtContrasena = findViewById<TextView>(R.id.txt_contrasena_valor)
        txtContrasena.text = "•".repeat(contrasena.length.coerceAtLeast(1))

        val btnToggle = findViewById<ImageView>(R.id.btn_toggle_visibilidad)
        btnToggle.setOnClickListener {
            passwordVisible = !passwordVisible
            txtContrasena.text = if (passwordVisible) contrasena
                                 else "•".repeat(contrasena.length.coerceAtLeast(1))
        }

        val barraFondo      = findViewById<View>(R.id.barra_fortaleza_bg)
        val barraFortaleza  = findViewById<View>(R.id.barra_fortaleza)
        val txtFortalezaLbl = findViewById<TextView>(R.id.txt_fortaleza_label)
        val txtFortalezaVal = findViewById<TextView>(R.id.txt_fortaleza_valor)
        barraFondo.post {
            aplicarFortaleza(barraFortaleza, barraFondo, txtFortalezaLbl, txtFortalezaVal, calcularFortaleza(contrasena))
        }

        findViewById<ImageView>(R.id.btn_copiar_usuario).setOnClickListener { copyToClipboard(usuario) }
        findViewById<ImageView>(R.id.btn_copiar_contrasena).setOnClickListener { copyToClipboard(contrasena) }
        findViewById<ImageView>(R.id.btn_back_detalles).setOnClickListener { finish() }

        // Botón Editar
        findViewById<TextView>(R.id.btn_editar).setOnClickListener {
            val editIntent = Intent(this, EditarContrasena::class.java).apply {
                putExtra("nombre",        nombre)
                putExtra("usuario",       usuario)
                putExtra("contrasena",    contrasena)
                putExtra("icono",         icono)
                putExtra("iconName",      iconName)
                putExtra("fechaCreacion", fechaCreacion)
            }
            @Suppress("DEPRECATION")
            startActivityForResult(editIntent, REQUEST_EDIT)
        }

        // Botón Eliminar
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_eliminar)
            .setOnClickListener {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Eliminar contraseña")
                    .setMessage("¿Seguro que quieres eliminar la contraseña de $nombre?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Eliminar") { _, _ ->
                        PasswordRepository.delete(this, nombre, usuario)
                        Toast.makeText(this, "Contraseña eliminada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .show()
            }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_detalles)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { finish(); true }
                R.id.nav_key -> {
                    val anim = NavigationAnimationHelper.getAnimForDirection(
                        NavigationAnimationHelper.POS_BAUL, NavigationAnimationHelper.POS_GENERADOR)
                    startActivity(Intent(this, generador_contra::class.java))
                    overridePendingTransition(anim.enter, anim.exit)
                    true
                }
                R.id.nav_settings -> {
                    val anim = NavigationAnimationHelper.getAnimForDirection(
                        NavigationAnimationHelper.POS_BAUL, NavigationAnimationHelper.POS_CONFIGURACION)
                    startActivity(Intent(this, Configuracion::class.java))
                    overridePendingTransition(anim.enter, anim.exit)
                    true
                }
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_detalles)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val lp = v.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            lp.height = (70 * resources.displayMetrics.density).toInt() + systemBars.bottom
            v.layoutParams = lp
            insets
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            finish()
        }
    }

    private enum class Strength { DEBIL, MEDIA, FUERTE }

    private fun calcularFortaleza(pwd: String): Strength {
        if (pwd.length < 6) return Strength.DEBIL
        var score = 0
        if (pwd.length >= 10) score++
        if (pwd.any { it.isUpperCase() }) score++
        if (pwd.any { it.isDigit() }) score++
        if (pwd.any { !it.isLetterOrDigit() }) score++
        return when {
            score >= 3 -> Strength.FUERTE
            score >= 2 -> Strength.MEDIA
            else       -> Strength.DEBIL
        }
    }

    private fun aplicarFortaleza(barra: View, fondo: View, label: TextView, valor: TextView, strength: Strength) {
        val colorHex: String; val labelStr: String; val valorStr: String; val fraction: Float
        when (strength) {
            Strength.DEBIL  -> { colorHex = "#FF5722"; labelStr = "Contraseña débil";   valorStr = "Débil";   fraction = 0.33f }
            Strength.MEDIA  -> { colorHex = "#FFA726"; labelStr = "Contraseña regular"; valorStr = "Regular"; fraction = 0.66f }
            Strength.FUERTE -> { colorHex = "#2547F4"; labelStr = "Contraseña fuerte";  valorStr = "Fuerte";  fraction = 1.0f  }
        }
        val lp = barra.layoutParams
        lp.width = (fondo.width * fraction).toInt().coerceAtLeast(8)
        barra.layoutParams = lp
        barra.setBackgroundColor(Color.parseColor(colorHex))
        label.text = labelStr
        valor.text = valorStr
        valor.setTextColor(Color.parseColor(colorHex))
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("dato", text))
        Toast.makeText(this, "Copiado", Toast.LENGTH_SHORT).show()
    }

    private fun formatRelativeTime(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / 60_000; val hours = diff / 3_600_000; val days = diff / 86_400_000
        return when {
            minutes < 1  -> "Justo ahora"
            minutes < 60 -> "Hace $minutes min"
            hours < 24   -> "Hace $hours h"
            days == 1L   -> "Ayer"
            days < 30    -> "Hace $days días"
            days < 365   -> "Hace ${days / 30} meses"
            else         -> "Hace ${days / 365} años"
        }
    }
}
