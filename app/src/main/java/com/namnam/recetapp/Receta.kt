package com.namnam.recetapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Locale

@Entity(tableName = "recetas_usuario")
@TypeConverters(Converters::class)
data class Receta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val tipo: String, // Comida, Postre
    val dificultad: String, // Fácil, Media, Difícil

    // --- ¡CAMBIOS GRANDES AQUÍ! ---
    // 1. 'tiempoPreparacion' se divide en dos campos
    val tiempoValor: Int,
    val tiempoUnidad: String, // "Minutos", "Horas", "Días"

    // 2. 'ingredientes' ahora usa nuestra nueva data class
    val ingredientes: List<IngredienteItem>,

    // 3. 'pasos' sigue siendo una lista de strings
    val pasos: List<String>,

    val creatorId: String = "USUARIO_LOCAL",

    // Mantenemos esto solo para la imagen por defecto
    val imagenResId: Int = android.R.drawable.ic_menu_gallery
) {

    // Función útil para mostrar el tiempo
    fun getTiempoDisplay(): String {
        return "$tiempoValor ${tiempoUnidad.lowercase(Locale.ROOT)}"
    }
}