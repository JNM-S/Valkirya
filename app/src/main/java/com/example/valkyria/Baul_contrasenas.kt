package com.example.valkyria

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class Baul_contrasenas : AppCompatActivity() {

    private lateinit var adapter: PasswordAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var buscador: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_baul_contrasenas)

        recycler = findViewById(R.id.lista_contrasenas)
        recycler.layoutManager = LinearLayoutManager(this)

        buscador = findViewById(R.id.buscador)

        // Adapter vacío — se llena en onResume
        adapter = PasswordAdapter(emptyList()) { item ->
            val intent = Intent(this, DetallesContrasena::class.java)
            intent.putExtra("nombre",            item.nombre)
            intent.putExtra("usuario",           item.usuario)
            intent.putExtra("icono",             item.icono)
            intent.putExtra("contrasena",        item.contrasena)
            intent.putExtra("fechaCreacion",     item.fechaCreacion)
            intent.putExtra("fechaModificacion", item.fechaModificacion)
            intent.putExtra("iconName",          item.iconName)
            startActivity(intent)
        }
        recycler.adapter = adapter

        buscador.addTextChangedListener {
            adapter.filtrar(it.toString().trim())
        }

        val saludoTxt = findViewById<TextView>(R.id.txt_saludo)
        val prefs = getSharedPreferences("usuarios", MODE_PRIVATE)
        val nombre = prefs.getString("nombre_usuario", "Usuario")
        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val saludo = when {
            hora in 6..11  -> "Buenos días"
            hora in 12..18 -> "Buenas tardes"
            else           -> "Buenas noches"
        }
        saludoTxt.text = "$saludo, \n$nombre"

        val botonGenerador = findViewById<FloatingActionButton>(R.id.btn_add)
        botonGenerador.setOnClickListener {
            val anim = NavigationAnimationHelper.getAnimForDirection(
                NavigationAnimationHelper.POS_BAUL, NavigationAnimationHelper.POS_GENERADOR)
            startActivity(Intent(this, generador_contra::class.java))
            overridePendingTransition(anim.enter, anim.exit)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            val fromPos = NavigationAnimationHelper.POS_BAUL
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_key -> {
                    val anim = NavigationAnimationHelper.getAnimForDirection(fromPos, NavigationAnimationHelper.POS_GENERADOR)
                    startActivity(Intent(this, generador_contra::class.java))
                    overridePendingTransition(anim.enter, anim.exit)
                    true
                }
                R.id.nav_settings -> {
                    val anim = NavigationAnimationHelper.getAnimForDirection(fromPos, NavigationAnimationHelper.POS_CONFIGURACION)
                    startActivity(Intent(this, Configuracion::class.java))
                    overridePendingTransition(anim.enter, anim.exit)
                    true
                }
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
            override fun handleOnBackPressed() { finishAffinity() }
        })
    }

    override fun onResume() {
        super.onResume()
        // Recargar lista cada vez que la Activity vuelve al frente
        // (después de eliminar en Detalles, o de guardar desde el Generador)
        val lista = PasswordRepository.load(this)
        adapter.actualizarLista(lista)
        buscador.text?.clear()
    }
}
