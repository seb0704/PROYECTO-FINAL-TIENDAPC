package com.example.tiendapcgamer.data.model

data class Producto(
    val id: Long? = null,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val marcaId: Int,
    val categoriaId: Int,
    val imagenUrl: String
)
