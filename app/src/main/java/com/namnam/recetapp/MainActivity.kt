package com.namnam.recetapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.namnam.recetapp.ui.theme.RecetAppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

object Rutas {
    const val MIS_RECETAS = "mis_recetas"
    const val EXPLORAR = "explorar"
    const val PERFIL = "perfil"
}

private enum class Pantalla {
    LISTA,
    DETALLE,
    CREAR_O_EDITAR,
    PERFIL_USUARIO
}

class MainActivity : ComponentActivity() {

    private val viewModel: RecetasViewModel by viewModels {
        RecetasViewModel.RecetasViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecetAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val usuarioActual by viewModel.usuarioActual.collectAsState()

                    if (usuarioActual == null) {
                        PantallaAuth(viewModel = viewModel)
                    } else {
                        AppRecetasNavegacion(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaAuth(viewModel: RecetasViewModel) {
    var modoRegistro by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var nombreCompleto by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo placeholder (círculo negro con icono)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "RecetApp",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                if (modoRegistro) "Crea tu cuenta" else "Bienvenido de vuelta",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMsg = ""
                },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            if (modoRegistro) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = nombreCompleto,
                    onValueChange = { nombreCompleto = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            if (errorMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        val exito = if (modoRegistro) {
                            viewModel.registrarUsuario(
                                nombreUsuario = username.trim(),
                                nombreCompleto = nombreCompleto.trim(),
                                email = email.trim()
                            )
                        } else {
                            viewModel.iniciarSesion(username.trim())
                        }

                        if (!exito) {
                            errorMsg = if (modoRegistro) {
                                "El usuario ya existe"
                            } else {
                                "Usuario no encontrado"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = username.isNotBlank() && (!modoRegistro || (nombreCompleto.isNotBlank() && email.isNotBlank())),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    if (modoRegistro) "Crear cuenta" else "Iniciar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    modoRegistro = !modoRegistro
                    errorMsg = ""
                }
            ) {
                Text(
                    if (modoRegistro) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRecetasNavegacion(viewModel: RecetasViewModel) {
    val tabNavController = rememberNavController()
    var pantallaActual by remember { mutableStateOf(Pantalla.LISTA) }
    var recetaSeleccionada by remember { mutableStateOf<Receta?>(null) }
    var usuarioSeleccionado by remember { mutableStateOf<Usuario?>(null) }

    val misRecetas by viewModel.misRecetas.collectAsState()
    val explorarRecetas by viewModel.explorarRecetas.collectAsState()
    val usuarioActual by viewModel.usuarioActual.collectAsState()

    when (pantallaActual) {
        Pantalla.LISTA -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "RecetApp",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                bottomBar = {
                    AppBottomNavigation(navController = tabNavController)
                },
                floatingActionButton = {
                    val currentRoute = tabNavController.currentBackStackEntryAsState().value?.destination?.route
                    if (currentRoute == Rutas.MIS_RECETAS) {
                        FloatingActionButton(
                            onClick = {
                                recetaSeleccionada = null
                                pantallaActual = Pantalla.CREAR_O_EDITAR
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Crear Receta")
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                NavHost(
                    navController = tabNavController,
                    startDestination = Rutas.EXPLORAR,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable(Rutas.MIS_RECETAS) {
                        PantallaListaRecetas(
                            titulo = "Mis Recetas",
                            recetas = misRecetas,
                            viewModel = viewModel,
                            onRecetaClick = { receta ->
                                recetaSeleccionada = receta
                                pantallaActual = Pantalla.DETALLE
                            },
                            onDeleteClick = { receta ->
                                viewModel.borrarReceta(receta)
                            },
                            onEditClick = { receta ->
                                recetaSeleccionada = receta
                                pantallaActual = Pantalla.CREAR_O_EDITAR
                            },
                            mostrarAutor = false
                        )
                    }
                    composable(Rutas.EXPLORAR) {
                        PantallaListaRecetas(
                            titulo = "Explorar",
                            recetas = explorarRecetas,
                            viewModel = viewModel,
                            onRecetaClick = { receta ->
                                recetaSeleccionada = receta
                                pantallaActual = Pantalla.DETALLE
                            },
                            onDeleteClick = null,
                            onEditClick = null,
                            mostrarAutor = true,
                            onAutorClick = { usuario ->
                                usuarioSeleccionado = usuario
                                pantallaActual = Pantalla.PERFIL_USUARIO
                            }
                        )
                    }
                    composable(Rutas.PERFIL) {
                        usuarioActual?.let { usuario ->
                            PantallaPerfil(
                                usuario = usuario,
                                viewModel = viewModel,
                                esMiPerfil = true,
                                onCerrarSesion = {
                                    viewModel.cerrarSesion()
                                }
                            )
                        }
                    }
                }
            }
        }
        Pantalla.DETALLE -> {
            recetaSeleccionada?.let { receta ->
                PantallaDetalleReceta(
                    receta = receta,
                    viewModel = viewModel,
                    onBack = {
                        pantallaActual = Pantalla.LISTA
                        recetaSeleccionada = null
                    },
                    onAutorClick = { usuario ->
                        usuarioSeleccionado = usuario
                        pantallaActual = Pantalla.PERFIL_USUARIO
                    }
                )
            }
        }
        Pantalla.CREAR_O_EDITAR -> {
            PantallaCrearReceta(
                recetaExistente = recetaSeleccionada,
                viewModel = viewModel,
                onBack = {
                    pantallaActual = Pantalla.LISTA
                    recetaSeleccionada = null
                }
            )
        }

        Pantalla.PERFIL_USUARIO -> {
            usuarioSeleccionado?.let { usuario ->
                PantallaPerfil(
                    usuario = usuario,
                    viewModel = viewModel,
                    esMiPerfil = false,
                    onBack = {
                        pantallaActual = Pantalla.LISTA
                        usuarioSeleccionado = null
                    },
                    onCerrarSesion = {}
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Explorar",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Explorar", fontSize = 12.sp) },
            selected = currentRoute == Rutas.EXPLORAR,
            onClick = {
                navController.navigate(Rutas.EXPLORAR) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Mis Recetas",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Mis Recetas", fontSize = 12.sp) },
            selected = currentRoute == Rutas.MIS_RECETAS,
            onClick = {
                navController.navigate(Rutas.MIS_RECETAS) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Perfil", fontSize = 12.sp) },
            selected = currentRoute == Rutas.PERFIL,
            onClick = {
                navController.navigate(Rutas.PERFIL) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun PantallaListaRecetas(
    titulo: String,
    recetas: List<Receta>,
    viewModel: RecetasViewModel,
    onRecetaClick: (Receta) -> Unit,
    onDeleteClick: ((Receta) -> Unit)?,
    onEditClick: ((Receta) -> Unit)?,
    mostrarAutor: Boolean = false,
    onAutorClick: ((Usuario) -> Unit)? = null
) {
    var usuariosMap by remember { mutableStateOf<Map<Int, Usuario>>(emptyMap()) }

    LaunchedEffect(recetas) {
        if (mostrarAutor) {
            val map = mutableMapOf<Int, Usuario>()
            recetas.forEach { receta ->
                if (!map.containsKey(receta.usuarioId)) {
                    val usuario = viewModel.obtenerUsuarioPorId(receta.usuarioId)
                    if (usuario != null) {
                        map[receta.usuarioId] = usuario
                    }
                }
            }
            usuariosMap = map
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (recetas.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = if (titulo.contains("Mis")) "Aún no has creado recetas" else "No hay recetas disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        items(recetas) { receta ->
            TarjetaReceta(
                receta = receta,
                autor = if (mostrarAutor) usuariosMap[receta.usuarioId] else null,
                onClick = { onRecetaClick(receta) },
                onDeleteClick = onDeleteClick?.let { { it(receta) } },
                onEditClick = onEditClick?.let { { it(receta) } },
                onAutorClick = if (mostrarAutor && onAutorClick != null) {
                    { usuariosMap[receta.usuarioId]?.let { onAutorClick(it) } }
                } else null
            )
        }
    }
}

@Composable
fun TarjetaReceta(
    receta: Receta,
    autor: Usuario? = null,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)?,
    onEditClick: (() -> Unit)?,
    onAutorClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagen
            if (receta.imagenUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(Uri.parse(receta.imagenUri)),
                    contentDescription = receta.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
            }

            // Contenido
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = receta.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (autor != null && onAutorClick != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onAutorClick() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = autor.getIniciales(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = autor.nombreUsuario,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoChip(icon = "⏱️", text = receta.getTiempoDisplay())
                    InfoChip(icon = "📊", text = receta.dificultad)
                    InfoChip(icon = "🍽️", text = receta.tipo)
                }

                if (onEditClick != null || onDeleteClick != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (onEditClick != null) {
                            IconButton(onClick = onEditClick) {
                                Icon(
                                    Icons.Default.Edit,
                                    "Editar",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        if (onDeleteClick != null) {
                            IconButton(onClick = onDeleteClick) {
                                Icon(
                                    Icons.Default.Delete,
                                    "Eliminar",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(icon: String, text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// CONTINUACIÓN DEL MAINACTIVITY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleReceta(
    receta: Receta,
    viewModel: RecetasViewModel,
    onBack: () -> Unit,
    onAutorClick: (Usuario) -> Unit
) {
    var autor by remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(receta) {
        autor = viewModel.obtenerUsuarioPorId(receta.usuarioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                // Imagen principal
                if (receta.imagenUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(Uri.parse(receta.imagenUri)),
                        contentDescription = receta.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = receta.nombre,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (autor != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAutorClick(autor!!) },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = autor!!.getIniciales(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = autor!!.nombreCompleto,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "@${autor!!.nombreUsuario}",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoChip(icon = "⏱️", text = receta.getTiempoDisplay())
                        InfoChip(icon = "📊", text = receta.dificultad)
                        InfoChip(icon = "🍽️", text = receta.tipo)
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Ingredientes",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            items(receta.ingredientes) { ingrediente ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = ingrediente.cantidad,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = ingrediente.nombre,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Preparación",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            items(receta.pasos.size) { index ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp
                ) {
                    Row(modifier = Modifier.padding(20.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = receta.pasos[index],
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearReceta(
    recetaExistente: Receta?,
    viewModel: RecetasViewModel,
    onBack: () -> Unit
) {
    val esModoEditar = recetaExistente != null

    val opcionesTipo = listOf("Comida", "Postre", "Bebida", "Entrada")
    val opcionesDificultad = listOf("Fácil", "Media", "Difícil")
    val opcionesTiempo = listOf("Minutos", "Horas", "Días")

    var nombre by remember { mutableStateOf(recetaExistente?.nombre ?: "") }
    var tipoExpandido by remember { mutableStateOf(false) }
    var tipo by remember { mutableStateOf(recetaExistente?.tipo ?: opcionesTipo[0]) }
    var dificultadExpandido by remember { mutableStateOf(false) }
    var dificultad by remember { mutableStateOf(recetaExistente?.dificultad ?: opcionesDificultad[0]) }
    var tiempoValor by remember { mutableStateOf(recetaExistente?.tiempoValor?.toString() ?: "") }
    var tiempoUnidadExpandido by remember { mutableStateOf(false) }
    var tiempoUnidad by remember { mutableStateOf(recetaExistente?.tiempoUnidad ?: opcionesTiempo[0]) }
    var ingredientes by remember { mutableStateOf(recetaExistente?.ingredientes ?: listOf(IngredienteItem())) }
    var pasos by remember { mutableStateOf(recetaExistente?.pasos ?: listOf("", "", "")) }
    var imagenUri by remember { mutableStateOf(recetaExistente?.imagenUri?.let { Uri.parse(it) }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (esModoEditar) "Editar Receta" else "Nueva Receta",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Button(
                    onClick = {
                        val ingredientesLimpios = ingredientes.filter {
                            it.cantidad.isNotBlank() || it.nombre.isNotBlank()
                        }
                        val pasosLimpios = pasos.filter { it.isNotBlank() }
                        val valorTiempo = tiempoValor.toIntOrNull() ?: 0

                        if (esModoEditar) {
                            val recetaActualizada = recetaExistente!!.copy(
                                nombre = nombre,
                                tipo = tipo,
                                dificultad = dificultad,
                                tiempoValor = valorTiempo,
                                tiempoUnidad = tiempoUnidad,
                                ingredientes = ingredientesLimpios,
                                pasos = pasosLimpios,
                                imagenUri = imagenUri?.toString()
                            )
                            viewModel.actualizarReceta(recetaActualizada)
                        } else {
                            viewModel.crearReceta(
                                nombre = nombre,
                                tipo = tipo,
                                dificultad = dificultad,
                                tiempoValor = valorTiempo,
                                tiempoUnidad = tiempoUnidad,
                                ingredientes = ingredientesLimpios,
                                pasos = pasosLimpios,
                                imagenUri = imagenUri?.toString()
                            )
                        }

                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = nombre.isNotBlank()
                ) {
                    Text(
                        if (esModoEditar) "Actualizar" else "Guardar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Selector de imagen
            ImagePickerBox(
                imageUri = imagenUri,
                onImageSelected = { uri -> imagenUri = uri }
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la receta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DropdownCampo(
                    label = "Tipo",
                    opciones = opcionesTipo,
                    seleccionado = tipo,
                    expandido = tipoExpandido,
                    onExpandir = { tipoExpandido = !tipoExpandido },
                    onDismiss = { tipoExpandido = false },
                    onSelect = {
                        tipo = it
                        tipoExpandido = false
                    },
                    modifier = Modifier.weight(1f)
                )

                DropdownCampo(
                    label = "Dificultad",
                    opciones = opcionesDificultad,
                    seleccionado = dificultad,
                    expandido = dificultadExpandido,
                    onExpandir = { dificultadExpandido = !dificultadExpandido },
                    onDismiss = { dificultadExpandido = false },
                    onSelect = {
                        dificultad = it
                        dificultadExpandido = false
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = tiempoValor,
                    onValueChange = { if (it.all { char -> char.isDigit() }) tiempoValor = it },
                    label = { Text("Tiempo") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp)
                )
                DropdownCampo(
                    label = "Unidad",
                    opciones = opcionesTiempo,
                    seleccionado = tiempoUnidad,
                    expandido = tiempoUnidadExpandido,
                    onExpandir = { tiempoUnidadExpandido = !tiempoUnidadExpandido },
                    onDismiss = { tiempoUnidadExpandido = false },
                    onSelect = {
                        tiempoUnidad = it
                        tiempoUnidadExpandido = false
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Ingredientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            ingredientes.forEachIndexed { index, ingrediente ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ingrediente.cantidad,
                        onValueChange = { nuevaCantidad ->
                            val copiaLista = ingredientes.toMutableList()
                            copiaLista[index] = ingrediente.copy(cantidad = nuevaCantidad)
                            ingredientes = copiaLista
                        },
                        label = { Text("Cant.") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    OutlinedTextField(
                        value = ingrediente.nombre,
                        onValueChange = { nuevoNombre ->
                            val copiaLista = ingredientes.toMutableList()
                            copiaLista[index] = ingrediente.copy(nombre = nuevoNombre)
                            ingredientes = copiaLista
                        },
                        label = { Text("Ingrediente") },
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    IconButton(onClick = {
                        if (ingredientes.size > 1) {
                            val copiaLista = ingredientes.toMutableList()
                            copiaLista.removeAt(index)
                            ingredientes = copiaLista
                        }
                    }) {
                        Icon(Icons.Default.Delete, "Eliminar")
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    val copiaLista = ingredientes.toMutableList()
                    copiaLista.add(IngredienteItem())
                    ingredientes = copiaLista
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, "Añadir", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir Ingrediente")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Pasos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            pasos.forEachIndexed { index, paso ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedTextField(
                        value = paso,
                        onValueChange = { nuevoPaso ->
                            val copiaLista = pasos.toMutableList()
                            copiaLista[index] = nuevoPaso
                            pasos = copiaLista
                        },
                        label = { Text("Descripción") },
                        modifier = Modifier.weight(1f),
                        minLines = 2,
                        shape = RoundedCornerShape(16.dp)
                    )
                    IconButton(
                        onClick = {
                            if (pasos.size > 1) {
                                val copiaLista = pasos.toMutableList()
                                copiaLista.removeAt(index)
                                pasos = copiaLista
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(Icons.Default.Delete, "Eliminar")
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    val copiaLista = pasos.toMutableList()
                    copiaLista.add("")
                    pasos = copiaLista
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, "Añadir", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir Paso")
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownCampo(
    label: String,
    opciones: List<String>,
    seleccionado: String,
    expandido: Boolean,
    onExpandir: () -> Unit,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { onExpandir() },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = seleccionado,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { onDismiss() }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = { onSelect(opcion) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    usuario: Usuario,
    viewModel: RecetasViewModel,
    esMiPerfil: Boolean,
    onBack: (() -> Unit)? = null,
    onCerrarSesion: () -> Unit
) {
    var editandoBio by remember { mutableStateOf(false) }
    var bioTemp by remember { mutableStateOf(usuario.bio) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val fechaRegistroStr = dateFormatter.format(Date(usuario.fechaRegistro))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esMiPerfil) "Mi Perfil" else "Perfil") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Volver")
                        }
                    }
                },
                actions = {
                    if (esMiPerfil) {
                        IconButton(onClick = onCerrarSesion) {
                            Icon(Icons.Default.ExitToApp, "Cerrar Sesión")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = usuario.getIniciales(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = usuario.nombreCompleto,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "@${usuario.nombreUsuario}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Miembro desde $fechaRegistroStr",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Biografía",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (esMiPerfil && !editandoBio) {
                                IconButton(onClick = {
                                    editandoBio = true
                                    bioTemp = usuario.bio
                                }) {
                                    Icon(Icons.Default.Edit, "Editar")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (editandoBio && esMiPerfil) {
                            OutlinedTextField(
                                value = bioTemp,
                                onValueChange = { bioTemp = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Cuéntanos sobre ti...") },
                                minLines = 3,
                                shape = RoundedCornerShape(16.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                            ) {
                                TextButton(onClick = { editandoBio = false }) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        viewModel.actualizarPerfil(bioTemp)
                                        editandoBio = false
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Guardar")
                                }
                            }
                        } else {
                            Text(
                                text = if (usuario.bio.isNotBlank()) usuario.bio else "Sin biografía",
                                fontSize = 15.sp,
                                color = if (usuario.bio.isNotBlank())
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Email",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = usuario.email,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}