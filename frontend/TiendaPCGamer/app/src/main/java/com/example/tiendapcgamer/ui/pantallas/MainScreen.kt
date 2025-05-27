package com.example.tiendapcgamer.ui.pantallas

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation // Importa VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lint.kotlin.metadata.Visibility
import androidx.navigation.NavController
import com.example.tiendapcgamer.R
import com.example.tiendapcgamer.ui.theme.*
import kotlinx.coroutines.delay

// Credenciales para el modo empresa (manteniendo el nombre y la contraseña correctos)
private const val CORRECT_USERNAME = "empleado"
private const val CORRECT_PASSWORD = "admin123"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(navController: NavController) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loginSuccess by remember { mutableStateOf(false) }
    var showWelcomeAnimation by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Animaciones de entrada al iniciar la pantalla
    LaunchedEffect(Unit) {
        delay(100)
        showWelcomeAnimation = true
    }

    // Lógica de autenticación
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(1500) // Simula el tiempo de verificación
            if (usernameInput == CORRECT_USERNAME && passwordInput == CORRECT_PASSWORD) {
                loginSuccess = true
                delay(800) // Muestra el éxito por un momento
                showPasswordDialog = false
                usernameInput = ""
                passwordInput = ""
                navController.navigate("empresa") // Navega al modo empresa
            } else {
                inputError = true // Muestra error si las credenciales son incorrectas
            }
            isLoading = false
        }
    }

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
        ),
        start = Offset(0f, 0f),
        end = Offset.Infinite
    )

    // Animaciones para los elementos principales de la interfaz
    val logoScale by animateFloatAsState(
        targetValue = if (showWelcomeAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (showWelcomeAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "contentAlpha"
    )

    val buttonsTranslation by animateDpAsState(
        targetValue = if (showWelcomeAnimation) 0.dp else 50.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonsTranslation"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "TIENDA S-TECH COMPUTERS",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightGrayText,
                        modifier = Modifier
                            .shadow(
                                elevation = 2.dp,
                                spotColor = Color.Black.copy(alpha = 0.2f)
                            )
                            .graphicsLayer {
                                alpha = contentAlpha
                                translationY = if (showWelcomeAnimation) 0f else -50f
                            }
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo con animación de entrada
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo empresa",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .shadow(8.dp, CircleShape)
                            .border(2.dp, CyberpunkPrimaryLight, CircleShape)
                            .scale(logoScale),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Texto de bienvenida con animación
                    AnimatedVisibility(
                        visible = showWelcomeAnimation,
                        enter = slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(durationMillis = 600, delayMillis = 300)
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = 600)
                        )
                    ) {
                        Text(
                            text = "BIENVENIDO",
                            style = TextStyle(
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 3.sp,
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.45f),
                                    offset = Offset(3f, 3f),
                                    blurRadius = 6f
                                )
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Descripción con animación
                    AnimatedVisibility(
                        visible = showWelcomeAnimation,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 600)
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = 600)
                        )
                    ) {
                        Text(
                            text = "La mejor tecnología para tu PC gamer 💻, con cotizaciones personalizadas y envíos seguros a todo Colombia 🇨🇴.",
                            color = LightGrayText,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Iconos sociales con animación escalonada
                    AnimatedVisibility(
                        visible = showWelcomeAnimation,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SocialIcon(
                                iconRes = R.drawable.instagram,
                                label = "Instagram",
                                borderColor = CyberpunkPrimaryLight,
                                url = "https://www.instagram.com/stechcomputers_/",
                                context = context,
                                animationDelay = 0
                            )
                            SocialIcon(
                                iconRes = R.drawable.whatsapp,
                                label = "WhatsApp",
                                borderColor = CyberpunkSecondaryLight,
                                url = "https://wa.link/vodd4p",
                                context = context,
                                animationDelay = 100
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Botones con animación mejorada
                    AnimatedVisibility(
                        visible = showWelcomeAnimation && !showPasswordDialog,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = 400)
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeOut(
                            animationSpec = tween(durationMillis = 300)
                        )
                    ) {
                        Column(
                            modifier = Modifier.offset(y = buttonsTranslation)
                        ) {
                            AnimatedButton(
                                text = "MODO EMPRESA",
                                backgroundColor = CyberpunkPrimaryLight,
                                onClick = { showPasswordDialog = true },
                                animationDelay = 0
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            AnimatedButton(
                                text = "MODO CLIENTE",
                                backgroundColor = CyberpunkSecondaryLight,
                                onClick = { navController.navigate("cliente") },
                                animationDelay = 100
                            )
                        }
                    }
                }

                // Texto del desarrollador
                Text(
                    text = "Hecho por Sebastian Rojas Sanchez",
                    color = LightGrayText.copy(alpha = 0.6f * contentAlpha),
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )

                // Diálogo mejorado con animaciones
                AnimatedVisibility(
                    visible = showPasswordDialog,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 300)
                    ),
                    exit = scaleOut(
                        targetScale = 0.8f,
                        animationSpec = tween(durationMillis = 200)
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 200)
                    )
                ) {
                    UserPasswordDialog(
                        usernameInput = usernameInput,
                        onUsernameChange = {
                            usernameInput = it
                            inputError = false
                        },
                        passwordInput = passwordInput,
                        onPasswordChange = {
                            passwordInput = it
                            inputError = false
                        },
                        inputError = inputError,
                        isLoading = isLoading,
                        loginSuccess = loginSuccess,
                        onDismiss = {
                            showPasswordDialog = false
                            usernameInput = ""
                            passwordInput = ""
                            inputError = false
                            isLoading = false
                            loginSuccess = false
                        },
                        onConfirm = {
                            if (!isLoading) {
                                isLoading = true
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun AnimatedButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    animationDelay: Int
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )

    Button(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp,
            focusedElevation = 12.dp
        )
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(150)
            pressed = false
        }
    }
}

@Composable
fun SocialIcon(
    iconRes: Int,
    label: String,
    borderColor: Color,
    url: String,
    context: android.content.Context,
    animationDelay: Int
) {
    var visible by remember { mutableStateOf(false) }
    var pressed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = when {
            pressed -> 0.9f
            visible -> 1f
            else -> 0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "socialIconScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, borderColor, CircleShape)
                .clickable {
                    pressed = true
                    val uri = Uri.parse(url)
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(100)
            pressed = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPasswordDialog(
    usernameInput: String,
    onUsernameChange: (String) -> Unit,
    passwordInput: String,
    onPasswordChange: (String) -> Unit,
    inputError: Boolean,
    isLoading: Boolean,
    loginSuccess: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    // Estado para controlar la visibilidad de la contraseña
    var passwordVisible by remember { mutableStateOf(false) }

    // Animación de shake para el error
    val shakeOffset by animateFloatAsState(
        targetValue = if (inputError) 10f else 0f,
        animationSpec = keyframes {
            durationMillis = 300
            0f at 0
            10f at 50
            -10f at 100
            10f at 150
            -10f at 200
            5f at 250
            0f at 300
        },
        label = "shakeOffset"
    )

    AlertDialog(
        // FIX: Ensure onDismissRequest always provides a () -> Unit lambda
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Animación del icono
                    val iconRotation by animateFloatAsState(
                        targetValue = if (loginSuccess) 360f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "iconRotation"
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.candado),
                        contentDescription = "Icono candado",
                        tint = if (loginSuccess) Color.Green else CyberpunkPrimary,
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer {
                                rotationZ = iconRotation
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Inicio de sesión Restringido",
                        color = LightGrayText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Acceso restringido para administradores autorizados de S-Tech Computers. Ingrese sus credenciales para gestionar productos y servicios.",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(x = shakeOffset.dp)
            ) {
                // Campo de usuario
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = onUsernameChange,
                    label = { Text("Usuario", color = LightGrayText) },
                    singleLine = true,
                    isError = inputError,
                    enabled = !isLoading, // Deshabilita el campo mientras carga
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (loginSuccess) Color.Green else CyberpunkPrimary,
                        errorBorderColor = CyberpunkError,
                        cursorColor = CyberpunkPrimary,
                        focusedLabelColor = if (loginSuccess) Color.Green else CyberpunkPrimary,
                        errorLabelColor = CyberpunkError,
                        unfocusedLabelColor = LightGrayText,
                        unfocusedBorderColor = LightGrayText.copy(alpha = 0.5f),
                        errorCursorColor = CyberpunkError
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo de contraseña con icono de visibilidad
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = onPasswordChange,
                    label = { Text("Contraseña", color = LightGrayText) },
                    // Alterna la visibilidad
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    isError = inputError,
                    enabled = !isLoading, // Deshabilita el campo mientras carga
                    trailingIcon = { // Icono al final del campo
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description, tint = LightGrayText)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (loginSuccess) Color.Green else CyberpunkPrimary,
                        errorBorderColor = CyberpunkError,
                        cursorColor = CyberpunkPrimary,
                        focusedLabelColor = if (loginSuccess) Color.Green else CyberpunkPrimary,
                        errorLabelColor = CyberpunkError,
                        unfocusedLabelColor = LightGrayText,
                        unfocusedBorderColor = LightGrayText.copy(alpha = 0.5f),
                        errorCursorColor = CyberpunkError
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Estados de carga y éxito
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = CyberpunkPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Verificando credenciales...",
                            color = LightGrayText,
                            fontSize = 14.sp
                        )
                    }
                }

                AnimatedVisibility(
                    visible = loginSuccess,
                    enter = fadeIn() + slideInVertically { -it },
                    exit = fadeOut()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "✓ Acceso concedido",
                            color = Color.Green,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                AnimatedVisibility(
                    visible = inputError && !isLoading,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut()
                ) {
                    Text(
                        text = "❌ Usuario o contraseña incorrectos",
                        color = CyberpunkError,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        // Posicionamiento de botones: Cancelar a la izquierda, Ingresar a la derecha
        properties = DialogProperties(usePlatformDefaultWidth = false), // Permite controlar el ancho de los botones
        confirmButton = {
            // Este es el botón "Ingresar" que irá a la derecha
            AnimatedVisibility(
                visible = !isLoading && !loginSuccess,
                enter = slideInHorizontally { it } + fadeIn(),
                exit = slideOutHorizontally { it } + fadeOut()
            ) {
                TextButton(
                    onClick = onConfirm,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Ingresar",
                        color = Color.Green,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center // Alineación central para el texto del botón
                    )
                }
            }
        },
        dismissButton = {
            // Este es el botón "Cancelar" que irá a la izquierda
            AnimatedVisibility(
                visible = !isLoading,
                enter = slideInHorizontally { -it } + fadeIn(),
                exit = slideOutHorizontally { -it } + fadeOut()
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center // Alineación central para el texto del botón
                    )
                }
            }
        },
        shape = RoundedCornerShape(25.dp),
        containerColor = DarkSurface,
        tonalElevation = 16.dp
    )
}