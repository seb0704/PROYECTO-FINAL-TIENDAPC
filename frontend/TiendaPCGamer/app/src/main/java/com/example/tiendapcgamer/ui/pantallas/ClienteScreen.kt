package com.example.tiendapcgamer.ui.pantallas

import androidx.compose.foundation.interaction.MutableInteractionSource
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.tiendapcgamer.data.util.formatearPesos
import com.example.tiendapcgamer.ui.navigation.Routes
import com.example.tiendapcgamer.ui.theme.CyberpunkPrimary
import com.example.tiendapcgamer.viewmodel.ProductoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class,)
@Composable
fun ClienteScreen(
    productoViewModel: ProductoViewModel,
    navController: NavHostController
) {
    val productos by productoViewModel.productos.collectAsState()
    val isLoading by productoViewModel.loading.collectAsState()
    val errorMessage by productoViewModel.errorMessage.collectAsState()
    val carritoProductos by productoViewModel.carrito.collectAsState()
    val context = LocalContext.current

    var productoSeleccionado by remember { mutableStateOf<String?>(null) }
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }

    // Categorías predefinidas (puedes obtenerlas del ViewModel si las tienes)
    val categorias = remember {
        listOf("Todas", "Procesadores", "Tarjetas Gráficas", "Memoria RAM", "Almacenamiento", "Placas Madre", "Fuentes", "Gabinetes", "Periféricos")
    }

    // Filtrar productos basado en búsqueda y categoría
    val productosFiltrados = remember(productos, textoBusqueda, categoriaSeleccionada) {
        productos.filter { producto ->
            val coincideBusqueda = if (textoBusqueda.isBlank()) {
                true
            } else {
                producto.nombre.contains(textoBusqueda, ignoreCase = true) ||
                        producto.descripcion.contains(textoBusqueda, ignoreCase = true)
            }

            val coincideCategoria = if (categoriaSeleccionada == "Todas") {
                true
            } else {
                // Aquí puedes usar producto.categoria si tienes ese campo
                // Por ahora uso una lógica simple basada en palabras clave en el nombre
                when (categoriaSeleccionada) {
                    "Procesadores" -> producto.nombre.contains("Procesador", ignoreCase = true) ||
                            producto.nombre.contains("CPU", ignoreCase = true) ||
                            producto.nombre.contains("Intel", ignoreCase = true) ||
                            producto.nombre.contains("AMD", ignoreCase = true)
                    "Tarjetas Gráficas" -> producto.nombre.contains("RTX", ignoreCase = true) ||
                            producto.nombre.contains("GTX", ignoreCase = true) ||
                            producto.nombre.contains("GPU", ignoreCase = true) ||
                            producto.nombre.contains("Gráfica", ignoreCase = true)
                    "Memoria RAM" -> producto.nombre.contains("RAM", ignoreCase = true) ||
                            producto.nombre.contains("Memoria", ignoreCase = true)
                    "Almacenamiento" -> producto.nombre.contains("SSD", ignoreCase = true) ||
                            producto.nombre.contains("HDD", ignoreCase = true) ||
                            producto.nombre.contains("Disco", ignoreCase = true)
                    "Placas Madre" -> producto.nombre.contains("Placa", ignoreCase = true) ||
                            producto.nombre.contains("Motherboard", ignoreCase = true)
                    "Fuentes" -> producto.nombre.contains("Fuente", ignoreCase = true) ||
                            producto.nombre.contains("PSU", ignoreCase = true)
                    "Gabinetes" -> producto.nombre.contains("Gabinete", ignoreCase = true) ||
                            producto.nombre.contains("Case", ignoreCase = true)
                    "Periféricos" -> producto.nombre.contains("Mouse", ignoreCase = true) ||
                            producto.nombre.contains("Teclado", ignoreCase = true) ||
                            producto.nombre.contains("Monitor", ignoreCase = true) ||
                            producto.nombre.contains("Audífono", ignoreCase = true)
                    else -> true
                }
            }

            coincideBusqueda && coincideCategoria
        }
    }

    LaunchedEffect(Unit) {
        productoViewModel.cargarProductos()
    }

    // Gradiente animado para el fondo
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
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f + gradientOffset * 0.2f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f + (1f - gradientOffset) * 0.2f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f + gradientOffset * 0.3f)
        )
    )

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = (screenWidth / 2) - 16.dp

    // Animación mejorada para el icono carrito con rotación
    var carritoAnimado by remember { mutableStateOf(false) }
    val escalaCarrito by animateFloatAsState(
        targetValue = if (carritoAnimado) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "carritoScale",
        finishedListener = {
            if (carritoAnimado) {
                carritoAnimado = false
            }
        }
    )

    val rotacionCarrito by animateFloatAsState(
        targetValue = if (carritoAnimado) 15f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "carritoRotation"
    )

    LaunchedEffect(carritoProductos.size) {
        if (carritoProductos.isNotEmpty()) {
            carritoAnimado = true
        }
    }

    // Animación de aparición para toda la pantalla
    var screenVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        screenVisible = true
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = screenVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(800, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(800))
                    ) {
                        Text("🛍️ MODO CLIENTE")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        IconButton(
                            onClick = { navController.navigate(Routes.CARRITO) },
                            modifier = Modifier
                                .scale(escalaCarrito)
                                .rotate(rotacionCarrito)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Ver carrito",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        this@CenterAlignedTopAppBar.AnimatedVisibility(
                            visible = carritoProductos.isNotEmpty(),
                            enter = scaleIn(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessHigh
                                )
                            ) + fadeIn(),
                            exit = scaleOut(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessHigh
                                )
                            ) + fadeOut()
                        ) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp),
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                AnimatedContent(
                                    targetState = carritoProductos.size,
                                    transitionSpec = {
                                        if (targetState > initialState) {
                                            (slideInVertically { -it } + fadeIn()).togetherWith(
                                                slideOutVertically { it } + fadeOut())
                                        } else {
                                            (slideInVertically { it } + fadeIn()).togetherWith(
                                                slideOutVertically { -it } + fadeOut())
                                        }.using(SizeTransform(clip = false))
                                    },
                                    label = "badgeContent"
                                ) { count ->
                                    Text(text = count.toString())
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(brush = gradientBrush)
        ) {

            // AlertDialog mejorado para imagen ampliada con zoom
            productoSeleccionado?.let { imagenUrl ->
                var scale by remember { mutableStateOf(1f) }
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }

                AlertDialog(
                    onDismissRequest = {
                        productoSeleccionado = null
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            productoSeleccionado = null
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        }) {
                            Text("Cerrar")
                        }
                    },
                    text = {
                        AsyncImage(
                            model = imagenUrl,
                            contentDescription = "Imagen ampliada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .pointerInput(Unit) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        scale = (scale * zoom).coerceIn(1f, 5f)
                                        offsetX += pan.x
                                        offsetY += pan.y
                                    }
                                }
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offsetX,
                                    translationY = offsetY
                                ),
                            contentScale = ContentScale.Fit,
                            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                            error = painterResource(id = android.R.drawable.ic_menu_report_image)
                        )
                    }
                )
            }

            AnimatedVisibility(
                visible = screenVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(1000))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Título animado
                    AnimatedVisibility(
                        visible = screenVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(800, delayMillis = 200)
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 200))
                    ) {
                        Text(
                            text = "¡Bienvenido a S-Tech Computers!",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedVisibility(
                        visible = screenVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(800, delayMillis = 400)
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Descubre la mejor selección de componentes y equipos gamer de alta calidad.\nCompra con confianza y recibe tu pedido en cualquier rincón de Colombia. 🇨🇴📦🚚",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

// Buscador animado mejorado y estilizado (sin shadow)
                    AnimatedVisibility(
                        visible = screenVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(800, delayMillis = 600)
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 600))
                    ) {
                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { textoBusqueda = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White),
                            placeholder = {
                                Text(
                                    text = "Buscar productos...",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                if (textoBusqueda.isNotEmpty()) {
                                    IconButton(onClick = { textoBusqueda = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Limpiar búsqueda",
                                            tint = Color.Red.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                focusedBorderColor = CyberpunkPrimary,
                                unfocusedBorderColor = Color.LightGray,
                                cursorColor = CyberpunkPrimary,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

// Filtro de categorías animado (sin shadow)
                    AnimatedVisibility(
                        visible = screenVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(800, delayMillis = 800)
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 800))
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(categorias) { categoria ->
                                val isSelected = categoria == categoriaSeleccionada

                                val backgroundColor by animateColorAsState(
                                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                                    animationSpec = tween(300),
                                    label = "backgroundColor"
                                )

                                val textColor by animateColorAsState(
                                    targetValue = if (isSelected) Color.White else Color.Black,
                                    animationSpec = tween(300),
                                    label = "textColor"
                                )

                                val scale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.05f else 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "scale"
                                )

                                FilterChip(
                                    onClick = { categoriaSeleccionada = categoria },
                                    label = {
                                        Text(
                                            text = categoria,
                                            color = textColor,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    selected = isSelected,
                                    modifier = Modifier.scale(scale),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = backgroundColor,
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        labelColor = textColor
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        isLoading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                // Loading animado mejorado
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val loadingTransition = rememberInfiniteTransition(label = "loading")
                                    val rotation by loadingTransition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 360f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(durationMillis = 1200, easing = LinearEasing),
                                            repeatMode = RepeatMode.Restart
                                        ),
                                        label = "loadingRotation"
                                    )

                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .rotate(rotation)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Cargando productos...",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        !errorMessage.isNullOrEmpty() -> {
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically() + fadeIn()
                            ) {
                                Text(
                                    text = "Ocurrió un error: $errorMessage",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        productosFiltrados.isEmpty() -> {
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically() + fadeIn()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (textoBusqueda.isNotEmpty() || categoriaSeleccionada != "Todas") {
                                            "No se encontraron productos con los filtros aplicados 🔍"
                                        } else {
                                            "Actualmente no contamos con productos disponibles. Por favor, revisa más tarde. 🕒"
                                        },
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )

                                    if (textoBusqueda.isNotEmpty() || categoriaSeleccionada != "Todas") {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                textoBusqueda = ""
                                                categoriaSeleccionada = "Todas"
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.White,
                                                contentColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Text("Limpiar filtros")
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(productosFiltrados, key = { it.id ?: 0L }) { producto ->

                                    // Control de animación local por producto mejorado
                                    var clicked by remember { mutableStateOf(false) }
                                    var isVisible by remember { mutableStateOf(false) }
                                    val scope = rememberCoroutineScope()

                                    LaunchedEffect(Unit) {
                                        delay(productosFiltrados.indexOf(producto) * 100L) // Stagger animation
                                        isVisible = true
                                    }

                                    val scale by animateFloatAsState(
                                        targetValue = if (clicked) 1.1f else 1f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        ),
                                        label = "cardScale"
                                    )

                                    val elevation by animateFloatAsState(
                                        targetValue = if (clicked) 12f else 4f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        ),
                                        label = "cardElevation"
                                    )

                                    AnimatedVisibility(
                                        visible = isVisible,
                                        enter = slideInVertically(
                                            initialOffsetY = { it },
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        ) + fadeIn(
                                            animationSpec = tween(600)
                                        ) + scaleIn(
                                            initialScale = 0.8f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .width(cardWidth)
                                                .height(380.dp)
                                                .scale(scale),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            elevation = CardDefaults.cardElevation(elevation.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp),
                                                verticalArrangement = Arrangement.SpaceBetween,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                if (producto.imagenUrl.isNotBlank()) {
                                                    var imageLoaded by remember { mutableStateOf(false) }

                                                    Box {
                                                        AsyncImage(
                                                            model = producto.imagenUrl,
                                                            contentDescription = "Imagen del producto",
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(140.dp)
                                                                .clip(MaterialTheme.shapes.medium)
                                                                .clickable { productoSeleccionado = producto.imagenUrl }
                                                                .graphicsLayer(
                                                                    alpha = if (imageLoaded) 1f else 0f
                                                                ),
                                                            contentScale = ContentScale.Crop,
                                                            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                                            error = painterResource(id = android.R.drawable.ic_menu_report_image),
                                                            onSuccess = { imageLoaded = true }
                                                        )

                                                        if (!imageLoaded) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .height(140.dp)
                                                                    .clip(MaterialTheme.shapes.medium)
                                                                    .background(Color.Gray.copy(alpha = 0.2f)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                CircularProgressIndicator(
                                                                    modifier = Modifier.size(24.dp),
                                                                    strokeWidth = 2.dp
                                                                )
                                                            }
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(
                                                    text = producto.nombre,
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 3,
                                                    overflow = TextOverflow.Ellipsis,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center
                                                )

                                                Text(
                                                    text = producto.descripcion,
                                                    color = Color.DarkGray,
                                                    maxLines = 3,
                                                    overflow = TextOverflow.Ellipsis,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Justify
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = "💸 PRECIO: ${formatearPesos(producto.precio)}",
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center
                                                )

                                                Text(
                                                    text = "📦 STOCK: ${producto.stock}",
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                var isPressed by remember { mutableStateOf(false) }
                                                val iconScale by animateFloatAsState(
                                                    targetValue = if (isPressed) 0.95f else 1f,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessMedium
                                                    ),
                                                    label = "iconScale"
                                                )

                                                val interactionSource = remember { MutableInteractionSource() }

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(48.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            brush = Brush.horizontalGradient(
                                                                colors = listOf(
                                                                    MaterialTheme.colorScheme.primary,
                                                                    MaterialTheme.colorScheme.secondary
                                                                )
                                                            )
                                                        )
                                                        .clickable(
                                                            interactionSource = interactionSource,
                                                            indication = null
                                                        ) {
                                                            clicked = true
                                                            productoViewModel.agregarAlCarrito(producto)

                                                            scope.launch {
                                                                isPressed = true
                                                                delay(100)
                                                                isPressed = false
                                                                Toast.makeText(
                                                                    context,
                                                                    "${producto.nombre} agregado al carrito 🛒",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                delay(200)
                                                                clicked = false
                                                            }
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ShoppingCart,
                                                        contentDescription = "Agregar al carrito",
                                                        tint = Color.White,
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .graphicsLayer(
                                                                scaleX = iconScale,
                                                                scaleY = iconScale
                                                            )
                                                    )
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}