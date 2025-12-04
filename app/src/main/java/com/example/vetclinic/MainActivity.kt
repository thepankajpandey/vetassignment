package com.example.vetclinic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            val navController = rememberNavController()
            VetClinicTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        NavHost(navController, startDestination = "main") {
                            composable("main") {
                                val viewModel: MainViewModel = hiltViewModel()
                                val uiState by viewModel.ui.collectAsState()
                                MainScreen(uiState, withinHours = true)
                            }
                            composable("web/{url}/{title}") { back ->
                                val url = java.net.URLDecoder.decode(back.arguments?.getString("url") ?: "", "utf-8")
                                val title = java.net.URLDecoder.decode(back.arguments?.getString("title") ?: "", "utf-8")
                                //To write webview navigation
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VetClinicTheme {
        Greeting("Android")
    }
}