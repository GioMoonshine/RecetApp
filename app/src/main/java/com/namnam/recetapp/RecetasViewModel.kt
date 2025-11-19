package com.namnam.recetapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecetasViewModel(application: Application) : AndroidViewModel(application) {

    // === BASE DE DATOS ===
    private val database = AppDatabase.getDatabase(application)
    private val recetaDao = database.recetaDao()
    private val usuarioDao = database.usuarioDao()
    private val sessionManager = SessionManager(application)
    val themeManager = ThemeManager(application)

    // === JOBS PARA CANCELAR COLECCIONES ANTERIORES ===
    private var misRecetasJob: Job? = null
    private var explorarRecetasJob: Job? = null

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
            // ✅ PASO 1: Cancelar todas las colecciones anteriores
            misRecetasJob?.cancel()
            explorarRecetasJob?.cancel()

            // ✅ PASO 2: Limpiar estados inmediatamente
            _misRecetas.value = emptyList()
            _explorarRecetas.value = emptyList()
            _usuarioActual.value = null

            // ✅ PASO 3: Cargar nuevo usuario
            val userId = sessionManager.getCurrentUserId()
            if (userId != -1) {
                val usuario = usuarioDao.obtenerUsuarioPorId(userId)
                _usuarioActual.value = usuario

                // ✅ PASO 4: Iniciar nuevas colecciones con el userId correcto
                misRecetasJob = launch {
                    recetaDao.obtenerRecetasDelUsuario(userId).collect { recetas ->
                        // ✅ Verificación adicional: solo actualizar si sigue siendo el usuario actual
                        if (_usuarioActual.value?.id == userId) {
                            _misRecetas.value = recetas
                        }
                    }
                }

                explorarRecetasJob = launch {
                    recetaDao.obtenerRecetasPublicas().collect { todasPublicas ->
                        // ✅ Verificación adicional: solo actualizar si sigue siendo el usuario actual
                        if (_usuarioActual.value?.id == userId) {
                            _explorarRecetas.value = todasPublicas.filter { receta ->
                                receta.usuarioId != userId
                            }
                        }
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

            // 1. Crear usuario
            val nuevoUsuario = Usuario(
                nombreUsuario = nombreUsuario,
                nombreCompleto = nombreCompleto,
                email = email,
                bio = bio
            )
            val userId = usuarioDao.insertarUsuario(nuevoUsuario).toInt()

            // 2. ✅ VERIFICAR que el usuario no tenga recetas ya
            val cantidadRecetas = recetaDao.contarRecetasDelUsuario(userId)

            // 3. Solo crear recetas por defecto si NO tiene ninguna
            if (cantidadRecetas == 0) {
                val recetasPorDefecto = RecetasData.obtenerRecetasPorDefecto(userId)
                for (receta in recetasPorDefecto) {
                    recetaDao.insertarReceta(receta)
                }
            }

            // 4. Guardar sesión y cargar datos
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
        // ✅ Cancelar colecciones antes de limpiar
        misRecetasJob?.cancel()
        explorarRecetasJob?.cancel()

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
                // Eliminar la foto anterior si existe y no es un recurso
                if (usuario.fotoPerfilUri != null &&
                    !usuario.fotoPerfilUri.startsWith("android.resource://")) {
                    withContext(Dispatchers.IO) {
                        ImageCompressor.deleteCompressedImage(
                            usuario.fotoPerfilUri.removePrefix("file://")
                        )
                    }
                }

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
        esPrivada: Boolean = false
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
            // Eliminar la imagen física si existe
            if (receta.imagenUri != null &&
                !receta.imagenUri.startsWith("android.resource://")) {
                withContext(Dispatchers.IO) {
                    ImageCompressor.deleteCompressedImage(
                        receta.imagenUri.removePrefix("file://")
                    )
                }
            }

            recetaDao.borrarReceta(receta)
        }
    }

    fun limpiarImagenesHuerfanas() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val todasLasRecetas = _misRecetas.value + _explorarRecetas.value
                val rutasReferenciadas = todasLasRecetas
                    .mapNotNull { it.imagenUri }
                    .filter { !it.startsWith("android.resource://") }
                    .map { it.removePrefix("file://") }

                ImageCompressor.cleanOrphanImages(
                    getApplication(),
                    rutasReferenciadas
                )
            }
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