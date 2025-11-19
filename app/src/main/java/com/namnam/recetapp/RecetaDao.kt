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

    // Obtener todas las recetas del usuario (privadas y públicas propias)
    @Query("SELECT * FROM recetas_usuario WHERE usuarioId = :userId ORDER BY fechaCreacion DESC")
    fun obtenerRecetasDelUsuario(userId: Int): Flow<List<Receta>>

    // Obtener solo recetas PÚBLICAS para explorar (de todos los usuarios)
    @Query("SELECT * FROM recetas_usuario WHERE esPrivada = 0 ORDER BY fechaCreacion DESC")
    fun obtenerRecetasPublicas(): Flow<List<Receta>>

    // ✅ NUEVO: Contar cuántas recetas tiene un usuario
    @Query("SELECT COUNT(*) FROM recetas_usuario WHERE usuarioId = :userId")
    suspend fun contarRecetasDelUsuario(userId: Int): Int

    // ✅ NUEVO: Eliminar TODAS las recetas de un usuario (para testing)
    @Query("DELETE FROM recetas_usuario WHERE usuarioId = :userId")
    suspend fun eliminarTodasLasRecetasDelUsuario(userId: Int)

    // ✅ NUEVO: Obtener todas las recetas (sin Flow, para debug)
    @Query("SELECT * FROM recetas_usuario")
    suspend fun obtenerTodasLasRecetas(): List<Receta>
}