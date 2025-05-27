package com.example.tiendapcgamer.ui.pantallas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("unused")
fun ImagenAmpliadaScreen(
    navController: NavHostController,
    imagenUrlEncoded: String
) {
    // Decodificamos la URL para evitar problemas con caracteres especiales
    val imagenUrl = URLDecoder.decode(imagenUrlEncoded, StandardCharsets.UTF_8.toString())

    // Estados para las animaciones
    var visible by remember { mutableStateOf(false) }
    var imageLoaded by remember { mutableStateOf(false) }

    // Iniciar animaciones al cargar la pantalla
    LaunchedEffect(Unit) {
        visible = true
    }

    // Animaciones
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "backgroundAlpha"
    )

    val topBarTranslation by animateDpAsState(
        targetValue = if (visible) 0.dp else (-100).dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "topBarTranslation"
    )

    val imageScale by animateFloatAsState(
        targetValue = if (visible && imageLoaded) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "imageScale"
    )

    val imageAlpha by animateFloatAsState(
        targetValue = if (imageLoaded) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "imageAlpha"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Imagen ampliada",
                        modifier = Modifier
                            .alpha(backgroundAlpha)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            visible = false
                            // Pequeño delay antes de navegar para mostrar la animación
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .alpha(backgroundAlpha)
                            .scale(if (visible) 1f else 0.8f)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                },
                modifier = Modifier.offset(y = topBarTranslation)
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = backgroundAlpha)
                    )
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                // Indicador de carga con animación
                if (!imageLoaded) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .alpha(1f - imageAlpha),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                AsyncImage(
                    model = imagenUrl,
                    contentDescription = "Imagen ampliada",
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(imageScale)
                        .alpha(imageAlpha),
                    contentScale = ContentScale.Fit,
                    onSuccess = {
                        imageLoaded = true
                    }
                )

                // Efecto de fade in para toda la pantalla
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    ) + scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    ),
                    exit = fadeOut(
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = FastOutLinearInEasing
                        )
                    ) + scaleOut(
                        targetScale = 0.9f,
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = FastOutLinearInEasing
                        )
                    )
                ) {
                    // Overlay invisible para manejar gestos si es necesario
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    )
}