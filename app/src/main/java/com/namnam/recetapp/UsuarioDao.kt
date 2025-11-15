package com.namnam.recetapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertarUsuario(usuario: Usuario): Long

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE id = :userId")
    suspend fun obtenerUsuarioPorId(userId: Int): Usuario?

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :username")
    suspend fun obtenerUsuarioPorNombre(username: String): Usuario?

    @Query("SELECT * FROM usuarios")
    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>>

    @Query("SELECT COUNT(*) FROM usuarios WHERE nombreUsuario = :username")
    suspend fun existeUsuario(username: String): Int
}