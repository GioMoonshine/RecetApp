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

    // === BASE DE DATOS ===
    private val database = AppDatabase.getDatabase(application)
    private val recetaDao = database.recetaDao()
    private val usuarioDao = database.usuarioDao()
    private val sessionManager = SessionManager(application)
    val themeManager = ThemeManager(application)

    // === ESTADOS ===
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _misRecetas = MutableStateFlow<List<Receta>>(emptyList())
    val misRecetas: StateFlow<List<Receta>> = _misRecetas.asStateFlow()

    private val _explorarRecetas = MutableStateFlow<List<Receta>>(emptyList())
    val explorarRecetas: StateFlow<List<Receta>> = _explorarRecetas.asStateFlow()

    val todosLosUsuarios: StateFlow<List<Usuario>> = usuarioDao.obtenerTodosLosUsuarios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        cargarUsuarioActual()
    }

    // === USUARIO ===
    private fun cargarUsuarioActual() {
        viewModelScope.launch {
            val userId = sessionManager.getCurrentUserId()
            if (userId != -1) {
                val usuario = usuarioDao.obtenerUsuarioPorId(userId)
                _usuarioActual.value = usuario

                // Cargar MIS recetas (solo las del usuario actual, privadas y públicas)
                launch {
                    recetaDao.obtenerRecetasDelUsuario(userId).collect { recetas ->
                        _misRecetas.value = recetas
                    }
                }

                // Cargar recetas públicas EXCLUYENDO las del usuario actual
                launch {
                    recetaDao.obtenerRecetasPublicas().collect { todasPublicas ->
                        // Filtrar para excluir las del usuario actual
                        _explorarRecetas.value = todasPublicas.filter { it.usuarioId != userId }
                    }
                }
            }
        }
    }

    suspend fun registrarUsuario(
        nombreUsuario: String,
        nombreCompleto: String,
        email: String,
        bio: String = ""
    ): Boolean {
        return try {
            val existe = usuarioDao.existeUsuario(nombreUsuario) > 0
            if (existe) {
                return false
            }

            // Crear usuario
            val nuevoUsuario = Usuario(
                nombreUsuario = nombreUsuario,
                nombreCompleto = nombreCompleto,
                email = email,
                bio = bio
            )
            val userId = usuarioDao.insertarUsuario(nuevoUsuario).toInt()

            // Crear recetas por defecto PRIVADAS ANTES de guardar la sesión
            val recetasPorDefecto = RecetasData.obtenerRecetasPorDefecto(userId)
            for (receta in recetasPorDefecto) {
                recetaDao.insertarReceta(receta)
            }

            // Guardar sesión y cargar datos
            sessionManager.saveUserSession(userId, nombreUsuario)
            cargarUsuarioActual()

            true
        } catch (e: Exception) {
            e.printStackTrace()
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

    fun actualizarPerfil(bio: String, fotoPerfilUri: String? = null) {
        viewModelScope.launch {
            _usuarioActual.value?.let { usuario ->
                val usuarioActualizado = usuario.copy(
                    bio = bio,
                    fotoPerfilUri = fotoPerfilUri ?: usuario.fotoPerfilUri
                )
                usuarioDao.actualizarUsuario(usuarioActualizado)
                _usuarioActual.value = usuarioActualizado
            }
        }
    }

    fun actualizarFotoPerfil(uri: String) {
        viewModelScope.launch {
            _usuarioActual.value?.let { usuario ->
                val usuarioActualizado = usuario.copy(fotoPerfilUri = uri)
                usuarioDao.actualizarUsuario(usuarioActualizado)
                _usuarioActual.value = usuarioActualizado
            }
        }
    }

    suspend fun obtenerUsuarioPorId(userId: Int): Usuario? {
        return usuarioDao.obtenerUsuarioPorId(userId)
    }

    // === RECETAS ===
    fun crearReceta(
        nombre: String,
        tipo: String,
        dificultad: String,
        tiempoValor: Int,
        tiempoUnidad: String,
        ingredientes: List<IngredienteItem>,
        pasos: List<String>,
        imagenUri: String? = null,
        esPrivada: Boolean = false  // Por defecto las recetas nuevas son públicas
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
                    imagenUri = imagenUri,
                    esPrivada = esPrivada
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