package com.namnam.recetapp

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Locale

@Entity(
    tableName = "recetas_usuario",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
@TypeConverters(Converters::class)
data class Receta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val tipo: String,
    val dificultad: String,

    val tiempoValor: Int,
    val tiempoUnidad: String,

    val ingredientes: List<IngredienteItem>,
    val pasos: List<String>,

    val usuarioId: Int,

    val fechaCreacion: Long = System.currentTimeMillis(),

    // URI de la imagen personalizada (si existe)
    val imagenUri: String? = null,

    // TRUE = privada (solo el usuario), FALSE = pública (visible en Explorar)
    val esPrivada: Boolean = false
) {
    fun getTiempoDisplay(): String {
        return "$tiempoValor ${tiempoUnidad.lowercase(Locale.ROOT)}"
    }
}