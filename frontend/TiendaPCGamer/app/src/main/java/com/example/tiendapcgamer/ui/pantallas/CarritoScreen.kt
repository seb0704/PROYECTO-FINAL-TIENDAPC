package com.example.tiendapcgamer.ui.pantallas

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tiendapcgamer.R
import com.example.tiendapcgamer.data.model.Producto
import com.example.tiendapcgamer.viewmodel.ProductoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    productoViewModel: ProductoViewModel,
    navController: NavController,
    modoEmpresa: Boolean = false
) {
    val carritoItems by productoViewModel.carrito.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mostrarDialogoEfectivo by remember { mutableStateOf(false) }
    var mostrarDialogoVaciar by remember { mutableStateOf(false) }

    var nombreCliente by remember { mutableStateOf("") }
    var correoCliente by remember { mutableStateOf("") }
    var telefonoCliente by remember { mutableStateOf("") }
    var errorFormulario by remember { mutableStateOf("") }

    var visible by remember { mutableStateOf(false) }
    var totalVisible by remember { mutableStateOf(false) }

    val formatoPesos = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }
    }

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
        )
    )

    val snackbarHostState = remember { SnackbarHostState() }

    val errorMessage by productoViewModel.errorMessage.collectAsState()
    val successMessage by productoViewModel.successMessage.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
            productoViewModel.limpiarError()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
            productoViewModel.limpiarExito()
        }
    }

    fun validarFormulario(): Boolean {
        return when {
            nombreCliente.isBlank() -> {
                errorFormulario = "El nombre es obligatorio."
                false
            }
            correoCliente.isBlank() -> {
                errorFormulario = "El correo es obligatorio."
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correoCliente).matches() -> {
                errorFormulario = "Formato de correo inválido."
                false
            }
            telefonoCliente.isBlank() -> {
                errorFormulario = "El teléfono es obligatorio."
                false
            }
            telefonoCliente.length < 7 || telefonoCliente.length > 10 || !telefonoCliente.all { it.isDigit() } -> { // Teléfono entre 7 y 10 dígitos y solo números
                errorFormulario = "El teléfono debe tener entre 7 y 10 dígitos numéricos."
                false
            }
            else -> {
                errorFormulario = ""
                true
            }
        }
    }

    fun limpiarFormulario() {
        nombreCliente = ""
        correoCliente = ""
        telefonoCliente = ""
        errorFormulario = ""
    }

    LaunchedEffect(Unit) {
        visible = true
        delay(300)
        totalVisible = true
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
                        containerColor = when {
                            data.visuals.message.startsWith("✅") || data.visuals.message.startsWith("📤") -> Color(0xFF25D366) // Éxito o envío
                            data.visuals.message.startsWith("⚠️") -> Color(0xFFFFA000) // Advertencia
                            else -> MaterialTheme.colorScheme.error // Error
                        },
                        contentColor = Color.White,
                        actionColor = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        actionOnNewLine = true
                    )
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(600, easing = EaseOutCubic)
                        ) + fadeIn(animationSpec = tween(600))
                    ) {
                        Text("CARRITO DE COMPRAS", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    AnimatedVisibility(
                        visible = visible,
                        enter = scaleIn(
                            animationSpec = tween(400, delayMillis = 200, easing = EaseOutBack)
                        ) + fadeIn(animationSpec = tween(400, delayMillis = 200))
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.scale(
                                animateFloatAsState(
                                    targetValue = if (visible) 1f else 0f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    label = "back_button_scale"
                                ).value
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = carritoItems.isEmpty() && visible,
                enter = fadeIn(animationSpec = tween(800)) + scaleIn(
                    animationSpec = tween(800, easing = EaseOutBack)
                ),
                exit = fadeOut(animationSpec = tween(400)) + scaleOut(
                    animationSpec = tween(400)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f), // Añadir weight para centrar verticalmente
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🛒",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "El carrito está vacío",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Agrega productos para comenzar tu compra",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = carritoItems.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, easing = EaseOutCubic)
                ),
                exit = fadeOut(animationSpec = tween(400)) + slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(400)
                )
            ) {
                Column {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        itemsIndexed(carritoItems.toList()) { index, (producto, cantidad) ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(
                                        durationMillis = 500,
                                        delayMillis = index * 100,
                                        easing = EaseOutCubic
                                    )
                                ) + fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 500,
                                        delayMillis = index * 100
                                    )
                                ),
                                exit = slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                            ) {
                                CarritoItemCard(
                                    producto = producto,
                                    cantidad = cantidad,
                                    productoViewModel = productoViewModel,
                                    formatoPesos = formatoPesos
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Total animado con mejor diseño
                    AnimatedVisibility(
                        visible = totalVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(600, easing = EaseOutCubic)
                        ) + fadeIn(animationSpec = tween(600))
                    ) {
                        val total = productoViewModel.obtenerTotalCarrito()
                        val animatedTotal by animateIntAsState(
                            targetValue = total.toInt(),
                            animationSpec = tween(800, easing = EaseOutCubic),
                            label = "total_animation"
                        )

                        val scale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "total_scale"
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Total a pagar: ${formatoPesos.format(animatedTotal)}",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedVisibility(
                        visible = totalVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(700, delayMillis = 300, easing = EaseOutCubic)
                        ) + fadeIn(animationSpec = tween(700, delayMillis = 300))
                    ) {
                        Column {
                            Text(
                                text = "Medios de pago",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Grid de 2x2 para métodos de pago
                            val mediosPago = listOf(
                                Triple("Nequi", R.drawable.nequi, "https://www.nequi.com/"),
                                Triple("Daviplata", R.drawable.daviplata, "https://www.daviplata.com/"),
                                Triple("PSE", R.drawable.pse, "https://www.pse.com.co/"),
                                Triple("Efectivo", R.drawable.efectivo, "efectivo")
                            )

                            // Primera fila
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(500, delayMillis = 400, easing = EaseOutCubic)
                                ) + fadeIn(animationSpec = tween(500, delayMillis = 400))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    PagoButton(
                                        nombre = mediosPago[0].first,
                                        icono = mediosPago[0].second,
                                        url = mediosPago[0].third,
                                        context = context,
                                        modoEmpresa = modoEmpresa,
                                        mostrarDialogoEfectivo = { mostrarDialogoEfectivo = true }
                                    )

                                    PagoButton(
                                        nombre = mediosPago[1].first,
                                        icono = mediosPago[1].second,
                                        url = mediosPago[1].third,
                                        context = context,
                                        modoEmpresa = modoEmpresa,
                                        mostrarDialogoEfectivo = { mostrarDialogoEfectivo = true }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Segunda fila
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(500, delayMillis = 500, easing = EaseOutCubic)
                                ) + fadeIn(animationSpec = tween(500, delayMillis = 500))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    PagoButton(
                                        nombre = mediosPago[2].first,
                                        icono = mediosPago[2].second,
                                        url = mediosPago[2].third,
                                        context = context,
                                        modoEmpresa = modoEmpresa,
                                        mostrarDialogoEfectivo = { mostrarDialogoEfectivo = true }
                                    )

                                    PagoButton(
                                        nombre = mediosPago[3].first,
                                        icono = mediosPago[3].second,
                                        url = mediosPago[3].third,
                                        context = context,
                                        modoEmpresa = modoEmpresa,
                                        mostrarDialogoEfectivo = { mostrarDialogoEfectivo = true }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón WhatsApp mejorado
                    AnimatedVisibility(
                        visible = totalVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(600, delayMillis = 600, easing = EaseOutCubic)
                        ) + fadeIn(animationSpec = tween(600, delayMillis = 600))
                    ) {
                        PagoWhatsAppButton(context = context)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón vaciar carrito mejorado
                    AnimatedVisibility(
                        visible = totalVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(600, delayMillis = 700, easing = EaseOutCubic)
                        ) + fadeIn(animationSpec = tween(600, delayMillis = 700))
                    ) {
                        OutlinedButton(
                            onClick = { mostrarDialogoVaciar = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(
                                text = "🗑️ Vaciar Carrito",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Diálogo de efectivo mejorado con mejor layout de texto
        AnimatedVisibility(
            visible = mostrarDialogoEfectivo,
            enter = scaleIn(animationSpec = tween(300, easing = EaseOutBack)) + fadeIn(),
            exit = scaleOut(animationSpec = tween(200)) + fadeOut()
        ) {
            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoEfectivo = false
                    limpiarFormulario()
                },
                title = {
                    Text(
                        text = if (modoEmpresa) "💼 Confirmar Venta" else "💰 Solicitar Pago en Efectivo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp) // Limitar altura para que el scroll funcione si es necesario
                    ) {
                        if (modoEmpresa) {
                            Text(
                                text = "¿Confirmar la venta y actualizar el inventario inmediatamente?",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    text = "✅ Se actualizará el stock automáticamente",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "Tu solicitud de pago será enviada a la empresa para aprobación.",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    text = "⚠️ El producto no se envía hasta que la empresa confirme el pago recibido",
                                    fontSize = 14.sp,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Formulario de cliente con mejor diseño
                            Text(
                                text = "📋 Datos del Cliente",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Green,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Campo Nombre
                            OutlinedTextField(
                                value = nombreCliente,
                                onValueChange = { nombreCliente = it },
                                label = { Text("Nombre completo *") },
                                placeholder = { Text("Ej: Juan Pérez") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = errorFormulario.contains("nombre", ignoreCase = true)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Campo Correo
                            OutlinedTextField(
                                value = correoCliente,
                                onValueChange = { correoCliente = it },
                                label = { Text("Correo electrónico *") },
                                placeholder = { Text("ejemplo@correo.com") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                isError = errorFormulario.contains("correo", ignoreCase = true)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Campo Teléfono
                            OutlinedTextField(
                                value = telefonoCliente,
                                onValueChange = { telefonoCliente = it },
                                label = { Text("Teléfono *") },
                                placeholder = { Text("3001234567") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                isError = errorFormulario.contains("teléfono", ignoreCase = true)
                            )

                            // Mostrar error si existe
                            if (errorFormulario.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                    )
                                ) {
                                    Text(
                                        text = "❌ $errorFormulario",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Resumen del carrito con mejor diseño
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📄 Resumen de compra:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = productoViewModel.obtenerResumenCarrito(),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (modoEmpresa) {
                                // Modo empresa: procesar inmediatamente (venta directa)
                                mostrarDialogoEfectivo = false
                                productoViewModel.procesarPago(null) // Llama con null para indicar venta directa
                                limpiarFormulario()
                            } else {
                                // Modo cliente: validar formulario y enviar solicitud
                                if (validarFormulario()) {
                                    mostrarDialogoEfectivo = false
                                    val clienteData = mapOf(
                                        "nombre" to nombreCliente,
                                        "correo" to correoCliente,
                                        "telefono" to telefonoCliente
                                    )
                                    productoViewModel.procesarPago(clienteData) // Llama con datos del cliente para solicitud
                                    limpiarFormulario()
                                }
                            }
                        }
                    ) {
                        Text(
                            if (modoEmpresa) "Confirmar Venta" else "Enviar Solicitud",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        mostrarDialogoEfectivo = false
                        limpiarFormulario()
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Diálogo vaciar carrito
        AnimatedVisibility(
            visible = mostrarDialogoVaciar,
            enter = scaleIn(animationSpec = tween(300, easing = EaseOutBack)) + fadeIn(),
            exit = scaleOut(animationSpec = tween(200)) + fadeOut()
        ) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoVaciar = false },
                title = { Text("⚠️ Vaciar carrito") },
                text = { Text("¿Estás seguro? Se eliminarán todos los productos del carrito.") },
                confirmButton = {
                    Button( // Cambiado a Button para destacar la acción
                        onClick = {
                            mostrarDialogoVaciar = false
                            productoViewModel.vaciarCarrito()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Sí, vaciar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoVaciar = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun CarritoItemCard(
    producto: Producto,
    cantidad: Int,
    productoViewModel: ProductoViewModel, // Ya no necesita carritoItems, snackbarHostState, scope aquí
    formatoPesos: NumberFormat
) {
    val scope = rememberCoroutineScope() // Crear un scope local para la corrutina
    val snackbarHostState = remember { SnackbarHostState() } // Crear un snackbar local si solo se usa aquí, o pasar desde el padre

    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .scale(scale),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen con mejor diseño
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(animationSpec = tween(400, easing = EaseOutBack))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(producto.imagenUrl),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Precio: ${formatoPesos.format(producto.precio)}",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                // Cantidad animada con mejor diseño
                val animatedCantidad by animateIntAsState(
                    targetValue = cantidad,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "cantidad_animation"
                )

                Text(
                    text = "Subtotal: ${formatoPesos.format(producto.precio * cantidad)}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón restar mejorado
                    var minusPressed by remember { mutableStateOf(false) }
                    val minusScale by animateFloatAsState(
                        targetValue = if (minusPressed) 0.8f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
                        label = "minus_scale"
                    )

                    FilledIconButton(
                        onClick = {
                            minusPressed = true
                            productoViewModel.quitarDelCarrito(producto)
                            scope.launch {
                                delay(100)
                                minusPressed = false
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .scale(minusScale),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "−",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Cantidad con mejor diseño
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "$animatedCantidad",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    // Botón sumar mejorado
                    var plusPressed by remember { mutableStateOf(false) }
                    val plusScale by animateFloatAsState(
                        targetValue = if (plusPressed) 0.8f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
                        label = "plus_scale"
                    )

                    FilledIconButton(
                        onClick = {
                            plusPressed = true
                            productoViewModel.agregarAlCarrito(producto) // ViewModel ya valida stock
                            scope.launch {
                                delay(100)
                                plusPressed = false
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .scale(plusScale),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "+",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoButton(
    nombre: String,
    icono: Int,
    url: String,
    context: Context,
    modoEmpresa: Boolean,
    mostrarDialogoEfectivo: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pago_button_scale"
    )

    Card(
        onClick = {
            if (nombre == "Efectivo") {
                mostrarDialogoEfectivo()
            } else {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {
                }
            }
        },
        modifier = Modifier
            .width(140.dp)
            .height(70.dp)
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(icono),
                contentDescription = "Icono de $nombre",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (nombre == "Efectivo" && modoEmpresa) "Confirmar Venta" else nombre,
                color = Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun PagoWhatsAppButton(context: Context) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "whatsapp_scale"
    )

    Button(
        onClick = {
            val url = "https://wa.me/573001112233?text=Hola,%20quiero%20comprar%20mi%20PC%20Gamer%20%F0%9F%92%BB%F0%9F%94%A5" // Emoji codificado
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF25D366)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "💬 Finalizar por WhatsApp",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}