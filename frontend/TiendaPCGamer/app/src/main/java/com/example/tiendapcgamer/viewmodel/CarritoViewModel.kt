package com.example.tiendapcgamer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tiendapcgamer.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Clase para representar un item en el carrito con cantidad mutable
data class ItemCarrito(val producto: Producto, var cantidad: Int)

class CarritoViewModel : ViewModel() {

    // Estado observable del carrito
    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito

    /**
     * Agrega un producto al carrito. Si ya existe, incrementa la cantidad
     * solo si no supera el stock disponible.
     */
    fun agregarAlCarrito(producto: Producto) {
        val listaActual = _carrito.value.toMutableList()
        val existente = listaActual.find { it.producto.id == producto.id }

        if (existente != null) {
            if (existente.cantidad < producto.stock) {
                existente.cantidad++
            }
            // Si ya está en el carrito y no se puede incrementar, no hace nada
        } else {
            // Solo añade si hay stock disponible
            if (producto.stock > 0) {
                listaActual.add(ItemCarrito(producto, 1))
            }
        }

        _carrito.value = listaActual
    }

    /**
     * Elimina un producto del carrito por su ID
     */
    fun eliminarDelCarrito(productoId: Long) {
        _carrito.value = _carrito.value.filterNot { it.producto.id == productoId }
    }

    /**
     * Vacía completamente el carrito
     */
    fun vaciarCarrito() {
        _carrito.value = emptyList()
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     * Solo permite cantidades positivas y que no excedan el stock disponible.
     */
    fun actualizarCantidad(productoId: Long, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) return  // Ignora cantidades inválidas

        val listaActual = _carrito.value.toMutableList()
        val item = listaActual.find { it.producto.id == productoId }
        if (item != null && nuevaCantidad <= item.producto.stock) {
            item.cantidad = nuevaCantidad
            _carrito.value = listaActual
        }
        // Si no cumple, no hace nada
    }
}
