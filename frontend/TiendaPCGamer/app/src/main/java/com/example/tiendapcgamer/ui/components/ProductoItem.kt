package com.example.tiendapcgamer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.RoundedCornersTransformation
import com.example.tiendapcgamer.data.model.Producto
import androidx.compose.ui.platform.LocalContext
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductoItem(producto: Producto) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(producto.imagenUrl)
            .crossfade(true)
            .size(Size.ORIGINAL)
            .build()
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painter,
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, style = MaterialTheme.typography.titleLarge)
                Text(text = producto.descripcion, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = formatearPrecio(producto.precio),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

fun formatearPrecio(precio: Double): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    return formato.format(precio)
}
