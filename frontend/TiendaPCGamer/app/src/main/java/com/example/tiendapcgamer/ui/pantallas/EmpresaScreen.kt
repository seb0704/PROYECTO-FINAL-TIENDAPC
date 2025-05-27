package com.example.tiendapcgamer.ui.pantallas

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tiendapcgamer.data.model.Producto
import com.example.tiendapcgamer.data.util.formatearPesos
import com.example.tiendapcgamer.ui.components.NotificationBanner
import com.example.tiendapcgamer.viewmodel.EstadoPago
import com.example.tiendapcgamer.viewmodel.ProductoViewModel
import com.example.tiendapcgamer.viewmodel.SolicitudPago
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresaScreen(
    productoViewModel: ProductoViewModel,
    onBack: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    // Estados para los campos del formulario de producto
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var marcaId by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var editandoId by remember { mutableStateOf<Long?>(null) }

    // Estados para controlar la visibilidad de los diálogos
    var dialogProductoAbierto by remember { mutableStateOf(false) }
    var dialogEliminarProductoAbierto by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    // Listas de marcas y categorías
    val marcas = listOf(1 to "ASUS", 2 to "MSI", 3 to "GIGABYTE", 4 to "AORUS")
    val categorias = listOf(1 to "Portátil", 2 to "Escritorio", 3 to "Accesorios")

    // Estados para los nombres seleccionados en los dropdowns
    var selectedMarcaName by remember { mutableStateOf("Selecciona una marca") }
    var selectedCategoriaName by remember { mutableStateOf("Selecciona una categoría") }

    // Estados para controlar la expansión de los dropdowns personalizados
    var expandedMarca by remember { mutableStateOf(false) }
    var expandedCategoria by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    val gradientBrush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f + gradientOffset * 0.1f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f + (1f - gradientOffset) * 0.1f)
        )
    )

    val fabScale by animateFloatAsState(
        targetValue = if (dialogProductoAbierto) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fabScale"
    )

    val fabRotation by animateFloatAsState(
        targetValue = if (dialogProductoAbierto) 45f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fabRotation"
    )

    var welcomeTextVisible by remember { mutableStateOf(false) }
    var cardsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        productoViewModel.cargarProductos()
        delay(300)
        welcomeTextVisible = true
        delay(200)
        cardsVisible = true
    }

    val productos by productoViewModel.productos.collectAsState()
    val isLoading by productoViewModel.loading.collectAsState()
    val errorMessage by productoViewModel.errorMessage.collectAsState()
    val successMessage by productoViewModel.successMessage.collectAsState()
    val solicitudesPagoPendientes by productoViewModel.solicitudesPagoPendientes.collectAsState()
    val nuevaSolicitudPago by productoViewModel.nuevaSolicitudPago.collectAsState()

    LaunchedEffect(nuevaSolicitudPago) {
        nuevaSolicitudPago?.let {
            productoViewModel.limpiarError()
            productoViewModel.limpiarExito()
            productoViewModel.marcarSolicitudComoVista()
        }
    }

    // Efecto para actualizar los nombres seleccionados cuando cambian los IDs
    LaunchedEffect(marcaId) {
        selectedMarcaName = marcas.find { it.first.toString() == marcaId }?.second ?: "Selecciona una marca"
    }

    LaunchedEffect(categoriaId) {
        selectedCategoriaName = categorias.find { it.first.toString() == categoriaId }?.second ?: "Selecciona una categoría"
    }

    fun limpiarCamposProducto() {
        nombre = ""
        descripcion = ""
        precio = ""
        stock = ""
        marcaId = ""
        categoriaId = ""
        imagenUrl = ""
        editandoId = null
        productoViewModel.limpiarError()
        productoViewModel.limpiarExito()
        selectedMarcaName = "Selecciona una marca"
        selectedCategoriaName = "Selecciona una categoría"
        expandedMarca = false
        expandedCategoria = false
    }

    fun abrirDialogoAgregarProducto() {
        limpiarCamposProducto()
        dialogProductoAbierto = true
    }

    fun abrirDialogoEditarProducto(producto: Producto) {
        nombre = producto.nombre
        descripcion = producto.descripcion
        precio = producto.precio.toInt().toString()
        stock = producto.stock.toString()
        marcaId = producto.marcaId.toString()
        categoriaId = producto.categoriaId.toString()
        imagenUrl = producto.imagenUrl
        editandoId = producto.id
        productoViewModel.limpiarError()
        productoViewModel.limpiarExito()
        selectedMarcaName = marcas.find { it.first.toString() == marcaId }?.second ?: "Selecciona una marca"
        selectedCategoriaName = categorias.find { it.first.toString() == categoriaId }?.second ?: "Selecciona una categoría"
        expandedMarca = false
        expandedCategoria = false
        dialogProductoAbierto = true
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Snackbar(
                        snackbarData = data,
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        containerColor = Color(0xFF25D366),
                        contentColor = Color.White,
                        actionColor = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        actionOnNewLine = true
                    )
                }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AnimatedContent(
                        targetState = "💼 MODO EMPRESA",
                        transitionSpec = {
                            slideInVertically { -it } + fadeIn() togetherWith
                                    slideOutVertically { it } + fadeOut()
                        },
                        label = "topBarTitle"
                    ) { text ->
                        Text(text)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.graphicsLayer {
                            scaleX = 1f
                            scaleY = 1f
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            modifier = Modifier.graphicsLayer {
                                rotationZ = 0f
                            }
                        )
                    }
                },
                actions = {
                    val pendingRequestsCount = solicitudesPagoPendientes.count { it.estado == EstadoPago.PENDIENTE }

                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(
                            badge = {
                                if (pendingRequestsCount > 0) {
                                    Badge {
                                        Text(text = pendingRequestsCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones de pago"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { abrirDialogoAgregarProducto() },
                modifier = Modifier
                    .size(64.dp)
                    .scale(fabScale)
                    .graphicsLayer {
                        rotationZ = fabRotation
                    },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(brush = gradientBrush)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                NotificationBanner(
                    message = errorMessage,
                    isError = true
                )
                NotificationBanner(
                    message = successMessage,
                    isError = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(
                    visible = welcomeTextVisible,
                    enter = slideInVertically { it } + fadeIn(
                        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "¡Hola! 👋 Bienvenido al equipo S-Tech\n" +
                                    "👨🏽‍💻 Tu rol es crucial para nuestro éxito\n" +
                                    "🛒 Brindemos la mejor experiencia de compra\n" +
                                    "¡Juntos potenciamos la tecnología! ⚡",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = cardsVisible,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 600, delayMillis = 600)
                    )
                ) {
                    Text(
                        text = "📦 Gestión de Inventario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                    )
                }

                AnimatedVisibility(
                    visible = cardsVisible,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 600, delayMillis = 200)
                    )
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        userScrollEnabled = false,
                        modifier = Modifier.heightIn(min = 0.dp, max = 1500.dp)
                    ) {
                        items(productos) { producto ->
                            ProductCard(
                                producto = producto,
                                marcas = marcas,
                                categorias = categorias,
                                onEdit = { abrirDialogoEditarProducto(producto) },
                                onDelete = {
                                    productoAEliminar = producto
                                    dialogEliminarProductoAbierto = true
                                }
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }
            }

            if (dialogProductoAbierto) {
                val dialogScrollState = rememberScrollState()

                AlertDialog(
                    onDismissRequest = {
                        dialogProductoAbierto = false
                        expandedMarca = false
                        expandedCategoria = false
                    },
                    confirmButton = {
                        AnimatedContent(
                            targetState = editandoId == null,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "confirmButton"
                        ) { isAdd ->
                            TextButton(onClick = {
                                if (!productoViewModel.validarCampos(
                                        nombre,
                                        descripcion,
                                        precio,
                                        stock,
                                        marcaId,
                                        categoriaId
                                    )
                                ) {
                                    productoViewModel.setErrorMessage("⚠️ Todos los campos deben ser válidos")
                                    productoViewModel.limpiarExito()
                                    return@TextButton
                                }

                                val producto = Producto(
                                    id = editandoId,
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    precio = precio.toDouble(),
                                    stock = stock.toInt(),
                                    marcaId = marcaId.toInt(),
                                    categoriaId = categoriaId.toInt(),
                                    imagenUrl = imagenUrl
                                )

                                if (editandoId == null) {
                                    productoViewModel.agregarProducto(producto)
                                } else {
                                    productoViewModel.actualizarProducto(producto)
                                }
                                dialogProductoAbierto = false
                                expandedMarca = false
                                expandedCategoria = false
                            }) {
                                Text(if (isAdd) "\uD83D\uDCE6 Agregar" else "🛠 Actualizar")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            dialogProductoAbierto = false
                            expandedMarca = false
                            expandedCategoria = false
                        }) {
                            Text("❌ Cancelar")
                        }
                    },
                    title = {
                        AnimatedContent(
                            targetState = editandoId == null,
                            transitionSpec = {
                                slideInHorizontally { if (targetState) -it else it } + fadeIn() togetherWith
                                        slideOutHorizontally { if (targetState) it else -it } + fadeOut()
                            },
                            label = "dialogTitle"
                        ) { isAdd ->
                            Text(if (isAdd) "\uD83D\uDCE6 Agregar producto" else "🛠 Editar producto")
                        }
                    },
                    text = {
                        Column(modifier = Modifier.verticalScroll(dialogScrollState)) {
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre 🏷️") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción 📝") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = precio,
                                onValueChange = { if (it.all(Char::isDigit) || (it.all { char -> char.isDigit() || char == '.' } && it.count { char -> char == '.' } <= 1)) precio = it },
                                label = { Text("Precio \uD83D\uDCB8") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = stock,
                                onValueChange = { if (it.all(Char::isDigit)) stock = it },
                                label = { Text("Stock 📦") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // SELECTOR PERSONALIZADO DE MARCA
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Marca 🏷️",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    // Botón principal que simula un TextField
                                    OutlinedButton(
                                        onClick = { expandedMarca = !expandedMarca },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            contentColor = if (marcaId.isEmpty())
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            else MaterialTheme.colorScheme.onSurface
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            width = 1.dp,
                                            brush = Brush.horizontalGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.outline,
                                                    MaterialTheme.colorScheme.outline
                                                )
                                            )
                                        ),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = selectedMarcaName,
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Start
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Expandir",
                                                modifier = Modifier.graphicsLayer {
                                                    rotationZ = if (expandedMarca) 180f else 0f
                                                }
                                            )
                                        }
                                    }

                                    // Menú dropdown
                                    DropdownMenu(
                                        expanded = expandedMarca,
                                        onDismissRequest = { expandedMarca = false },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        marcas.forEach { (id, name) ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        if (marcaId == id.toString()) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = "Seleccionado",
                                                                tint = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                        Text(
                                                            text = name,
                                                            color = if (marcaId == id.toString())
                                                                MaterialTheme.colorScheme.primary
                                                            else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    marcaId = id.toString()
                                                    selectedMarcaName = name
                                                    expandedMarca = false
                                                },
                                                modifier = Modifier.background(
                                                    if (marcaId == id.toString())
                                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                                                    else Color.Transparent
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // SELECTOR PERSONALIZADO DE CATEGORÍA
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Categoría 📂",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    // Botón principal que simula un TextField
                                    OutlinedButton(
                                        onClick = { expandedCategoria = !expandedCategoria },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            contentColor = if (categoriaId.isEmpty())
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            else MaterialTheme.colorScheme.onSurface
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            width = 1.dp,
                                            brush = Brush.horizontalGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.outline,
                                                    MaterialTheme.colorScheme.outline
                                                )
                                            )
                                        ),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = selectedCategoriaName,
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Start
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Expandir",
                                                modifier = Modifier.graphicsLayer {
                                                    rotationZ = if (expandedCategoria) 180f else 0f
                                                }
                                            )
                                        }
                                    }

                                    // Menú dropdown
                                    DropdownMenu(
                                        expanded = expandedCategoria,
                                        onDismissRequest = { expandedCategoria = false },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        categorias.forEach { (id, name) ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        if (categoriaId == id.toString()) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = "Seleccionado",
                                                                tint = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                        Text(
                                                            text = name,
                                                            color = if (categoriaId == id.toString())
                                                                MaterialTheme.colorScheme.primary
                                                            else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    categoriaId = id.toString()
                                                    selectedCategoriaName = name
                                                    expandedCategoria = false
                                                },
                                                modifier = Modifier.background(
                                                    if (categoriaId == id.toString())
                                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                                                    else Color.Transparent
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = imagenUrl,
                                onValueChange = { imagenUrl = it },
                                label = { Text("URL Imagen 🌄") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }

            if (dialogEliminarProductoAbierto && productoAEliminar != null) {
                AlertDialog(
                    onDismissRequest = { dialogEliminarProductoAbierto = false },
                    title = { Text("🗑 Confirmar eliminación") },
                    text = { Text("¿Seguro que deseas eliminar el producto '${productoAEliminar!!.nombre}'?") },
                    confirmButton = {
                        TextButton(onClick = {
                            productoViewModel.eliminarProducto(productoAEliminar!!.id!!)
                            dialogEliminarProductoAbierto = false
                            productoAEliminar = null
                        }) {
                            Text("Eliminar 🗑")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogEliminarProductoAbierto = false }) {
                            Text("Cancelar ❌")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    producto: Producto,
    marcas: List<Pair<Int, String>>,
    categorias: List<Pair<Int, String>>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ), label = "cardScale"
    )

    val cardElevation by animateDpAsState(
        targetValue = if (isHovered) 16.dp else 10.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ), label = "cardElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .scale(cardScale),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FC)),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (producto.imagenUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(producto.imagenUrl),
                        contentDescription = "Imagen de ${producto.nombre}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Text(
                    text = producto.nombre.uppercase(),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = producto.descripcion,
                    color = Color.DarkGray,
                    maxLines = 3,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "💵 Precio: ${formatearPesos(producto.precio)}",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "📦 Stock: ${producto.stock} unidades",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )

                val nombreMarca = marcas.find { it.first == producto.marcaId }?.second ?: "Desconocida"
                Text(
                    text = "🏷️ Marca: $nombreMarca",
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )

                val nombreCategoria = categorias.find { it.first == producto.categoriaId }?.second ?: "Desconocida"
                Text(
                    text = "📂 Categoría: $nombreCategoria",
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("✏️")
                }
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))))
                ) {
                    Text("🗑")
                }
            }
        }
    }
}