package com.example.valkyria

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent

open class BaseActivity : AppCompatActivity() {

    companion object {
        var ultimaInteraccion: Long = System.currentTimeMillis()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplica el modo guardado antes de inflar el layout
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        delegate.localNightMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                                  else        AppCompatDelegate.MODE_NIGHT_NO
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        // No verificar en MainActivity ni en AutoLock
        if (this is MainActivity || this is AutoLock) return

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val autoLock = prefs.getString("auto_lock", "nunca")
        if (autoLock == "nunca") return

        val limiteMs = when (autoLock) {
            "1min"  -> 1 * 60 * 1000L
            "3min"  -> 3 * 60 * 1000L
            "5min"  -> 5 * 60 * 1000L
            "10min" -> 10 * 60 * 1000L
            else    -> return
        }

        val tiempoInactivo = System.currentTimeMillis() - ultimaInteraccion
        if (tiempoInactivo >= limiteMs) {
            // Marcar como deslogueado y volver al login
            getSharedPreferences("sesion", MODE_PRIVATE).edit()
                .putBoolean("logueado", false)
                .apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun dispatchTouchEvent(ev: android.view.MotionEvent?): Boolean {
        // Actualizar tiempo de última interacción en cualquier toque
        ultimaInteraccion = System.currentTimeMillis()
        return super.dispatchTouchEvent(ev)
    }
}
