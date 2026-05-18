package com.example.valkyria

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.biometric.BiometricManager
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class Configuracion : BaseActivity() {

    private var isInitializing = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configuracion)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.selectedItemId = R.id.nav_settings

        // ── Dispositivos activos ───────────────────────────────────────────
        val containerDispositivos = findViewById<android.widget.LinearLayout>(R.id.container_dispositivos)
        val currentSessionId = SessionManager.getSessionId(this)

        SessionManager.loadSessions { sessions ->
            containerDispositivos.removeAllViews()
            for (session in sessions) {
                val deviceName = session["deviceName"] as? String ?: "Dispositivo desconocido"
                val androidVersion = session["androidVersion"] as? String ?: ""
                val sessionId = session["sessionId"] as? String ?: ""
                val isCurrentDevice = sessionId == currentSessionId

                val card = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                    background = resources.getDrawable(R.drawable.bg_card, theme)
                    setPadding(
                        (12 * resources.displayMetrics.density).toInt(),
                        (12 * resources.displayMetrics.density).toInt(),
                        (12 * resources.displayMetrics.density).toInt(),
                        (12 * resources.displayMetrics.density).toInt()
                    )
                    val lp = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.bottomMargin = (8 * resources.displayMetrics.density).toInt()
                    layoutParams = lp
                }

                val icon = android.widget.ImageView(this).apply {
                    val size = (20 * resources.displayMetrics.density).toInt()
                    layoutParams = android.widget.LinearLayout.LayoutParams(size, size).apply {
                        marginEnd = (10 * resources.displayMetrics.density).toInt()
                    }
                    setImageResource(R.drawable.disp_cel)
                }

                val textContainer = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val txtName = android.widget.TextView(this).apply {
                    text = if (isCurrentDevice) "$deviceName (Este dispositivo)" else deviceName
                    setTextColor(resources.getColor(R.color.text_primary, theme))
                    typeface = resources.getFont(R.font.inter_font)
                }

                val txtStatus = android.widget.TextView(this).apply {
                    text = if (isCurrentDevice) "$androidVersion • Activo ahora" else androidVersion
                    setTextColor(resources.getColor(R.color.text_secondary, theme))
                    typeface = resources.getFont(R.font.inter_font)
                    textSize = 12f
                }

                textContainer.addView(txtName)
                textContainer.addView(txtStatus)
                card.addView(icon)
                card.addView(textContainer)
                containerDispositivos.addView(card)
            }

            // Si no hay sesiones, mostrar al menos el dispositivo actual
            if (sessions.isEmpty()) {
                val nombreDispositivo = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} (Este dispositivo)"
                val card = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                    background = resources.getDrawable(R.drawable.bg_card, theme)
                    setPadding(
                        (12 * resources.displayMetrics.density).toInt(),
                        (12 * resources.displayMetrics.density).toInt(),
                        (12 * resources.displayMetrics.density).toInt(),
                        (12 * resources.displayMetrics.density).toInt()
                    )
                }
                val icon = android.widget.ImageView(this).apply {
                    val size = (20 * resources.displayMetrics.density).toInt()
                    layoutParams = android.widget.LinearLayout.LayoutParams(size, size).apply {
                        marginEnd = (10 * resources.displayMetrics.density).toInt()
                    }
                    setImageResource(R.drawable.disp_cel)
                }
                val textContainer = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }
                val txtName = android.widget.TextView(this).apply {
                    text = nombreDispositivo
                    setTextColor(resources.getColor(R.color.text_primary, theme))
                    typeface = resources.getFont(R.font.inter_font)
                }
                val txtStatus = android.widget.TextView(this).apply {
                    text = "Android ${android.os.Build.VERSION.RELEASE} • Activo ahora"
                    setTextColor(resources.getColor(R.color.text_secondary, theme))
                    typeface = resources.getFont(R.font.inter_font)
                    textSize = 12f
                }
                textContainer.addView(txtName)
                textContainer.addView(txtStatus)
                card.addView(icon)
                card.addView(textContainer)
                containerDispositivos.addView(card)
            }
        }

        // ── Botón salir de todos los dispositivos ──────────────────────────
        findViewById<android.widget.TextView>(R.id.salir).setOnClickListener {
            com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar otras sesiones")
                .setMessage("¿Deseas cerrar sesión en todos los demás dispositivos?")
                .setPositiveButton("Cerrar sesiones") { _, _ ->
                    SessionManager.closeAllOtherSessions(this) {
                        android.widget.Toast.makeText(this, "Sesiones cerradas en otros dispositivos", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // ── Navegación tarjetas ────────────────────────────────────────────
        findViewById<android.view.View>(R.id.card).setOnClickListener {
            startActivity(Intent(this, PerfilUsuario::class.java))
        }

        findViewById<android.view.View>(R.id.card2).setOnClickListener {
            startActivity(Intent(this, CambioContrasena::class.java))
        }

        findViewById<android.view.View>(R.id.card4).setOnClickListener {
            startActivity(Intent(this, AutoLock::class.java))
        }

        // ── Switch Modo Oscuro ─────────────────────────────────────────────
        val switchModoOscuro = findViewById<SwitchMaterial>(R.id.switch3)
        isInitializing = true
        switchModoOscuro.isChecked = prefs.getBoolean("dark_mode", false)
        isInitializing = false

        switchModoOscuro.setOnCheckedChangeListener { _, checked ->
            if (isInitializing) return@setOnCheckedChangeListener
            prefs.edit().putBoolean("dark_mode", checked).apply()
            val mode = if (checked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
            delegate.localNightMode = mode
        }

        // ── Switch Desbloqueo Biométrico ───────────────────────────────────
        val switchBiometrico = findViewById<SwitchMaterial>(R.id.switch2)
        val biometricManager = BiometricManager.from(this)
        val biometriaDisponible = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS

        if (!biometriaDisponible) {
            switchBiometrico.isEnabled = false
            switchBiometrico.isChecked = false
        } else {
            isInitializing = true
            // Activo por defecto (true si nunca se ha guardado)
            switchBiometrico.isChecked = prefs.getBoolean("biometric_enabled", true)
            isInitializing = false

            switchBiometrico.setOnCheckedChangeListener { _, checked ->
                if (isInitializing) return@setOnCheckedChangeListener
                prefs.edit().putBoolean("biometric_enabled", checked).apply()
            }
        }

        // ── Switch 2FA (verificación de email al login) ────────────────────
        val switch2FA = findViewById<SwitchMaterial>(R.id.switch1)
        isInitializing = true
        switch2FA.isChecked = prefs.getBoolean("2fa_enabled", false)
        isInitializing = false

        switch2FA.setOnCheckedChangeListener { _, checked ->
            if (isInitializing) return@setOnCheckedChangeListener
            prefs.edit().putBoolean("2fa_enabled", checked).apply()
            // Guardar también en Firestore para que persista entre dispositivos
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .update("2fa_enabled", checked)
            }
            val msg = if (checked) "2FA activado: se verificará tu email al iniciar sesión"
                      else "2FA desactivado"
            android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
        }

        // ── Bottom Navigation ──────────────────────────────────────────────
        bottomNav.setOnItemSelectedListener { item ->
            val fromPos = NavigationAnimationHelper.POS_CONFIGURACION
            when (item.itemId) {
                R.id.nav_home -> {
                    val anim = NavigationAnimationHelper.getAnimForDirection(fromPos, NavigationAnimationHelper.POS_BAUL)
                    startActivity(Intent(this, Baul_contrasenas::class.java))
                    overridePendingTransition(anim.enter, anim.exit)
                    true
                }
                R.id.nav_key -> {
                    val anim = NavigationAnimationHelper.getAnimForDirection(fromPos, NavigationAnimationHelper.POS_GENERADOR)
                    startActivity(Intent(this, generador_contra::class.java))
                    overridePendingTransition(anim.enter, anim.exit)
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                com.google.android.material.dialog.MaterialAlertDialogBuilder(this@Configuracion)
                    .setTitle("Salir de la App")
                    .setMessage("¿Deseas salir de la App?")
                    .setPositiveButton("Salir") { _, _ -> finishAffinity() }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        })
    }
}
