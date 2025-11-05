package com.namnam.recetapp

data class Receta(
    val id: Int,
    val nombre: String,
    val tipo: String, // "Comida" o "Postre"
    val imagenResId: Int, // Recurso de imagen
    val ingredientes: List<String>,
    val pasos: List<String>,
    val tiempoPreparacion: String,
    val dificultad: String
)