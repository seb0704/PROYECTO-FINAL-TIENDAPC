package com.example.tiendapcgamer.ui.pantallas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiendapcgamer.ui.components.NotificationBanner
import com.example.tiendapcgamer.viewmodel.EstadoPago
import com.example.tiendapcgamer.viewmodel.ProductoViewModel
import com.example.tiendapcgamer.viewmodel.SolicitudPago
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.tiendapcgamer.data.model.Producto
import com.example.tiendapcgamer.data.util.formatearPesos

@Composable
fun SolicitudPagoCard(
    solicitud: SolicitudPago,
    onConfirm: (SolicitudPago) -> Unit,
    onReject: (SolicitudPago) -> Unit,
    productoViewModel: ProductoViewModel // Necesario para el resumen
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Solicitud #${solicitud.id.take(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(solicitud.fechaSolicitud)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cliente: ${solicitud.clienteInfo ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Total: ${formatearPesos(solicitud.total)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Resumen de productos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            ) {
                solicitud.productos.forEach { (producto, cantidad) ->
                    Text(
                        text = "• ${producto.nombre} x$cantidad",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (solicitud.estado == EstadoPago.PENDIENTE) {
                    Button(
                        onClick = { onConfirm(solicitud) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirmar ✅", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onReject(solicitud) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rechazar ❌", color = Color.White)
                    }
                } else {
                    val statusColor = when (solicitud.estado) {
                        EstadoPago.CONFIRMADO -> Color(0xFF4CAF50)
                        EstadoPago.RECHAZADO -> Color(0xFFF44336)
                        EstadoPago.PENDIENTE -> MaterialTheme.colorScheme.onSurfaceVariant // Should not happen here
                    }
                    Text(
                        text = "Estado: ${solicitud.estado.name}",
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    productoViewModel: ProductoViewModel,
    onBack: () -> Unit
) {
    val solicitudesPagoPendientes by productoViewModel.solicitudesPagoPendientes.collectAsState()
    val isLoading by productoViewModel.loading.collectAsState()
    val errorMessage by productoViewModel.errorMessage.collectAsState()
    val successMessage by productoViewModel.successMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var dialogConfirmarPagoAbierto by remember { mutableStateOf(false) }
    var dialogRechazarPagoAbierto by remember { mutableStateOf(false) }
    var solicitudAProcesar by remember { mutableStateOf<SolicitudPago?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "gradientNotification")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffsetNotification"
    )

    val gradientBrush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f + gradientOffset * 0.1f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f + (1f - gradientOffset) * 0.1f)
        )
    )

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
                title = { Text("🔔 Solicitudes de Pago") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            Column(modifier = Modifier.fillMaxSize()) {
                NotificationBanner(
                    message = errorMessage,
                    isError = true
                )
                NotificationBanner(
                    message = successMessage,
                    isError = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (solicitudesPagoPendientes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay solicitudes de pago pendientes o procesadas.",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(solicitudesPagoPendientes, key = { it.id }) { solicitud ->
                            SolicitudPagoCard(
                                solicitud = solicitud,
                                onConfirm = {
                                    solicitudAProcesar = it
                                    dialogConfirmarPagoAbierto = true
                                },
                                onReject = {
                                    solicitudAProcesar = it
                                    dialogRechazarPagoAbierto = true
                                },
                                productoViewModel = productoViewModel
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

            // Dialogs for confirming/rejecting payments (moved from EmpresaScreen)
            if (dialogConfirmarPagoAbierto && solicitudAProcesar != null) {
                AlertDialog(
                    onDismissRequest = { dialogConfirmarPagoAbierto = false },
                    title = { Text("✅ Confirmar Recepción de Pago") },
                    text = {
                        Column {
                            Text("¿Estás seguro de que deseas confirmar la recepción del pago para esta solicitud?")
                            Spacer(Modifier.height(8.dp))
                            Text("Esto actualizará el stock de los productos involucrados.",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            productoViewModel.confirmarPagoRecibido(solicitudAProcesar!!.id)
                            dialogConfirmarPagoAbierto = false
                            solicitudAProcesar = null
                            scope.launch {
                                snackbarHostState.showSnackbar("✅ Pago confirmado. Stock actualizado.")
                            }
                        }) {
                            Text("Confirmar ✅")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogConfirmarPagoAbierto = false }) {
                            Text("Cancelar ❌")
                        }
                    }
                )
            }

            if (dialogRechazarPagoAbierto && solicitudAProcesar != null) {
                AlertDialog(
                    onDismissRequest = { dialogRechazarPagoAbierto = false },
                    title = { Text("❌ Rechazar Solicitud de Pago") },
                    text = {
                        Column {
                            Text("¿Estás seguro de que deseas rechazar esta solicitud de pago?")
                            Spacer(Modifier.height(8.dp))
                            Text("El stock de los productos no se verá afectado.",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            productoViewModel.rechazarPago(solicitudAProcesar!!.id)
                            dialogRechazarPagoAbierto = false
                            solicitudAProcesar = null
                            scope.launch {
                                snackbarHostState.showSnackbar("❌ Solicitud de pago rechazada.")
                            }
                        }) {
                            Text("Rechazar ❌")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogRechazarPagoAbierto = false }) {
                            Text("Cancelar ✅")
                        }
                    }
                )
            }
        }
    }
}