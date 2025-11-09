package com.namnam.recetapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// ¡¡IMPORTANTE!! Versión sube de 1 a 2
@Database(entities = [Receta::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recetaDao(): RecetaDao

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // ¡¡IMPORTANTE!!
                    // Esto le dice a Room que si hay una migración (de v1 a v2),
                    // simplemente borre la base de datos antigua y cree una nueva.
                    // Para un proyecto de universidad, es perfecto.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }
}