package com.example.tiendapcgamer.ui.pantallas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiendapcgamer.R
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun CargandoScreen(onTimeout: () -> Unit) {

    var logoVisible by remember { mutableStateOf(false) }
    var progressVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var dotAnimationActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        delay(300)
        logoVisible = true
        delay(600)
        progressVisible = true
        delay(400)
        textVisible = true
        delay(200)
        dotAnimationActive = true

        delay(7000)
        onTimeout()
    }

    val density = LocalDensity.current

    // Gradiente animado de fondo
    val infiniteTransition = rememberInfiniteTransition()

    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f + gradientOffset * 0.1f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f + gradientOffset * 0.15f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f + gradientOffset * 0.2f)
        )
    )

    // Animación de rotación para el indicador de progreso
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Animación de escala pulsante para el logo
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animación de brillo/glow para el logo
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animación flotante (arriba/abajo) para todo el contenedor
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animación de puntos suspensivos
    val dotAnimation1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val dotAnimation2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing, delayMillis = 250),
            repeatMode = RepeatMode.Restart
        )
    )

    val dotAnimation3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing, delayMillis = 500),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Partículas de fondo animadas
        repeat(8) { index ->
            val particleOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (3000 + index * 500),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )

            Box(
                modifier = Modifier
                    .size(4.dp)
                    .offset(
                        x = (150 * sin(Math.toRadians(particleOffset.toDouble() + index * 45))).dp,
                        y = (100 * sin(Math.toRadians(particleOffset.toDouble() * 1.5 + index * 30))).dp
                    )
                    .background(
                        Color.White.copy(alpha = 0.3f + (sin(Math.toRadians(particleOffset.toDouble())) * 0.2f).toFloat()),
                        CircleShape
                    )
            )
        }

        // Contenedor principal con movimiento flotante
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = floatingOffset.dp)
        ) {
            // Logo con animaciones de entrada y efectos continuos
            AnimatedVisibility(
                visible = logoVisible,
                enter = scaleIn(
                    animationSpec = tween(800, easing = EaseOutBack)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Círculo de resplandor de fondo
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.White.copy(alpha = glowIntensity * 0.2f),
                                CircleShape
                            )
                    )

                    // Imagen del logo
                    Image(
                        painter = painterResource(id = R.drawable.logo2),
                        contentDescription = "Logo de carga",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .graphicsLayer {
                                scaleX = logoScale
                                scaleY = logoScale
                                alpha = 0.95f + glowIntensity * 0.05f
                            }
                    )

                    // Indicador de progreso animado
                    this@Column.AnimatedVisibility(
                        visible = progressVisible,
                        enter = scaleIn(
                            animationSpec = tween(500, delayMillis = 200, easing = EaseOutCubic)
                        ) + fadeIn(animationSpec = tween(400, delayMillis = 200))
                    ) {
                        CircularProgressIndicator(
                            color = Color.White.copy(alpha = 0.8f),
                            strokeWidth = 6.dp,
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(rotation)
                                .graphicsLayer {
                                    alpha = 0.7f + sin(Math.toRadians(rotation.toDouble())).toFloat() * 0.3f
                                }
                        )
                    }

                    // Anillo exterior decorativo
                    this@Column.AnimatedVisibility(
                        visible = progressVisible,
                        enter = scaleIn(
                            animationSpec = tween(600, delayMillis = 400, easing = EaseOutBack)
                        )
                    ) {
                        val outerRingRotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = -360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 3000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )

                        CircularProgressIndicator(
                            progress = { 0.25f },
                            modifier = Modifier
                                .size(340.dp)
                                .rotate(outerRingRotation),
                            color = Color.White.copy(alpha = 0.4f),
                            strokeWidth = 3.dp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Texto de carga con animación de entrada y puntos animados
            AnimatedVisibility(
                visible = textVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(500))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Cargando app",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Puntos suspensivos animados
                    AnimatedVisibility(
                        visible = dotAnimationActive,
                        enter = fadeIn(animationSpec = tween(300))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            repeat(3) { index ->
                                val dotScale = when (index) {
                                    0 -> if (dotAnimation1 < 0.5f) 1f + dotAnimation1 * 0.5f else 1.5f - (dotAnimation1 - 0.5f) * 0.5f
                                    1 -> if (dotAnimation2 < 0.5f) 1f + dotAnimation2 * 0.5f else 1.5f - (dotAnimation2 - 0.5f) * 0.5f
                                    else -> if (dotAnimation3 < 0.5f) 1f + dotAnimation3 * 0.5f else 1.5f - (dotAnimation3 - 0.5f) * 0.5f
                                }

                                Text(
                                    text = "•",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .graphicsLayer {
                                            scaleX = dotScale
                                            scaleY = dotScale
                                        }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Texto adicional con fade suave
                    AnimatedVisibility(
                        visible = dotAnimationActive,
                        enter = fadeIn(animationSpec = tween(800, delayMillis = 500))
                    ) {
                        Text(
                            text = "Preparando tu experiencia de compra",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 16.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    alpha = 0.6f + sin(Math.toRadians((gradientOffset * 360).toDouble())).toFloat() * 0.2f
                                }
                        )
                    }
                }
            }
        }
    }
}