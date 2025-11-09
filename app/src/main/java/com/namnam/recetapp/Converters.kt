package com.namnam.recetapp

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    // --- Conversor para la LISTA DE PASOS (List<String>) ---
    private val SEPARADOR_PASOS = "|||---PASO_SEP---|||"

    @TypeConverter
    fun fromPasosList(list: List<String>): String {
        return list.joinToString(SEPARADOR_PASOS)
    }

    @TypeConverter
    fun toPasosList(data: String): List<String> {
        if (data.isBlank()) return emptyList()
        return data.split(SEPARADOR_PASOS)
    }

    // --- Conversor para la LISTA DE INGREDIENTES (List<IngredienteItem>) ---
    // Usamos GSON para convertir la lista de objetos a un string JSON

    @TypeConverter
    fun fromIngredientesList(list: List<IngredienteItem>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toIngredientesList(data: String): List<IngredienteItem> {
        if (data.isBlank()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<IngredienteItem>>() {}.type
        return Gson().fromJson(data, listType)
    }
}