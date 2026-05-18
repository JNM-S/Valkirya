package com.example.valkyria

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

object SessionManager {

    private const val PREF_NAME = "session_prefs"
    private const val KEY_SESSION_ID = "session_id"

    /**
     * Obtiene o crea un ID único para esta sesión/dispositivo.
     */
    fun getSessionId(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var sessionId = prefs.getString(KEY_SESSION_ID, null)
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_SESSION_ID, sessionId).apply()
        }
        return sessionId
    }

    /**
     * Registra este dispositivo como sesión activa en Firestore.
     */
    fun registerSession(context: Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val sessionId = getSessionId(context)
        val deviceName = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
        val androidVersion = "Android ${android.os.Build.VERSION.RELEASE}"

        val data = hashMapOf(
            "sessionId"  to sessionId,
            "deviceName" to deviceName,
            "androidVersion" to androidVersion,
            "lastActive" to System.currentTimeMillis(),
            "active"     to true
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("sessions")
            .document(sessionId)
            .set(data)
    }

    /**
     * Actualiza el timestamp de última actividad.
     */
    fun updateLastActive(context: Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val sessionId = getSessionId(context)

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("sessions")
            .document(sessionId)
            .update("lastActive", System.currentTimeMillis())
    }

    /**
     * Carga todas las sesiones activas del usuario.
     */
    fun loadSessions(callback: (List<Map<String, Any>>) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            callback(emptyList())
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("sessions")
            .whereEqualTo("active", true)
            .get()
            .addOnSuccessListener { docs ->
                val sessions = docs.map { it.data }
                callback(sessions)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    /**
     * Cierra todas las sesiones excepto la actual.
     */
    fun closeAllOtherSessions(context: Context, onComplete: () -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val currentSessionId = getSessionId(context)

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("sessions")
            .whereEqualTo("active", true)
            .get()
            .addOnSuccessListener { docs ->
                val batch = FirebaseFirestore.getInstance().batch()
                for (doc in docs) {
                    if (doc.getString("sessionId") != currentSessionId) {
                        batch.update(doc.reference, "active", false)
                    }
                }
                batch.commit().addOnCompleteListener { onComplete() }
            }
    }

    /**
     * Verifica si esta sesión sigue activa. Si no, cierra la app.
     */
    fun isSessionActive(context: Context, callback: (Boolean) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            callback(false)
            return
        }
        val sessionId = getSessionId(context)

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("sessions")
            .document(sessionId)
            .get()
            .addOnSuccessListener { doc ->
                val active = doc.getBoolean("active") ?: false
                callback(active)
            }
            .addOnFailureListener {
                callback(true) // En caso de error de red, no cerrar
            }
    }

    /**
     * Elimina la sesión actual (al cerrar sesión).
     */
    fun removeCurrentSession(context: Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val sessionId = getSessionId(context)

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("sessions")
            .document(sessionId)
            .update("active", false)
    }
}
