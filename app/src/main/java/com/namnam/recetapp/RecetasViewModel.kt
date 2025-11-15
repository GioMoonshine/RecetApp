package com.namnam.recetapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecetasViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val recetaDao = database.recetaDao()
    private val usuarioDao = database.usuarioDao()
    private val sessionManager = SessionManager(application)

    // Estado del usuario actual
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    // Mis recetas (del usuario actual)
    private val _misRecetas = MutableStateFlow<List<Receta>>(emptyList())
    val misRecetas: StateFlow<List<Receta>> = _misRecetas.asStateFlow()

    // Recetas para explorar (de otros usuarios)
    private val _explorarRecetas = MutableStateFlow<List<Receta>>(emptyList())
    val explorarRecetas: StateFlow<List<Receta>> = _explorarRecetas.asStateFlow()

    // Todos los usuarios
    val todosLosUsuarios: StateFlow<List<Usuario>> = usuarioDao.obtenerTodosLosUsuarios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        cargarUsuarioActual()
    }

    private fun cargarUsuarioActual() {
        viewModelScope.launch {
            val userId = sessionManager.getCurrentUserId()
            if (userId != -1) {
                val usuario = usuarioDao.obtenerUsuarioPorId(userId)
                _usuarioActual.value = usuario

                // Cargar recetas del usuario
                recetaDao.obtenerRecetasDelUsuario(userId).collect { recetas ->
                    _misRecetas.value = recetas
                }

                // Cargar recetas de otros usuarios
                recetaDao.obtenerRecetasDeOtrosUsuarios(userId).collect { recetas ->
                    _explorarRecetas.value = recetas
                }
            }
        }
    }

    // --- FUNCIONES DE USUARIO ---

    suspend fun registrarUsuario(
        nombreUsuario: String,
        nombreCompleto: String,
        email: String,
        bio: String = ""
    ): Boolean {
        return try {
            val existe = usuarioDao.existeUsuario(nombreUsuario) > 0
            if (existe) {
                false
            } else {
                val nuevoUsuario = Usuario(
                    nombreUsuario = nombreUsuario,
                    nombreCompleto = nombreCompleto,
                    email = email,
                    bio = bio
                )
                val userId = usuarioDao.insertarUsuario(nuevoUsuario).toInt()
                sessionManager.saveUserSession(userId, nombreUsuario)
                cargarUsuarioActual()
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun iniciarSesion(nombreUsuario: String): Boolean {
        return try {
            val usuario = usuarioDao.obtenerUsuarioPorNombre(nombreUsuario)
            if (usuario != null) {
                sessionManager.saveUserSession(usuario.id, usuario.nombreUsuario)
                cargarUsuarioActual()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun cerrarSesion() {
        sessionManager.clearSession()
        _usuarioActual.value = null
        _misRecetas.value = emptyList()
        _explorarRecetas.value = emptyList()
    }

    fun actualizarPerfil(bio: String) {
        viewModelScope.launch {
            _usuarioActual.value?.let { usuario ->
                val usuarioActualizado = usuario.copy(bio = bio)
                usuarioDao.actualizarUsuario(usuarioActualizado)
                _usuarioActual.value = usuarioActualizado
            }
        }
    }

    suspend fun obtenerUsuarioPorId(userId: Int): Usuario? {
        return usuarioDao.obtenerUsuarioPorId(userId)
    }

    // --- FUNCIONES DE RECETAS ---

    fun crearReceta(
        nombre: String,
        tipo: String,
        dificultad: String,
        tiempoValor: Int,
        tiempoUnidad: String,
        ingredientes: List<IngredienteItem>,
        pasos: List<String>,
        imagenUri: String? = null
    ) {
        viewModelScope.launch {
            _usuarioActual.value?.let { usuario ->
                val nuevaReceta = Receta(
                    nombre = nombre,
                    tipo = tipo,
                    dificultad = dificultad,
                    tiempoValor = tiempoValor,
                    tiempoUnidad = tiempoUnidad,
                    ingredientes = ingredientes,
                    pasos = pasos,
                    usuarioId = usuario.id,
                    imagenUri = imagenUri
                )
                recetaDao.insertarReceta(nuevaReceta)
            }
        }
    }

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