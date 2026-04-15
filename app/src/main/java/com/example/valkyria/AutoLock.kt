package com.example.valkyria

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AutoLock : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auto_lock)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<android.widget.ImageView>(R.id.back).setOnClickListener {
            finish()
        }

        // Cargar selección guardada
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val seleccion = prefs.getString("auto_lock", "nunca")

        val radioGroup = findViewById<android.widget.RadioGroup>(R.id.radio_group_autolock)
        when (seleccion) {
            "1min"  -> radioGroup.check(R.id.radio_1min)
            "3min"  -> radioGroup.check(R.id.radio_3min)
            "5min"  -> radioGroup.check(R.id.radio_5min)
            "10min" -> radioGroup.check(R.id.radio_10min)
            else    -> radioGroup.check(R.id.radio_nunca)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val valor = when (checkedId) {
                R.id.radio_1min  -> "1min"
                R.id.radio_3min  -> "3min"
                R.id.radio_5min  -> "5min"
                R.id.radio_10min -> "10min"
                else             -> "nunca"
            }
            prefs.edit().putString("auto_lock", valor).apply()
        }
    }
}
