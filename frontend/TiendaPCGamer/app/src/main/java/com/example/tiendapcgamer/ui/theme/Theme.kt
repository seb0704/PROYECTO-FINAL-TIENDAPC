package com.example.tiendapcgamer.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Paleta moderna, sobria, con colores más vivos pero elegantes
private val VibrantBlue = Color(0xFF1E40AF)       // Azul vibrante, vivo pero serio
private val SteelGray = Color(0xFF4B5563)         // Gris acero moderno, frío y profesional
private val SoftGray = Color(0xFF9CA3AF)          // Gris suave para textos secundarios
private val CleanWhite = Color(0xFFF9FAFB)        // Blanco limpio, no puro, para contraste
private val DarkSlate = Color(0xFF0F172A)         // Fondo oscuro pero con más cuerpo, azul oscuro
private val AlertRed = Color(0xFFEF4444)          // Rojo alerta, vibrante pero elegante

private val TiendaColorScheme = darkColorScheme(
    primary = VibrantBlue,          // Azul vivo para llamar la atención sin perder profesionalismo
    onPrimary = CleanWhite,         // Texto claro sobre primary
    secondary = SteelGray,          // Gris acero para secundario
    onSecondary = CleanWhite,       // Texto claro secundario
    background = DarkSlate,         // Fondo oscuro más profundo y con un azul grisáceo
    onBackground = SoftGray,        // Texto gris claro para no saturar la vista
    surface = SteelGray,            // Superficies con gris acero para dar profundidad
    onSurface = CleanWhite,         // Texto claro sobre superficies
    error = AlertRed,               // Rojo para errores, vibrante y claro
    onError = CleanWhite
)

private val Shapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun TiendaPCGamerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TiendaColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
