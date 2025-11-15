package com.namnam.recetapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombreUsuario: String,
    val nombreCompleto: String,
    val email: String,
    val bio: String = "",
    val fotoPerfilUri: String? = null,
    val fechaRegistro: Long = System.currentTimeMillis()
) {
    fun getIniciales(): String {
        return nombreCompleto.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .take(2)
            .joinToString("")
    }
}