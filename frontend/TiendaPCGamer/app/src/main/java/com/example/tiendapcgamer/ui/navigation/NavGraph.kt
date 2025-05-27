package com.example.tiendapcgamer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tiendapcgamer.ui.pantallas.ClienteScreen
import com.example.tiendapcgamer.ui.pantallas.EmpresaScreen
import com.example.tiendapcgamer.ui.pantallas.MainScreen
import com.example.tiendapcgamer.ui.pantallas.CargandoScreen
import com.example.tiendapcgamer.ui.pantallas.CarritoScreen
import com.example.tiendapcgamer.ui.pantallas.NotificationScreen
import com.example.tiendapcgamer.viewmodel.ProductoViewModel

object Routes {
    const val CARGANDO = "cargando"
    const val MAIN = "main"
    const val CLIENTE = "cliente"
    const val EMPRESA = "empresa"
    const val CARRITO = "carrito"
    const val NOTIFICACIONES = "notificaciones"
}

@Composable
fun NavGraph(navController: NavHostController) {
    val productoViewModel: ProductoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.CARGANDO
    ) {
        composable(Routes.CARGANDO) {
            CargandoScreen(onTimeout = {
                navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.CARGANDO) { inclusive = true }
                }
            })
        }
        composable(Routes.MAIN) {
            MainScreen(navController = navController)
        }
        composable(Routes.CLIENTE) {
            ClienteScreen(
                navController = navController,
                productoViewModel = productoViewModel
            )
        }
        composable(Routes.EMPRESA) {
            EmpresaScreen(
                productoViewModel = productoViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate(Routes.NOTIFICACIONES) }
            )
        }
        composable(Routes.CARRITO) {
            CarritoScreen(
                productoViewModel = productoViewModel,
                navController = navController
            )
        }
        composable(Routes.NOTIFICACIONES) {
            NotificationScreen(
                productoViewModel = productoViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}