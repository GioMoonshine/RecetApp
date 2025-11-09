package com.namnam.recetapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update // ¡NUEVA IMPORTACIÓN!
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {

    @Insert
    suspend fun insertarReceta(receta: Receta)

    // ¡¡NUEVA FUNCIÓN!!
    @Update
    suspend fun actualizarReceta(receta: Receta)

    @Delete
    suspend fun borrarReceta(receta: Receta)

    @Query("SELECT * FROM recetas_usuario ORDER BY nombre ASC")
    fun obtenerMisRecetas(): Flow<List<Receta>>

}