package com.namnam.recetapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecetasViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val recetaDao = database.recetaDao()

    val misRecetas: StateFlow<List<Receta>> = recetaDao.obtenerMisRecetas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ¡MODIFICADO!
    // Tu RecetasData.kt estático ya no es compatible con el nuevo
    // modelo de Receta (porque usa 'tiempoPreparacion', no 'tiempoValor').
    // Así que lo dejaremos vacío por ahora. La pestaña "Explorar" estará vacía
    // hasta que la actualicemos.
    val explorarRecetas: List<Receta> = emptyList() // RecetasData.obtenerRecetas() -> ESTO ROMPERÍA LA APP

    // ¡MODIFICADO! Acepta los nuevos campos
    fun crearReceta(
        nombre: String,
        tipo: String,
        dificultad: String,
        tiempoValor: Int,
        tiempoUnidad: String,
        ingredientes: List<IngredienteItem>,
        pasos: List<String>
    ) {
        viewModelScope.launch {
            val nuevaReceta = Receta(
                nombre = nombre,
                tipo = tipo,
                dificultad = dificultad,
                tiempoValor = tiempoValor,
                tiempoUnidad = tiempoUnidad,
                ingredientes = ingredientes,
                pasos = pasos
            )
            recetaDao.insertarReceta(nuevaReceta)
        }
    }

    // ¡MODIFICADO! Acepta el nuevo objeto Receta
    fun actualizarReceta(receta: Receta) {
        viewModelScope.launch {
            recetaDao.actualizarReceta(receta)
        }
    }

    fun borrarReceta(receta: Receta) {
        viewModelScope.launch {
            recetaDao.borrarReceta(receta)
        }
    }

    class RecetasViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecetasViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RecetasViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}