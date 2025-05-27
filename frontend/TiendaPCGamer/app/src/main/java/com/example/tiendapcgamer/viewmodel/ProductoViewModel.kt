package com.example.tiendapcgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendapcgamer.data.model.Producto
import com.example.tiendapcgamer.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class para representar una solicitud de pago pendiente
data class SolicitudPago(
    val id: String = java.util.UUID.randomUUID().toString(),
    val productos: Map<Producto, Int>,
    val total: Double,
    val fechaSolicitud: Long = System.currentTimeMillis(),
    val estado: EstadoPago = EstadoPago.PENDIENTE,
    val clienteInfo: String? = null
)

enum class EstadoPago {
    PENDIENTE,
    CONFIRMADO,
    RECHAZADO
}

class ProductoViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> get() = _productos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> get() = _successMessage

    private val _carrito = MutableStateFlow<Map<Producto, Int>>(emptyMap())
    val carrito: StateFlow<Map<Producto, Int>> get() = _carrito

    private val _solicitudesPagoPendientes = MutableStateFlow<List<SolicitudPago>>(emptyList())
    val solicitudesPagoPendientes: StateFlow<List<SolicitudPago>> get() = _solicitudesPagoPendientes

    private val _nuevaSolicitudPago = MutableStateFlow<SolicitudPago?>(null)
    val nuevaSolicitudPago: StateFlow<SolicitudPago?> get() = _nuevaSolicitudPago

    // ========== Funciones CRUD del Backend ==========

    fun cargarProductos() {
        viewModelScope.launch {
            _loading.value = true
            limpiarError() // Limpiar error anterior al iniciar una nueva carga
            limpiarExito() // Limpiar éxito anterior
            try {
                val response = api.obtenerProductos()
                if (response.isSuccessful) {
                    _productos.value = response.body().orEmpty()
                } else {
                    _errorMessage.value = "Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar productos: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _loading.value = true
            limpiarError()
            limpiarExito()
            try {
                val response = api.agregarProducto(producto)
                if (response.isSuccessful) {
                    _successMessage.value = "Producto agregado exitosamente"
                    cargarProductos() // Recargar para reflejar cambios
                } else {
                    _errorMessage.value = "Error al agregar: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al agregar producto: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            _loading.value = true
            limpiarError()
            limpiarExito()
            try {
                val id = producto.id ?: run {
                    _errorMessage.value = "ID del producto inválido para actualizar"
                    _loading.value = false
                    return@launch
                }
                val response = api.actualizarProducto(id, producto)
                if (response.isSuccessful) {
                    _successMessage.value = "Producto actualizado exitosamente"
                    cargarProductos() // Recargar para reflejar cambios
                } else {
                    _errorMessage.value = "Error al actualizar: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar producto: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun eliminarProducto(id: Long) {
        viewModelScope.launch {
            _loading.value = true
            limpiarError()
            limpiarExito()
            try {
                val response = api.eliminarProducto(id)
                if (response.isSuccessful) {
                    _successMessage.value = "Producto eliminado exitosamente"
                    cargarProductos() // Recargar para reflejar cambios
                } else {
                    _errorMessage.value = "Error al eliminar: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar producto: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ========== Validación de Campos de Producto ==========

    fun validarCampos(
        nombre: String,
        descripcion: String,
        precio: String,
        stock: String,
        marcaId: String,
        categoriaId: String
    ): Boolean {
        return nombre.isNotBlank() &&
                descripcion.isNotBlank() &&
                precio.toDoubleOrNull() != null &&
                stock.toIntOrNull() != null &&
                marcaId.toIntOrNull() != null &&
                categoriaId.toIntOrNull() != null
    }

    // ========== Carrito de Compras ==========

    fun agregarAlCarrito(producto: Producto) {
        val productoEnLista = _productos.value.find { it.id == producto.id }
        if (productoEnLista == null) {
            _errorMessage.value = "Error: Producto no encontrado en el inventario."
            return
        }

        val cantidadActualEnCarrito = _carrito.value[producto] ?: 0
        if (cantidadActualEnCarrito >= productoEnLista.stock) {
            _errorMessage.value = "No hay más stock disponible de ${producto.nombre}."
            return
        }

        val carritoActual = _carrito.value.toMutableMap()
        carritoActual[producto] = cantidadActualEnCarrito + 1
        _carrito.value = carritoActual
        limpiarError() // Limpiar cualquier error anterior si se agregó con éxito
    }

    fun quitarDelCarrito(producto: Producto) {
        val carritoActual = _carrito.value.toMutableMap()
        val cantidad = carritoActual[producto] ?: return
        if (cantidad <= 1) {
            carritoActual.remove(producto)
        } else {
            carritoActual[producto] = cantidad - 1
        }
        _carrito.value = carritoActual
        limpiarError()
    }

    fun aumentarCantidad(producto: Producto) {
        agregarAlCarrito(producto) // La lógica de stock ya está en agregarAlCarrito
    }

    fun disminuirCantidad(producto: Producto) {
        quitarDelCarrito(producto)
    }

    fun vaciarCarrito() {
        _carrito.value = emptyMap()
        limpiarError()
        limpiarExito()
    }

    // ========== Flujo de Pago (Unificado para Cliente y Empresa) ==========

    // Función unificada para solicitar pago (cliente) o procesar venta directa (empresa)
    fun procesarPago(clienteData: Map<String, String>? = null) {
        if (_carrito.value.isEmpty()) {
            _errorMessage.value = "El carrito está vacío."
            return
        }

        if (!validarStockDisponibleEnCarrito()) { // Usar la validación de stock del carrito
            return
        }

        viewModelScope.launch {
            _loading.value = true
            limpiarError()
            limpiarExito()
            try {
                // Determine si es una venta directa (modo empresa) o una solicitud de pago (cliente)
                val isDirectSale = clienteData == null

                if (isDirectSale) {
                    // Lógica para venta directa (modo empresa)
                    var procesamientoExitoso = true
                    val productosActualizadosBackend = mutableListOf<Producto>()

                    for ((productoEnCarrito, cantidad) in _carrito.value) {
                        if (!procesamientoExitoso) break

                        val productoOriginalEnLista = _productos.value.find { it.id == productoEnCarrito.id }
                        if (productoOriginalEnLista == null) {
                            _errorMessage.value = "Error: Producto '${productoEnCarrito.nombre}' no encontrado en el inventario."
                            procesamientoExitoso = false
                            break
                        }

                        val nuevoStock = productoOriginalEnLista.stock - cantidad
                        val productoParaActualizar = productoOriginalEnLista.copy(stock = nuevoStock)

                        try {
                            val response = api.actualizarProducto(productoOriginalEnLista.id!!, productoParaActualizar)
                            if (response.isSuccessful) {
                                productosActualizadosBackend.add(response.body() ?: productoParaActualizar)
                            } else {
                                _errorMessage.value = "Error al actualizar stock de ${productoEnCarrito.nombre}: ${response.code()} ${response.message()}"
                                procesamientoExitoso = false
                            }
                        } catch (e: Exception) {
                            _errorMessage.value = "Error de conexión al actualizar ${productoEnCarrito.nombre}: ${e.localizedMessage}"
                            procesamientoExitoso = false
                        }
                    }

                    if (procesamientoExitoso) {
                        actualizarProductosLocales(productosActualizadosBackend)
                        _carrito.value = emptyMap()
                        _successMessage.value = "✅ Venta procesada exitosamente. Stock actualizado."
                        cargarProductos() // Recargar para asegurar la consistencia total
                    }

                } else {
                    // Lógica para solicitud de pago (cliente)
                    val nombreCliente = clienteData?.get("nombre") ?: "N/A"
                    val correoCliente = clienteData?.get("correo") ?: "N/A"
                    val telefonoCliente = clienteData?.get("telefono") ?: "N/A"

                    val clienteInfoString = buildString {
                        append("Nombre: $nombreCliente\n")
                        append("Correo: $correoCliente\n")
                        append("Teléfono: $telefonoCliente")
                    }

                    val solicitud = SolicitudPago(
                        productos = _carrito.value.toMap(),
                        total = obtenerTotalCarrito(),
                        clienteInfo = clienteInfoString
                    )

                    val solicitudesActuales = _solicitudesPagoPendientes.value.toMutableList()
                    solicitudesActuales.add(solicitud)
                    _solicitudesPagoPendientes.value = solicitudesActuales

                    _nuevaSolicitudPago.value = solicitud // Notificar nueva solicitud a la vista de empresa

                    _carrito.value = emptyMap()
                    _successMessage.value = "📤 Solicitud de pago enviada. Esperando confirmación de la empresa."
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error al procesar el pago: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ========== Funciones para la Vista de Empresa ==========

    fun confirmarPagoRecibido(solicitudId: String) {
        viewModelScope.launch {
            _loading.value = true
            limpiarError()
            limpiarExito()
            try {
                val solicitudesActuales = _solicitudesPagoPendientes.value.toMutableList()
                val solicitud = solicitudesActuales.find { it.id == solicitudId }

                if (solicitud == null) {
                    _errorMessage.value = "Error: Solicitud de pago no encontrada."
                    _loading.value = false
                    return@launch
                }

                if (solicitud.estado != EstadoPago.PENDIENTE) {
                    _errorMessage.value = "Error: Esta solicitud ya ha sido procesada (${solicitud.estado})."
                    _loading.value = false
                    return@launch
                }

                var procesamientoExitoso = true
                val productosActualizadosBackend = mutableListOf<Producto>()

                for ((productoEnSolicitud, cantidad) in solicitud.productos) {
                    if (!procesamientoExitoso) break

                    val productoOriginalEnLista = _productos.value.find { it.id == productoEnSolicitud.id }

                    if (productoOriginalEnLista == null) {
                        _errorMessage.value = "Error: Producto '${productoEnSolicitud.nombre}' de la solicitud no encontrado en el inventario."
                        procesamientoExitoso = false
                        break
                    }

                    if (productoOriginalEnLista.stock < cantidad) {
                        _errorMessage.value = "Error: Stock insuficiente para '${productoEnSolicitud.nombre}'. Disponible: ${productoOriginalEnLista.stock}, Solicitado: $cantidad."
                        procesamientoExitoso = false
                        break
                    }

                    val nuevoStock = productoOriginalEnLista.stock - cantidad
                    val productoParaActualizar = productoOriginalEnLista.copy(stock = nuevoStock)

                    try {
                        val response = api.actualizarProducto(productoOriginalEnLista.id!!, productoParaActualizar)
                        if (response.isSuccessful) {
                            productosActualizadosBackend.add(response.body() ?: productoParaActualizar)
                        } else {
                            _errorMessage.value = "Error al actualizar stock de ${productoEnSolicitud.nombre}: ${response.code()} ${response.message()}"
                            procesamientoExitoso = false
                        }
                    } catch (e: Exception) {
                        _errorMessage.value = "Error de conexión al actualizar ${productoEnSolicitud.nombre}: ${e.localizedMessage}"
                        procesamientoExitoso = false
                    }
                }

                if (procesamientoExitoso) {
                    actualizarProductosLocales(productosActualizadosBackend) // Reflejar en UI
                    val solicitudActualizada = solicitud.copy(estado = EstadoPago.CONFIRMADO)
                    val indice = solicitudesActuales.indexOfFirst { it.id == solicitudId }
                    if (indice != -1) {
                        solicitudesActuales[indice] = solicitudActualizada
                        _solicitudesPagoPendientes.value = solicitudesActuales
                    }
                    _successMessage.value = "✅ Pago confirmado. Stock actualizado correctamente."
                    cargarProductos() // Recargar para asegurar la consistencia total
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado al confirmar pago: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun rechazarPago(solicitudId: String) {
        viewModelScope.launch {
            _loading.value = true
            limpiarError()
            limpiarExito()
            try {
                val solicitudesActuales = _solicitudesPagoPendientes.value.toMutableList()
                val solicitud = solicitudesActuales.find { it.id == solicitudId }

                if (solicitud == null) {
                    _errorMessage.value = "Error: Solicitud de pago no encontrada."
                    _loading.value = false
                    return@launch
                }
                if (solicitud.estado != EstadoPago.PENDIENTE) {
                    _errorMessage.value = "Error: Esta solicitud ya ha sido procesada (${solicitud.estado})."
                    _loading.value = false
                    return@launch
                }

                val solicitudActualizada = solicitud.copy(estado = EstadoPago.RECHAZADO)
                val indice = solicitudesActuales.indexOfFirst { it.id == solicitudId }
                if (indice != -1) {
                    solicitudesActuales[indice] = solicitudActualizada
                    _solicitudesPagoPendientes.value = solicitudesActuales
                }
                _successMessage.value = "❌ Solicitud de pago rechazada."
            } catch (e: Exception) {
                _errorMessage.value = "Error al rechazar pago: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun marcarSolicitudComoVista() {
        _nuevaSolicitudPago.value = null
    }

    // Elimina solicitudes confirmadas/rechazadas de la lista visible después de un tiempo o acción
    fun eliminarSolicitudProcesada(solicitudId: String) {
        val solicitudesActuales = _solicitudesPagoPendientes.value.toMutableList()
        solicitudesActuales.removeAll { it.id == solicitudId && (it.estado == EstadoPago.CONFIRMADO || it.estado == EstadoPago.RECHAZADO) }
        _solicitudesPagoPendientes.value = solicitudesActuales
    }

    // ========== Funciones Auxiliares ==========

    private fun actualizarProductosLocales(productosActualizados: List<Producto>) {
        val listaActual = _productos.value.toMutableList()

        for (productoActualizado in productosActualizados) {
            val index = listaActual.indexOfFirst { it.id == productoActualizado.id }
            if (index != -1) {
                listaActual[index] = productoActualizado
            }
        }
        _productos.value = listaActual
    }

    fun obtenerTotalCarrito(): Double {
        return _carrito.value.entries.sumOf { it.key.precio * it.value }
    }

    fun obtenerResumenCarrito(): String {
        if (_carrito.value.isEmpty()) return "Carrito vacío"

        val resumen = StringBuilder()
        resumen.append("📋 RESUMEN DE COMPRA:\n\n")

        _carrito.value.forEach { (producto, cantidad) ->
            val subtotal = producto.precio * cantidad
            resumen.append("• ${producto.nombre}\n")
            resumen.append("  Cantidad: $cantidad\n")
            resumen.append("  Subtotal: $${String.format("%.0f", subtotal)}\n\n")
        }

        val total = obtenerTotalCarrito()
        resumen.append("💰 TOTAL: $${String.format("%.0f", total)}")

        return resumen.toString()
    }

    fun obtenerResumenSolicitud(solicitud: SolicitudPago): String {
        val resumen = StringBuilder()
        resumen.append("📋 SOLICITUD DE PAGO #${solicitud.id.take(8)}\n")
        resumen.append("⏰ ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(solicitud.fechaSolicitud))}\n\n")

        solicitud.productos.forEach { (producto, cantidad) ->
            val subtotal = producto.precio * cantidad
            resumen.append("• ${producto.nombre}\n")
            resumen.append("  Cantidad: $cantidad\n")
            resumen.append("  Subtotal: $${String.format("%.0f", subtotal)}\n\n")
        }

        resumen.append("💰 TOTAL: $${String.format("%.0f", solicitud.total)}\n")
        resumen.append("📱 Cliente: ${solicitud.clienteInfo ?: "No especificado"}")

        return resumen.toString()
    }

    // Valida stock disponible EN EL CARRITO vs. el stock actual de los productos
    fun validarStockDisponibleEnCarrito(): Boolean {
        _carrito.value.forEach { (productoEnCarrito, cantidadSolicitada) ->
            val productoReal = _productos.value.find { it.id == productoEnCarrito.id }
            if (productoReal == null || productoReal.stock < cantidadSolicitada) {
                _errorMessage.value = "Stock insuficiente para ${productoEnCarrito.nombre}. Disponible: ${productoReal?.stock ?: 0}, Solicitado: $cantidadSolicitada"
                return false
            }
        }
        return true
    }

    // ========== Mensajes de UI ==========

    fun limpiarError() {
        _errorMessage.value = null
    }

    fun limpiarExito() {
        _successMessage.value = null
    }

    fun setErrorMessage(msg: String?) {
        _errorMessage.value = msg
    }
}