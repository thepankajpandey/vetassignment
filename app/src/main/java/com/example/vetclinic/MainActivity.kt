package com.example.vetclinic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vetclinic.presentation.main.MainScreen
import com.example.vetclinic.presentation.main.MainViewModel
import com.example.vetclinic.ui.theme.VetClinicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VetClinicTheme {
                VetClinicApp()
            }
        }
    }
}

@Composable
fun VetClinicApp() {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            VetClinicNavHost(navController = navController)
        }
    }
}

@Composable
fun VetClinicNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = modifier
    ) {
        composable("main") {
            val viewModel: MainViewModel = hiltViewModel()
            val uiState by viewModel.ui.collectAsStateWithLifecycle()

            MainScreen(uiState)
        }

        composable(
            route = "web/{url}/{title}",
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            /*For Webview Navigation*/
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {

    VetClinicTheme {
        VetClinicApp()
    }
}