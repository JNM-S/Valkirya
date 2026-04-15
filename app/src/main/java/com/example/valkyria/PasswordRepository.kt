package com.example.valkyria

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object PasswordRepository {

    private const val KEY_PASSWORDS = "saved_passwords"

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

    // Clave de prefs única por usuario (basada en su correo)
    private fun prefsName(context: Context): String {
        val correo = context.getSharedPreferences("usuarios", Context.MODE_PRIVATE)
            .getString("correo_usuario", "default") ?: "default"
        return "vault_${correo.replace("@", "_").replace(".", "_")}"
    }

    fun save(context: Context, item: PasswordItem, iconName: String) {
        val prefs = context.getSharedPreferences(prefsName(context), Context.MODE_PRIVATE)
        val array = loadJsonArray(prefs)
        val now = System.currentTimeMillis()
        array.put(JSONObject().apply {
            put("nombre",            item.nombre)
            put("usuario",           item.usuario)
            put("contrasena",        item.contrasena)
            put("iconName",          iconName)
            put("fechaCreacion",     now)
            put("fechaModificacion", now)
        })
        prefs.edit().putString(KEY_PASSWORDS, array.toString()).apply()
    }

    fun update(context: Context, originalNombre: String, originalUsuario: String, item: PasswordItem, iconName: String) {
        val prefs = context.getSharedPreferences(prefsName(context), Context.MODE_PRIVATE)
        val array = loadJsonArray(prefs)
        val newArray = JSONArray()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            if (obj.getString("nombre") == originalNombre && obj.getString("usuario") == originalUsuario) {
                newArray.put(JSONObject().apply {
                    put("nombre",            item.nombre)
                    put("usuario",           item.usuario)
                    put("contrasena",        item.contrasena)
                    put("iconName",          iconName)
                    put("fechaCreacion",     obj.optLong("fechaCreacion", System.currentTimeMillis()))
                    put("fechaModificacion", System.currentTimeMillis())
                })
            } else {
                newArray.put(obj)
            }
        }
        prefs.edit().putString(KEY_PASSWORDS, newArray.toString()).apply()
    }

    fun delete(context: Context, nombre: String, usuario: String) {
        val prefs = context.getSharedPreferences(prefsName(context), Context.MODE_PRIVATE)
        val array = loadJsonArray(prefs)
        val newArray = JSONArray()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            if (obj.getString("nombre") != nombre || obj.getString("usuario") != usuario) {
                newArray.put(obj)
            }
        }
        prefs.edit().putString(KEY_PASSWORDS, newArray.toString()).apply()
    }

    fun load(context: Context): List<PasswordItem> {
        val prefs = context.getSharedPreferences(prefsName(context), Context.MODE_PRIVATE)
        val array = loadJsonArray(prefs)
        val result = mutableListOf<PasswordItem>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val iconName = obj.optString("iconName", "ic_service_placeholder")
            result.add(
                PasswordItem(
                    nombre            = obj.getString("nombre"),
                    usuario           = obj.getString("usuario"),
                    contrasena        = obj.optString("contrasena", ""),
                    icono             = ICON_MAP[iconName] ?: R.drawable.ic_service_placeholder,
                    fechaCreacion     = obj.optLong("fechaCreacion", System.currentTimeMillis()),
                    fechaModificacion = obj.optLong("fechaModificacion", System.currentTimeMillis()),
                    iconName          = iconName
                )
            )
        }
        return result
    }

    private fun loadJsonArray(prefs: android.content.SharedPreferences): JSONArray {
        val raw = prefs.getString(KEY_PASSWORDS, null) ?: return JSONArray()
        return try { JSONArray(raw) } catch (e: Exception) { JSONArray() }
    }
}
