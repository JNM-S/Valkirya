package com.example.valkyria

import androidx.annotation.AnimRes

object NavigationAnimationHelper {

    const val POS_BAUL = 0
    const val POS_GENERADOR = 1
    const val POS_CONFIGURACION = 2

    data class AnimPair(
        @AnimRes val enter: Int,
        @AnimRes val exit: Int
    )

    fun getAnimForDirection(fromPos: Int, toPos: Int): AnimPair {
        return if (toPos > fromPos) {
            // Navegar a la derecha: nueva pantalla entra desde derecha, actual sale a la izquierda
            AnimPair(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            // Navegar a la izquierda: nueva pantalla entra desde izquierda, actual sale a la derecha
            AnimPair(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}
