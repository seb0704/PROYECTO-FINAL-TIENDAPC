package com.example.tiendapcgamer.repository

import com.example.tiendapcgamer.data.model.Producto
import com.example.tiendapcgamer.data.remote.ProductoApiService

class ProductoRepository(private val api: ProductoApiService) {

    suspend fun obtenerProductos() = api.obtenerProductos()

    suspend fun agregarProducto(producto: Producto) = api.agregarProducto(producto)

    suspend fun actualizarProducto(producto: Producto) =
        producto.id?.let { api.actualizarProducto(it, producto) }
            ?: throw IllegalArgumentException("El producto debe tener un ID para actualizar")

    suspend fun eliminarProducto(id: Long) = api.eliminarProducto(id)
}
