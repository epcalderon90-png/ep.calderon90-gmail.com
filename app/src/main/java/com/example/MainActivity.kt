package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.DataRepository
import com.example.ui.theme.*
import com.example.ui.screens.*
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    
    // Initialize repository and database
    val appDatabase = remember { AppDatabase.getDatabase(context) }
    val repository = remember { DataRepository(appDatabase.appDao()) }
    
    // Create viewmodel
    val appViewModel: AppViewModel = viewModel(
        factory = AppViewModelFactory(
            application = context.applicationContext as android.app.Application,
            repository = repository
        )
    )

    var currentTab by remember { mutableStateOf(0) } // 0: Explorar, 1: Publicar, 2: Mensajes, 3: Mi Perfil, 4: Moderador

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column {
                Divider(color = Color(0xFFE7E0EC), thickness = 1.dp)
                NavigationBar(
                    containerColor = Color(0xFFF3EDF7),
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("app_navigation_bar")
                ) {
                    // Tab 0: Explorar
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 0) Icons.Default.Explore else Icons.Outlined.Explore,
                                contentDescription = "Explorar"
                            )
                        },
                        label = { Text("Explorar", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            unselectedIconColor = Color(0xFF1D192B).copy(alpha = 0.6f),
                            unselectedTextColor = Color(0xFF1D192B).copy(alpha = 0.6f),
                            indicatorColor = Color(0xFFE8DEF8)
                        ),
                        modifier = Modifier.testTag("nav_explore")
                    )

                    // Tab 1: Publicar
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 1) Icons.Default.AddCircle else Icons.Outlined.AddCircle,
                                contentDescription = "Publicar"
                            )
                        },
                        label = { Text("Publicar", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            unselectedIconColor = Color(0xFF1D192B).copy(alpha = 0.6f),
                            unselectedTextColor = Color(0xFF1D192B).copy(alpha = 0.6f),
                            indicatorColor = Color(0xFFE8DEF8)
                        ),
                        modifier = Modifier.testTag("nav_add")
                    )

                    // Tab 2: Mensajes
                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 2) Icons.Default.ChatBubble else Icons.Outlined.ChatBubble,
                                contentDescription = "Mensajes"
                            )
                        },
                        label = { Text("Mensajes", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            unselectedIconColor = Color(0xFF1D192B).copy(alpha = 0.6f),
                            unselectedTextColor = Color(0xFF1D192B).copy(alpha = 0.6f),
                            indicatorColor = Color(0xFFE8DEF8)
                        ),
                        modifier = Modifier.testTag("nav_messages")
                    )

                    // Tab 3: Perfil
                    NavigationBarItem(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 3) Icons.Default.Person else Icons.Outlined.Person,
                                contentDescription = "Perfil"
                            )
                        },
                        label = { Text("Perfil", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            unselectedIconColor = Color(0xFF1D192B).copy(alpha = 0.6f),
                            unselectedTextColor = Color(0xFF1D192B).copy(alpha = 0.6f),
                            indicatorColor = Color(0xFFE8DEF8)
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )

                    // Tab 4: Moderador
                    NavigationBarItem(
                        selected = currentTab == 4,
                        onClick = { currentTab = 4 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 4) Icons.Default.Shield else Icons.Outlined.Shield,
                                contentDescription = "Moderador"
                            )
                        },
                        label = { Text("Moderador", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            unselectedIconColor = Color(0xFF1D192B).copy(alpha = 0.6f),
                            unselectedTextColor = Color(0xFF1D192B).copy(alpha = 0.6f),
                            indicatorColor = Color(0xFFE8DEF8)
                        ),
                        modifier = Modifier.testTag("nav_admin")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                0 -> HomeScreen(
                    viewModel = appViewModel,
                    onNavigateToChat = { chatId ->
                        currentTab = 2 // redirects to messages tab
                    },
                    onNavigateToProfile = {
                        currentTab = 3 // redirects to profile tab
                    },
                    onNavigateToPremium = {
                        currentTab = 3 // redirects to profile (premium subscription block)
                    }
                )
                1 -> AddListingScreen(
                    viewModel = appViewModel,
                    onSuccess = {
                        currentTab = 0 // redirects back to feed
                    }
                )
                2 -> MessagingScreen(
                    viewModel = appViewModel
                )
                3 -> ProfileVerificationScreen(
                    viewModel = appViewModel
                )
                4 -> AdminPanelScreen(
                    viewModel = appViewModel
                )
            }
        }
    }
}
