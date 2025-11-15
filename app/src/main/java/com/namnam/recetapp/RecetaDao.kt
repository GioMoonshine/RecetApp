package com.namnam.recetapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {

    @Insert
    suspend fun insertarReceta(receta: Receta)

    @Update
    suspend fun actualizarReceta(receta: Receta)

    @Delete
    suspend fun borrarReceta(receta: Receta)

    // Obtener recetas del usuario actual
    @Query("SELECT * FROM recetas_usuario WHERE usuarioId = :userId ORDER BY fechaCreacion DESC")
    fun obtenerRecetasDelUsuario(userId: Int): Flow<List<Receta>>

    // Obtener todas las recetas de todos los usuarios
    @Query("SELECT * FROM recetas_usuario ORDER BY fechaCreacion DESC")
    fun obtenerTodasLasRecetas(): Flow<List<Receta>>

    // Obtener recetas de otros usuarios (para explorar)
    @Query("SELECT * FROM recetas_usuario WHERE usuarioId != :currentUserId ORDER BY fechaCreacion DESC")
    fun obtenerRecetasDeOtrosUsuarios(currentUserId: Int): Flow<List<Receta>>
}