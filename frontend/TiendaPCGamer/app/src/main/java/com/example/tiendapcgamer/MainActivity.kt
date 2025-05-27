package com.example.tiendapcgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.tiendapcgamer.ui.navigation.NavGraph
import com.example.tiendapcgamer.ui.theme.TiendaPCGamerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TiendaPCGamerTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
