package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.DataStoreManager
import com.example.data.StatsRepository
import com.example.ui.screens.*
import com.example.ui.components.frostedBackgroundBlur
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryPurple
import com.example.viewmodel.SharedStatsViewModel
import com.example.viewmodel.SharedStatsViewModelFactory
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val repository = StatsRepository(
            database.userStatsDao(),
            database.focusSessionDao(),
            database.storeItemDao()
        )
        val dataStoreManager = DataStoreManager(this)

        setContent {
            MyApplicationTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = true)
                
                val onboardingComplete by dataStoreManager.isOnboardingComplete.collectAsStateWithLifecycle(initialValue = false)
                val viewModel: SharedStatsViewModel = viewModel(factory = SharedStatsViewModelFactory(repository, this))
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()

                var showOnboarding by remember { mutableStateOf(true) }

                LaunchedEffect(onboardingComplete) {
                    showOnboarding = !onboardingComplete
                }

                if (showOnboarding) {
                    OnboardingScreen(onComplete = {
                        coroutineScope.launch {
                            dataStoreManager.setOnboardingComplete(true)
                            showOnboarding = false
                        }
                    })
                } else {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color(0xFFF6F9FD),
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                            ) {
                                // Floating white pill container
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(66.dp)
                                        .shadow(
                                            elevation = 14.dp,
                                            shape = RoundedCornerShape(33.dp),
                                            spotColor = Color(0xFF0F172A).copy(alpha = 0.12f)
                                        )
                                        .clip(RoundedCornerShape(33.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(33.dp))
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(66.dp)
                                        .padding(horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val items = listOf(
                                        Triple("home", "Trang chủ", Icons.Filled.Home),
                                        Triple("my_apps", "Ứng dụng", Icons.Outlined.GridView),
                                        Triple("timer", "Tập trung", Icons.Filled.LocalFireDepartment),
                                        Triple("community", "Cộng đồng", Icons.Outlined.Groups),
                                        Triple("store", "Cửa hàng", Icons.Outlined.Storefront)
                                    )

                                    for (item in items) {
                                        val route = item.first
                                        val label = item.second
                                        val icon = item.third
                                        val selected = currentDestination?.hierarchy?.any { it.route == route } == true
                                        val activeColor = Color(0xFF6C5CE7)
                                        val inactiveColor = Color(0xFF94A3B8)

                                        if (route == "timer") {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .offset(y = (-10).dp)
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null
                                                    ) {
                                                        navController.navigate(route) {
                                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    }
                                            ) {
                                                // Center elevated button with glowing outer halo
                                                Box(
                                                    modifier = Modifier
                                                        .size(56.dp)
                                                        .shadow(
                                                            elevation = 6.dp,
                                                            shape = CircleShape,
                                                            spotColor = Color(0xFF7C3AED).copy(alpha = 0.35f)
                                                        )
                                                        .clip(CircleShape)
                                                        .background(Color(0xFFEDE9FE))
                                                        .border(1.5.dp, Color(0xFFDDD6FE), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(44.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                Brush.linearGradient(
                                                                    listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
                                                                )
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = icon,
                                                            contentDescription = label,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(24.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = label,
                                                    fontSize = 9.5.sp,
                                                    color = if (selected) activeColor else Color(0xFF64748B),
                                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }
                                        } else {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null
                                                    ) {
                                                        navController.navigate(route) {
                                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    }
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = label,
                                                    tint = if (selected) activeColor else inactiveColor,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Text(
                                                    text = label,
                                                    fontSize = 9.5.sp,
                                                    color = if (selected) activeColor else inactiveColor,
                                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                                )

                                                // Bottom indicator pill when selected (like in the screenshot)
                                                if (selected) {
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .size(width = 22.dp, height = 3.dp)
                                                            .clip(CircleShape)
                                                            .background(activeColor)
                                                    )
                                                } else {
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        snackbarHost = { SnackbarHost(SnackbarHostState()) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("home") { 
                                DashboardScreen(viewModel) { 
                                    navController.navigate("timer") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    } 
                                } 
                            }
                            composable("my_apps") { MyAppsScreen() }
                            composable("timer") { 
                                TimerScreen(viewModel, SnackbarHostState()) {
                                    navController.navigate("home") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                } 
                            }
                            composable("rooms") { RoomsScreen(viewModel) }
                            composable("community") { CommunityScreen() }
                            composable("store") { StoreScreen(viewModel) }
                        }
                    }
                }
            }
        }
    }
}
