package com.example.valkyria

import android.content.Intent
import android.os.Bundle
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class Configuracion : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configuracion)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.selectedItemId = R.id.nav_settings

        // ── Dispositivo real ───────────────────────────────────────────────
        val nombreDispositivo = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} (Este dispositivo)"
        val esTablet = resources.configuration.smallestScreenWidthDp >= 600
        val iconoDispositivo = if (esTablet) R.drawable.disp_pc else R.drawable.disp_cel

        findViewById<android.widget.ImageView>(R.id.img_dispositivo).setImageResource(iconoDispositivo)
        findViewById<android.widget.TextView>(R.id.txt_nombre_dispositivo).text = nombreDispositivo
        findViewById<android.widget.TextView>(R.id.txt_estado_dispositivo).text =
            "Android ${android.os.Build.VERSION.RELEASE} • Activo ahora"

        // ── Switch Modo Oscuro ─────────────────────────────────────────────
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        val switchModoOscuro = findViewById<SwitchMaterial>(R.id.switch3)
        switchModoOscuro.isChecked = isDark

        switchModoOscuro.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("dark_mode", checked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES
                else         AppCompatDelegate.MODE_NIGHT_NO
            )
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
    }
}
