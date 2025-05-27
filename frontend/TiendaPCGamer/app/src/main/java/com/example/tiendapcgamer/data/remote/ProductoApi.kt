package com.example.tiendapcgamer.data.remote

import com.example.tiendapcgamer.data.model.Producto
import retrofit2.Response
import retrofit2.http.*

interface ProductoApiService {
    @GET("producto/listar")
    suspend fun obtenerProductos(): Response<List<Producto>>

    @POST("producto")
    suspend fun agregarProducto(@Body producto: Producto): Response<Producto>

    @PUT("producto/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: Producto
    ): Response<Producto>

    @DELETE("producto/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Void>
}
