package com.example.valkyria

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditarContrasena : AppCompatActivity() {

    private var selectedIconName = "ic_service_placeholder"
    private var selectedIconRes  = R.drawable.ic_service_placeholder

    private val iconOptions = listOf(
        "ic_gmail"               to R.drawable.ic_gmail,
        "ic_netflix"             to R.drawable.ic_netflix,
        "ic_github"              to R.drawable.ic_github,
        "ic_adobe"               to R.drawable.ic_adobe,
        "ic_spotify"             to R.drawable.ic_spotify,
        "email"                  to R.drawable.email,
        "disp_cel"               to R.drawable.disp_cel,
        "disp_pc"                to R.drawable.disp_pc,
        "ic_service_placeholder" to R.drawable.ic_service_placeholder
    )
    private val iconLabels = listOf("Gmail","Netflix","GitHub","Adobe","Spotify","Email","Móvil","PC","Genérico")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_contrasena)

        // Leer datos originales del Intent
        val originalNombre    = intent.getStringExtra("nombre")     ?: ""
        val originalUsuario   = intent.getStringExtra("usuario")    ?: ""
        val originalContrasena = intent.getStringExtra("contrasena") ?: ""
        val originalIconName  = intent.getStringExtra("iconName")   ?: "ic_service_placeholder"
        val originalIconRes   = intent.getIntExtra("icono", R.drawable.ic_service_placeholder)
        val fechaCreacion     = intent.getLongExtra("fechaCreacion", System.currentTimeMillis())

        selectedIconName = originalIconName
        selectedIconRes  = originalIconRes

        // Poblar campos
        val editNombre    = findViewById<TextInputEditText>(R.id.edit_nombre)
        val editUsuario   = findViewById<TextInputEditText>(R.id.edit_usuario)
        val editContrasena = findViewById<TextInputEditText>(R.id.edit_contrasena)
        val layoutNombre  = findViewById<TextInputLayout>(R.id.layout_edit_nombre)
        val layoutUsuario = findViewById<TextInputLayout>(R.id.layout_edit_usuario)
        val btnIconPicker = findViewById<ImageView>(R.id.btn_icon_picker_editar)

        editNombre.setText(originalNombre)
        editUsuario.setText(originalUsuario)
        editContrasena.setText(originalContrasena)
        btnIconPicker.setImageResource(selectedIconRes)

        // Limpiar errores al escribir
        editNombre.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { layoutNombre.error = null }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editUsuario.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { layoutUsuario.error = null }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Selector de ícono
        btnIconPicker.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_icon_picker, null)
            val gridView = dialogView.findViewById<android.widget.GridView>(R.id.grid_icons)

            val adapter = object : android.widget.BaseAdapter() {
                override fun getCount() = iconOptions.size
                override fun getItem(pos: Int) = iconOptions[pos]
                override fun getItemId(pos: Int) = pos.toLong()
                override fun getView(pos: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                    val view = convertView ?: layoutInflater.inflate(R.layout.item_icon_picker, parent, false)
                    view.findViewById<ImageView>(R.id.icon_image).setImageResource(iconOptions[pos].second)
                    view.findViewById<android.widget.TextView>(R.id.icon_label).text = iconLabels[pos]
                    view.alpha = if (iconOptions[pos].first == selectedIconName) 1f else 0.55f
                    return view
                }
            }
            gridView.adapter = adapter

            val dialog = android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

            dialogView.findViewById<MaterialButton>(R.id.btn_cancelar).setOnClickListener { dialog.dismiss() }

            gridView.setOnItemClickListener { _, _, which, _ ->
                selectedIconName = iconOptions[which].first
                selectedIconRes  = iconOptions[which].second
                btnIconPicker.setImageResource(selectedIconRes)
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            dialog.show()
        }

        // Botón atrás
        findViewById<ImageView>(R.id.btn_back_editar).setOnClickListener { finish() }

        // Botón guardar
        findViewById<MaterialButton>(R.id.btn_guardar_editar).setOnClickListener {
            val nuevoNombre    = editNombre.text.toString().trim()
            val nuevoUsuario   = editUsuario.text.toString().trim()
            val nuevaContrasena = editContrasena.text.toString().trim()

            when {
                nuevoNombre.isEmpty() -> {
                    layoutNombre.error = "Ingresa el nombre de la app"
                    editNombre.requestFocus()
                    return@setOnClickListener
                }
                nuevoUsuario.isEmpty() -> {
                    layoutUsuario.error = "Ingresa el correo o usuario"
                    editUsuario.requestFocus()
                    return@setOnClickListener
                }
            }

            val updatedItem = PasswordItem(
                nombre            = nuevoNombre,
                usuario           = nuevoUsuario,
                contrasena        = nuevaContrasena,
                icono             = selectedIconRes,
                fechaCreacion     = fechaCreacion,
                fechaModificacion = System.currentTimeMillis()
            )

            PasswordRepository.update(this, originalNombre, originalUsuario, updatedItem, selectedIconName)
            Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }

        // Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_editar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
