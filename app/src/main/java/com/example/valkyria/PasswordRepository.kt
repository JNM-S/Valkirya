package com.example.valkyria

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object PasswordRepository {

    val ICON_MAP = mapOf(
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

    private fun getUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid

    private fun passwordsCollection() =
        getUserId()?.let { uid ->
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("passwords")
        }

    fun save(context: Context, item: PasswordItem, iconName: String) {
        val collection = passwordsCollection() ?: return
        val now = System.currentTimeMillis()
        val data = hashMapOf(
            "nombre"            to item.nombre,
            "usuario"           to item.usuario,
            "contrasena"        to item.contrasena,
            "iconName"          to iconName,
            "fechaCreacion"     to now,
            "fechaModificacion" to now
        )
        collection.add(data)
    }

    fun update(context: Context, originalNombre: String, originalUsuario: String, item: PasswordItem, iconName: String) {
        val collection = passwordsCollection() ?: return
        collection
            .whereEqualTo("nombre", originalNombre)
            .whereEqualTo("usuario", originalUsuario)
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    doc.reference.update(
                        mapOf(
                            "nombre"            to item.nombre,
                            "usuario"           to item.usuario,
                            "contrasena"        to item.contrasena,
                            "iconName"          to iconName,
                            "fechaModificacion" to System.currentTimeMillis()
                        )
                    )
                }
            }
    }

    fun delete(context: Context, nombre: String, usuario: String) {
        val collection = passwordsCollection() ?: return
        collection
            .whereEqualTo("nombre", nombre)
            .whereEqualTo("usuario", usuario)
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    doc.reference.delete()
                }
            }
    }

    /**
     * Carga las contraseñas de forma asíncrona.
     * Usa el callback para recibir la lista cuando esté lista.
     */
    fun load(context: Context, callback: (List<PasswordItem>) -> Unit) {
        val collection = passwordsCollection()
        if (collection == null) {
            callback(emptyList())
            return
        }
        collection
            .orderBy("fechaModificacion", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { docs ->
                val result = docs.map { doc ->
                    val iconName = doc.getString("iconName") ?: "ic_service_placeholder"
                    PasswordItem(
                        nombre            = doc.getString("nombre") ?: "",
                        usuario           = doc.getString("usuario") ?: "",
                        contrasena        = doc.getString("contrasena") ?: "",
                        icono             = ICON_MAP[iconName] ?: R.drawable.ic_service_placeholder,
                        fechaCreacion     = doc.getLong("fechaCreacion") ?: System.currentTimeMillis(),
                        fechaModificacion = doc.getLong("fechaModificacion") ?: System.currentTimeMillis(),
                        iconName          = iconName
                    )
                }
                callback(result)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    /**
     * Versión síncrona para compatibilidad — carga desde caché local de Firestore.
     * Preferir la versión con callback.
     */
    fun load(context: Context): List<PasswordItem> {
        // Retorna lista vacía — las Activities deben migrar a la versión con callback
        return emptyList()
    }

    /**
     * Guarda el perfil del usuario en Firestore.
     */
    fun saveProfile(nombre: String, correo: String, telefono: String, prefijo: String) {
        val uid = getUserId() ?: return
        val data = hashMapOf(
            "nombre"   to nombre,
            "correo"   to correo,
            "telefono" to telefono,
            "prefijo"  to prefijo
        )
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
    }

    /**
     * Carga el perfil del usuario desde Firestore.
     */
    fun loadProfile(callback: (nombre: String, correo: String, telefono: String, prefijo: String) -> Unit) {
        val uid = getUserId() ?: run {
            callback("", "", "", "+57")
            return
        }
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                callback(
                    doc.getString("nombre") ?: "",
                    doc.getString("correo") ?: "",
                    doc.getString("telefono") ?: "",
                    doc.getString("prefijo") ?: "+57"
                )
            }
            .addOnFailureListener {
                callback("", "", "", "+57")
            }
    }
}
