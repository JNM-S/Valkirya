package com.example.valkyria

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : BaseActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Aplicar modo claro/oscuro guardado (default: claro)
        val appPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = appPrefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else        AppCompatDelegate.MODE_NIGHT_NO
        )

        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val logueado = prefs.getBoolean("logueado", false)
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    val intent = Intent(this@MainActivity, Baul_contrasenas::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@MainActivity, "Huella incorrecta", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación biométrica")
            .setSubtitle("Usa tu huella para ingresar")
            .setNegativeButtonText("Cancelar")
            .build()

        if (logueado) {
            val biometricManager = BiometricManager.from(this)
            val biometricEnabled = appPrefs.getBoolean("biometric_enabled", true)

            if (biometricEnabled &&
                biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                == BiometricManager.BIOMETRIC_SUCCESS) {

                biometricPrompt.authenticate(promptInfo)
            }
        }

        val email = findViewById<TextInputEditText>(R.id.entrada_email_edit)
        val password = findViewById<TextInputEditText>(R.id.entrada_contrasena_edit)
        val boton = findViewById<MaterialButton>(R.id.btn_ingresar)
        val btnGoogle = findViewById<MaterialButton>(R.id.btn_google)
        val progressLogin = findViewById<android.widget.ProgressBar>(R.id.progress_login)

        val layoutEmail = findViewById<TextInputLayout>(R.id.layout_email)
        val layoutPassword = findViewById<TextInputLayout>(R.id.layout_contrasena)

        fun setLoading(loading: Boolean) {
            if (loading) {
                boton.text = ""
                boton.isEnabled = false
                btnGoogle.isEnabled = false
                progressLogin.visibility = android.view.View.VISIBLE
            } else {
                boton.text = "Ingresar"
                boton.isEnabled = true
                btnGoogle.isEnabled = true
                progressLogin.visibility = android.view.View.GONE
            }
        }


        email.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                val texto = s.toString().trim()

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(texto).matches()) {
                    boton.text = "Ingresar"
                } else {
                    boton.text = "Ingresar"
                }

                when {
                    texto.isEmpty() -> {
                        layoutEmail.error = null
                    }

                    !texto.contains("@") -> {
                        layoutEmail.error = "Debe contener @"
                    }

                    !android.util.Patterns.EMAIL_ADDRESS.matcher(texto).matches() -> {
                        layoutEmail.error = "Correo inválido"
                    }

                    else -> {
                        layoutEmail.error = null
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnGoogle.setOnClickListener {

            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        boton.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passText = password.text.toString().trim()

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                layoutEmail.error = "Correo inválido"
                return@setOnClickListener
            }

            if (passText.isEmpty()) {
                layoutPassword.error = "Ingresa tu contraseña"
                return@setOnClickListener
            }

            // Login con Firebase Auth — primero verificar si el usuario existe
            setLoading(true)
            auth.fetchSignInMethodsForEmail(emailText)
                .addOnCompleteListener(this) { fetchTask ->
                    val methods = fetchTask.result?.signInMethods
                    if (fetchTask.isSuccessful && (methods == null || methods.isEmpty())) {
                        // El usuario no existe
                        setLoading(false)
                        Toast.makeText(this, "La cuenta no existe. Regístrate para continuar.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, crearCuenta::class.java)
                        intent.putExtra("email", emailText)
                        startActivity(intent)
                    } else {
                        // El usuario existe, intentar login
                        auth.signInWithEmailAndPassword(emailText, passText)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    // Verificar 2FA desde Firestore
                                    val uid = user?.uid
                                    if (uid != null) {
                                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                            .collection("users").document(uid).get()
                                            .addOnSuccessListener { doc ->
                                                val is2FAEnabled = doc.getBoolean("2fa_enabled") ?: false

                                                if (is2FAEnabled) {
                                                    // 2FA activo — ir a pantalla de código
                                                    startActivity(Intent(this, Verificacion2FA::class.java))
                                                } else {
                                                    getSharedPreferences("sesion", MODE_PRIVATE)
                                                        .edit()
                                                        .putBoolean("logueado", true)
                                                        .apply()
                                                    SessionManager.registerSession(this)
                                                    startActivity(Intent(this, Baul_contrasenas::class.java))
                                                    finish()
                                                }
                                            }
                                            .addOnFailureListener {
                                                // Si falla Firestore, dejar pasar
                                                getSharedPreferences("sesion", MODE_PRIVATE)
                                                    .edit()
                                                    .putBoolean("logueado", true)
                                                    .apply()
                                                SessionManager.registerSession(this)
                                                startActivity(Intent(this, Baul_contrasenas::class.java))
                                                finish()
                                            }
                                    } else {
                                        getSharedPreferences("sesion", MODE_PRIVATE)
                                            .edit()
                                            .putBoolean("logueado", true)
                                            .apply()
                                        SessionManager.registerSession(this)
                                        startActivity(Intent(this, Baul_contrasenas::class.java))
                                        finish()
                                    }
                                } else {
                                    setLoading(false)
                                    layoutPassword.error = "Contraseña incorrecta"
                                }
                            }
                    }
                }
        }
        val btnBiometria = findViewById<ConstraintLayout>(R.id.btn_biometria)
        val biometricEnabled = appPrefs.getBoolean("biometric_enabled", true)

        if (!biometricEnabled) {
            btnBiometria.visibility = android.view.View.GONE
        } else {
            btnBiometria.setOnClickListener {
                val logueado = prefs.getBoolean("logueado", false)

                if (!logueado) {
                    Toast.makeText(this, "Primero inicia sesión", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val biometricManager = BiometricManager.from(this)

                if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    == BiometricManager.BIOMETRIC_SUCCESS) {
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    Toast.makeText(this, "Biometría no disponible", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val olvido = findViewById<TextView>(R.id.txt_olvido_contraseña)

        olvido.setOnClickListener {
            startActivity(Intent(this, Recuperacion_contrasenas::class.java))
        }

        val texto = findViewById<TextView>(R.id.txt_registro)

        texto.setOnClickListener {
            startActivity(Intent(this, crearCuenta::class.java))
        }
        val textoCompleto = "¿Nuevo en Valkyria? Crea una cuenta"
        val spannable = SpannableString(textoCompleto)

        val inicio = textoCompleto.indexOf("Crea una cuenta")
        val fin = inicio + "Crea una cuenta".length

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.azul_1)),
            inicio,
            fin,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        texto.text = spannable
    }
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {

                val account = task.getResult(ApiException::class.java)

                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { authTask ->

                        if (authTask.isSuccessful) {

                            val user = auth.currentUser

                            // Guardar perfil en Firestore
                            PasswordRepository.saveProfile(
                                user?.displayName ?: "",
                                user?.email ?: "",
                                "",
                                "+57"
                            )

                            getSharedPreferences("sesion", MODE_PRIVATE)
                                .edit()
                                .putBoolean("logueado", true)
                                .apply()

                            SessionManager.registerSession(this)

                            Toast.makeText(
                                this,
                                "Bienvenido ${user?.displayName}",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this, Baul_contrasenas::class.java))
                            finish()

                        } else {

                            Toast.makeText(
                                this,
                                "Error de autenticación",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            } catch (e: ApiException) {

                Toast.makeText(
                    this,
                    "Error: ${e.statusCode}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
}
