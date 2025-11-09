package com.namnam.recetapp

object RecetasData {
    fun obtenerRecetas(): List<Receta> {
        // Devuelve una lista vacía.
        // Nuestros datos estáticos ('Pasta Carbonara', etc.) ya no
        // son compatibles con la nueva estructura de 'Receta.kt'
        // (que ahora pide 'tiempoValor', 'IngredienteItem', etc.).
        //
        // Como 'explorarRecetas' en el ViewModel también es una
        // lista vacía, esto hace que la app compile.
        return emptyList()
    }
}