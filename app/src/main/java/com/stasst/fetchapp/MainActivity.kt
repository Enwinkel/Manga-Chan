package com.stasst.fetchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stasst.fetchapp.ui.new_manga.NewMangaScreen
import com.stasst.fetchapp.ui.ReadScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxSize()
            ) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "new_manga") {
                    composable("new_manga") {
                        NewMangaScreen(navController)
                    }
                    composable("read/{link}", arguments = listOf(navArgument("link") {
                        type = NavType.StringType
                    })) {
                        ReadScreen(navController, it.arguments?.getString("link"))
                    }

                }
            }
        }
    }
}


