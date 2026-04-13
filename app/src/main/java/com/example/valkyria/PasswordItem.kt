package com.example.valkyria

data class PasswordItem(
    val nombre: String,
    val usuario: String,
    val icono: Int,
    val contrasena: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaModificacion: Long = System.currentTimeMillis(),
    val iconName: String = "ic_service_placeholder"
)
