package com.namnam.recetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.namnam.recetapp.ui.theme.RecetAppTheme
import java.util.Locale

object Rutas {
    const val MIS_RECETAS = "mis_recetas"
    const val EXPLORAR = "explorar"
}

private enum class Pantalla {
    LISTA,
    DETALLE,
    CREAR_O_EDITAR
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
                    var isLoggedIn by remember { mutableStateOf(false) }

                    if (isLoggedIn) {
                        AppRecetasNavegacion(viewModel = viewModel)
                    } else {
                        PantallaLogin(
                            onLoginClick = {
                                isLoggedIn = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaLogin(onLoginClick: () -> Unit) {
    // ... (Sin cambios)
    var username by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Bienvenido a RecetApp",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Ingresa tu nombre para continuar (simulado)",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Tu nombre de Chef") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotBlank()
        ) {
            Text("Entrar", fontSize = 16.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRecetasNavegacion(viewModel: RecetasViewModel) {
    // ... (Sin cambios en la lógica de navegación principal)
    val tabNavController = rememberNavController()
    var pantallaActual by remember { mutableStateOf(Pantalla.LISTA) }
    var recetaSeleccionada by remember { mutableStateOf<Receta?>(null) }

    val misRecetas by viewModel.misRecetas.collectAsState()
    val explorarRecetas = viewModel.explorarRecetas

    when (pantallaActual) {

        Pantalla.LISTA -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("RecetApp", fontWeight = FontWeight.Bold) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                bottomBar = {
                    AppBottomNavigation(navController = tabNavController)
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        recetaSeleccionada = null
                        pantallaActual = Pantalla.CREAR_O_EDITAR
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Crear Receta")
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = tabNavController,
                    startDestination = Rutas.EXPLORAR,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable(Rutas.MIS_RECETAS) {
                        PantallaListaRecetas(
                            recetas = misRecetas,
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
                            }
                        )
                    }
                    composable(Rutas.EXPLORAR) {
                        PantallaListaRecetas(
                            recetas = explorarRecetas,
                            onRecetaClick = { receta ->
                                recetaSeleccionada = receta
                                pantallaActual = Pantalla.DETALLE
                            },
                            onDeleteClick = null,
                            onEditClick = null
                        )
                    }
                }
            }
        }

        Pantalla.DETALLE -> {
            PantallaDetalleReceta(
                receta = recetaSeleccionada!!,
                onBack = {
                    pantallaActual = Pantalla.LISTA
                    recetaSeleccionada = null
                }
            )
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
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    // ... (Sin cambios)
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Explorar") },
            label = { Text("Explorar") },
            selected = currentRoute == Rutas.EXPLORAR,
            onClick = {
                navController.navigate(Rutas.EXPLORAR) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Mis Recetas") },
            label = { Text("Mis Recetas") },
            selected = currentRoute == Rutas.MIS_RECETAS,
            onClick = {
                navController.navigate(Rutas.MIS_RECETAS) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

@Composable
fun PantallaListaRecetas(
    recetas: List<Receta>,
    onRecetaClick: (Receta) -> Unit,
    onDeleteClick: ((Receta) -> Unit)?,
    onEditClick: ((Receta) -> Unit)?
) {
    // ... (Sin cambios)
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (recetas.isEmpty()) {
            item {
                Text(
                    text = "No hay recetas en esta sección.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
        items(recetas) { receta ->
            TarjetaReceta(
                receta = receta,
                onClick = { onRecetaClick(receta) },
                onDeleteClick = { onDeleteClick?.invoke(receta) },
                onEditClick = { onEditClick?.invoke(receta) }
            )
        }
    }
}


@Composable
fun TarjetaReceta(
    receta: Receta,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)?,
    onEditClick: (() -> Unit)?
) {
    // ¡MODIFICADO!
    // Ahora usa la función getTiempoDisplay() de la Receta
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = receta.nombre,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = receta.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = receta.tipo,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.height(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        // ¡MODIFICADO!
                        text = "⏱️ ${receta.getTiempoDisplay()}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dificultad: ${receta.dificultad}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column {
                if (onEditClick != null) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Receta",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (onDeleteClick != null) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Borrar Receta",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleReceta(
    receta: Receta,
    onBack: () -> Unit
) {
    // ¡MODIFICADO!
    // Ahora usa la función getTiempoDisplay() de la Receta
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = receta.nombre, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = receta.nombre,
                    modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // ¡MODIFICADO!
                        InfoItem("⏱️", receta.getTiempoDisplay())
                        InfoItem("📊", receta.dificultad)
                        InfoItem("🍽️", receta.tipo)
                    }
                }
            }
            item {
                Text(
                    text = "Ingredientes",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // ¡MODIFICADO!
            // Muestra la cantidad y el nombre del ingrediente
            items(receta.ingredientes) { ingrediente ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "✓",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = ingrediente.cantidad,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(text = ingrediente.nombre, fontSize = 15.sp)
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Preparación",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(receta.pasos.size) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Text(
                            text = receta.pasos[index],
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoItem(icono: String, texto: String) {
    // ... (Sin cambios)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icono, fontSize = 24.sp)
        Text(text = texto, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}


// --- ¡¡ESTE ES EL COMPOSABLE MÁS MODIFICADO!! ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearReceta(
    recetaExistente: Receta?,
    viewModel: RecetasViewModel,
    onBack: () -> Unit
) {
    val esModoEditar = recetaExistente != null

    // --- Listas de opciones ---
    val opcionesTipo = listOf("Comida", "Postre", "Bebida", "Entrada")
    val opcionesDificultad = listOf("Fácil", "Media", "Difícil")
    val opcionesTiempo = listOf("Minutos", "Horas", "Días")

    // --- Estados del formulario ---
    var nombre by remember { mutableStateOf(recetaExistente?.nombre ?: "") }

    // Tipo (Dropdown)
    var tipoExpandido by remember { mutableStateOf(false) }
    var tipo by remember { mutableStateOf(recetaExistente?.tipo ?: opcionesTipo[0]) }

    // Dificultad (Dropdown)
    var dificultadExpandido by remember { mutableStateOf(false) }
    var dificultad by remember { mutableStateOf(recetaExistente?.dificultad ?: opcionesDificultad[0]) }

    // Tiempo (Campos separados)
    var tiempoValor by remember { mutableStateOf(recetaExistente?.tiempoValor?.toString() ?: "") }
    var tiempoUnidadExpandido by remember { mutableStateOf(false) }
    var tiempoUnidad by remember { mutableStateOf(recetaExistente?.tiempoUnidad ?: opcionesTiempo[0]) }

    // Ingredientes (Lista dinámica)
    var ingredientes by remember {
        mutableStateOf(recetaExistente?.ingredientes ?: listOf(IngredienteItem()))
    }

    // Pasos (Lista dinámica)
    var pasos by remember {
        mutableStateOf(recetaExistente?.pasos ?: listOf("", "", ""))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esModoEditar) "Editar Receta" else "Crear Nueva Receta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(16.dp)
            ) {
                Button(
                    onClick = {
                        // Filtramos ingredientes y pasos vacíos antes de guardar
                        val ingredientesLimpios = ingredientes.filter { it.cantidad.isNotBlank() || it.nombre.isNotBlank() }
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
                                pasos = pasosLimpios
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
                                pasos = pasosLimpios
                            )
                        }

                        onBack() // Volvemos a la lista
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (esModoEditar) "Actualizar Receta" else "Guardar Receta", fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "Detalles de la Receta",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // --- Nombre ---
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la receta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // --- Tipo (Dropdown) ---
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
                }
            )

            // --- Dificultad (Dropdown) ---
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
                }
            )

            // --- Tiempo (Campos separados) ---
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tiempoValor,
                    onValueChange = { if (it.all { char -> char.isDigit() }) tiempoValor = it },
                    label = { Text("Tiempo") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                // Dropdown para Unidad de Tiempo
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

            // --- Ingredientes (Lista Dinámica) ---
            Text(
                "Ingredientes",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            ingredientes.forEachIndexed { index, ingrediente ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}.", modifier = Modifier.width(24.dp))
                    OutlinedTextField(
                        value = ingrediente.cantidad,
                        onValueChange = { nuevaCantidad ->
                            val copiaLista = ingredientes.toMutableList()
                            copiaLista[index] = ingrediente.copy(cantidad = nuevaCantidad)
                            ingredientes = copiaLista
                        },
                        label = { Text("Cant.") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = ingrediente.nombre,
                        onValueChange = { nuevoNombre ->
                            val copiaLista = ingredientes.toMutableList()
                            copiaLista[index] = ingrediente.copy(nombre = nuevoNombre)
                            ingredientes = copiaLista
                        },
                        label = { Text("Ingrediente") },
                        modifier = Modifier.weight(2f)
                    )
                    // Botón de eliminar ingrediente
                    IconButton(onClick = {
                        val copiaLista = ingredientes.toMutableList()
                        copiaLista.removeAt(index)
                        ingredientes = copiaLista
                    }) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Button(
                onClick = {
                    val copiaLista = ingredientes.toMutableList()
                    copiaLista.add(IngredienteItem())
                    ingredientes = copiaLista
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, "Añadir")
                Text("Añadir Ingrediente")
            }

            // --- Pasos (Lista Dinámica) ---
            Text(
                "Pasos",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            pasos.forEachIndexed { index, paso ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}.", modifier = Modifier.width(24.dp))
                    OutlinedTextField(
                        value = paso,
                        onValueChange = { nuevoPaso ->
                            val copiaLista = pasos.toMutableList()
                            copiaLista[index] = nuevoPaso
                            pasos = copiaLista
                        },
                        label = { Text("Paso ${index + 1}") },
                        modifier = Modifier.weight(1f)
                    )
                    // Botón de eliminar paso
                    IconButton(onClick = {
                        val copiaLista = pasos.toMutableList()
                        copiaLista.removeAt(index)
                        pasos = copiaLista
                    }) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Button(
                onClick = {
                    val copiaLista = pasos.toMutableList()
                    copiaLista.add("")
                    pasos = copiaLista
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, "Añadir")
                Text("Añadir Paso")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- NUEVO Composable reutilizable para los Dropdowns ---
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
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
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