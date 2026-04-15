package com.example.valkyria

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        // Aplica el modo guardado antes de inflar el layout
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        delegate.localNightMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                                  else        AppCompatDelegate.MODE_NIGHT_NO
        super.onCreate(savedInstanceState)
    }
}
