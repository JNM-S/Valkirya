package com.example.valkyria

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Config : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNav.selectedItemId = R.id.nav_settings
        bottomNav.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, BaulContrasenas::class.java))
                    true
                }

                R.id.nav_key -> {
                    startActivity(Intent(this, generador_contra::class.java))
                    true
                }

                R.id.nav_settings -> {
                    true // ya estás aquí
                }

                else -> false
            }
        }
    }
}