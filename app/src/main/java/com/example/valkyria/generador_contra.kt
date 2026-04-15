package com.example.valkyria

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial

class generador_contra : BaseActivity() {

    private data class GeneratorConfig(
        val length: Int = 12,
        val useUppercase: Boolean = true,
        val useLowercase: Boolean = true,
        val useNumbers: Boolean = true,
        val useSymbols: Boolean = false
    )

    private var config = GeneratorConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_generador_contra)

        val txtPassword = findViewById<TextView>(R.id.textView3)
        val seekBar = findViewById<SeekBar>(R.id.barra_long)
        val txtLength = findViewById<TextView>(R.id.num_long)
        val switchMayus = findViewById<SwitchMaterial>(R.id.switch_mayus)
        val switchMin = findViewById<SwitchMaterial>(R.id.switch_min)
        val switchNum = findViewById<SwitchMaterial>(R.id.switch_num)
        val switchSimb = findViewById<SwitchMaterial>(R.id.switch_simb)
        val btnCopy = findViewById<android.widget.ImageView>(R.id.imageView3)
        val btnRefresh = findViewById<android.widget.ImageView>(R.id.imageView2)
        val txtStrengthLabel = findViewById<TextView>(R.id.txt_strength_label)
        val strengthBar = findViewById<android.view.View>(R.id.strength_bar)

        fun updateStrength(pwd: String) {
            var score = 0
            if (pwd.length >= 10) score++
            if (pwd.any { it.isUpperCase() }) score++
            if (pwd.any { it.isDigit() }) score++
            if (pwd.any { !it.isLetterOrDigit() }) score++
            val (label, color, width) = when {
                score >= 3 -> Triple("Fuerte",  "#4CAF50", 40)
                score >= 2 -> Triple("Regular", "#FFA726", 28)
                else       -> Triple("Débil",   "#FF5722", 16)
            }
            txtStrengthLabel.text = label
            txtStrengthLabel.setTextColor(android.graphics.Color.parseColor(color))
            val lp = strengthBar.layoutParams
            lp.width = (width * resources.displayMetrics.density).toInt()
            strengthBar.layoutParams = lp
            strengthBar.setBackgroundColor(android.graphics.Color.parseColor(color))
        }

        // Inicializar switches
        switchMayus.isChecked = config.useUppercase
        switchMin.isChecked = config.useLowercase
        switchNum.isChecked = config.useNumbers
        switchSimb.isChecked = config.useSymbols

        // SeekBar: rango 8..32, valor inicial 12
        seekBar.max = 24 // 8 + 24 = 32
        seekBar.progress = config.length - 8
        txtLength.text = config.length.toString()

        // Generar contraseña inicial
        txtPassword.text = generatePassword(config)
        updateStrength(txtPassword.text.toString())

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                val length = progress + 8
                txtLength.text = length.toString()
                config = config.copy(length = length)
                txtPassword.text = generatePassword(config)
                updateStrength(txtPassword.text.toString())
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })

        fun updateSwitch(
            changed: SwitchMaterial,
            newValue: Boolean,
            updater: (Boolean) -> GeneratorConfig
        ) {
            val switches = listOf(switchMayus, switchMin, switchNum, switchSimb)
            val activeCount = switches.count { it.isChecked }
            if (!newValue && activeCount <= 1) {
                changed.isChecked = true
                Toast.makeText(this, "Debe haber al menos un tipo de carácter", Toast.LENGTH_SHORT).show()
                return
            }
            config = updater(newValue)
            txtPassword.text = generatePassword(config)
            updateStrength(txtPassword.text.toString())
        }

        switchMayus.setOnCheckedChangeListener { _, checked ->
            updateSwitch(switchMayus, checked) { config.copy(useUppercase = it) }
        }
        switchMin.setOnCheckedChangeListener { _, checked ->
            updateSwitch(switchMin, checked) { config.copy(useLowercase = it) }
        }
        switchNum.setOnCheckedChangeListener { _, checked ->
            updateSwitch(switchNum, checked) { config.copy(useNumbers = it) }
        }
        switchSimb.setOnCheckedChangeListener { _, checked ->
            updateSwitch(switchSimb, checked) { config.copy(useSymbols = it) }
        }

        btnCopy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("password", txtPassword.text))
            Toast.makeText(this, "Copiado", Toast.LENGTH_SHORT).show()
        }

        btnRefresh.setOnClickListener {
            txtPassword.text = generatePassword(config)
            updateStrength(txtPassword.text.toString())
        }

        val inputNomApp = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.input_nom_app)
        val inputCorreo = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inpput_correo)
        val layoutNomApp = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.layout_nom_app)
        val layoutCorreo = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.layout_correo)
        val btnGuardar  = findViewById<com.google.android.material.button.MaterialButton>(R.id.Guardar)

        // Limpiar error al escribir
        inputNomApp.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { layoutNomApp.error = null }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        inputCorreo.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { layoutCorreo.error = null }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // ── Selector de ícono ──────────────────────────────────────────────
        var selectedIconName = "ic_service_placeholder"
        var selectedIconRes  = R.drawable.ic_service_placeholder

        val iconOptions = listOf(
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

        // Botón de ícono — pequeño ImageView encima del campo nombre
        val btnIconPicker = findViewById<android.widget.ImageView>(R.id.btn_icon_picker)
        btnIconPicker.setImageResource(selectedIconRes)

        btnIconPicker.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_icon_picker, null)
            val gridView = dialogView.findViewById<android.widget.GridView>(R.id.grid_icons)

            val iconLabels = listOf("Gmail","Netflix","GitHub","Adobe","Spotify","Email","Móvil","PC","Genérico")

            val adapter = object : android.widget.BaseAdapter() {
                override fun getCount() = iconOptions.size
                override fun getItem(pos: Int) = iconOptions[pos]
                override fun getItemId(pos: Int) = pos.toLong()
                override fun getView(pos: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                    val view = convertView ?: layoutInflater.inflate(R.layout.item_icon_picker, parent, false)
                    view.findViewById<android.widget.ImageView>(R.id.icon_image).setImageResource(iconOptions[pos].second)
                    view.findViewById<android.widget.TextView>(R.id.icon_label).text = iconLabels[pos]
                    // Resaltar el seleccionado con borde azul
                    view.alpha = if (iconOptions[pos].first == selectedIconName) 1f else 0.55f
                    return view
                }
            }
            gridView.adapter = adapter

            val dialog = android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            dialog.window?.setBackgroundDrawable(
                android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
            )

            dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_cancelar)
                .setOnClickListener { dialog.dismiss() }

            gridView.setOnItemClickListener { _, _, which, _ ->
                selectedIconName = iconOptions[which].first
                selectedIconRes  = iconOptions[which].second
                btnIconPicker.setImageResource(selectedIconRes)
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            dialog.show()
        }

        btnGuardar.setOnClickListener {
            val nomApp = inputNomApp.text.toString().trim()
            val correo = inputCorreo.text.toString().trim()

            when {
                nomApp.isEmpty() -> {
                    layoutNomApp.error = "Ingresa el nombre de la app"
                    inputNomApp.requestFocus()
                }
                correo.isEmpty() -> {
                    layoutCorreo.error = "Ingresa el correo o usuario"
                    inputCorreo.requestFocus()
                }
                else -> {
                    val item = PasswordItem(
                        nombre     = nomApp,
                        usuario    = correo,
                        contrasena = txtPassword.text.toString(),
                        icono      = selectedIconRes
                    )
                    PasswordRepository.save(this, item, selectedIconName)
                    Toast.makeText(this, "Guardado en el Baúl", Toast.LENGTH_SHORT).show()
                    inputNomApp.text?.clear()
                    inputCorreo.text?.clear()
                    txtPassword.text = generatePassword(config)
                    selectedIconName = "ic_service_placeholder"
                    selectedIconRes  = R.drawable.ic_service_placeholder
                    btnIconPicker.setImageResource(selectedIconRes)
                    val anim = NavigationAnimationHelper.getAnimForDirection(
                        NavigationAnimationHelper.POS_GENERADOR, NavigationAnimationHelper.POS_BAUL)
                    startActivity(Intent(this, Baul_contrasenas::class.java))
                    overridePendingTransition(anim.enter, anim.exit)
                }
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.selectedItemId = R.id.nav_key

        bottomNav.setOnItemSelectedListener { item ->
            val fromPos = NavigationAnimationHelper.POS_GENERADOR
            when (item.itemId) {
                R.id.nav_home -> {
                    val anim = NavigationAnimationHelper.getAnimForDirection(fromPos, NavigationAnimationHelper.POS_BAUL)
                    startActivity(Intent(this, Baul_contrasenas::class.java))
                    overridePendingTransition(anim.enter, anim.exit)
                    true
                }
                R.id.nav_key -> true
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

        // Ajustar altura de la nav bar para incluir el inset del sistema
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = v.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            layoutParams.height = (70 * resources.displayMetrics.density).toInt() + systemBars.bottom
            v.layoutParams = layoutParams
            insets
        }
    }

    private fun generatePassword(config: GeneratorConfig): String {
        val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        val numbers = "0123456789"
        val symbols = "!@#\$%^&*()_+-=[]{}|;:,.<>?"

        val pool = buildString {
            if (config.useUppercase) append(uppercase)
            if (config.useLowercase) append(lowercase)
            if (config.useNumbers) append(numbers)
            if (config.useSymbols) append(symbols)
        }

        // Garantizar al menos un carácter de cada tipo activo
        val guaranteed = buildList {
            if (config.useUppercase) add(uppercase.random())
            if (config.useLowercase) add(lowercase.random())
            if (config.useNumbers) add(numbers.random())
            if (config.useSymbols) add(symbols.random())
        }

        val remaining = (config.length - guaranteed.size).coerceAtLeast(0)
        val rest = (1..remaining).map { pool.random() }

        return (guaranteed + rest).shuffled().joinToString("")
    }
}
