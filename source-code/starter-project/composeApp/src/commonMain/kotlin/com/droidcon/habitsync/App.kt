package com.droidcon.habitsync


import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.droidcon.habitsync.navigation.NavigationGraph
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val navController = rememberNavController()
            NavigationGraph(navController)
        }
    }
}
