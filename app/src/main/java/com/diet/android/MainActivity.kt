package com.diet.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diet.android.data.api.ApiClient
import com.diet.android.data.api.ApiService
import com.diet.android.data.repository.AuthRepository
import com.diet.android.ui.screens.home.HomeScreen
import com.diet.android.ui.screens.home.HomeViewModel
import com.diet.android.ui.screens.home.HomeViewModelFactory
import com.diet.android.ui.screens.explore.ExploreScreen
import com.diet.android.ui.screens.explore.ExploreViewModel
import com.diet.android.ui.screens.explore.ExploreViewModelFactory
import com.diet.android.ui.screens.login.LoginScreen
import com.diet.android.ui.screens.login.LoginViewModel
import com.diet.android.ui.screens.login.LoginViewModelFactory
import com.diet.android.ui.screens.profile.CompleteProfileScreen
import com.diet.android.ui.screens.profile.CompleteProfileViewModel
import com.diet.android.ui.screens.profile.CompleteProfileViewModelFactory
import com.diet.android.ui.screens.status.ApplicationStatusScreen
import com.diet.android.ui.theme.DietAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val apiService = ApiClient.getClient(applicationContext).create(ApiService::class.java)
        authRepository = AuthRepository(apiService)

        setContent {
            DietAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(authRepository = authRepository, apiService = apiService)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(authRepository: AuthRepository, apiService: ApiService) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf("splash") }

    LaunchedEffect(Unit) {
        val context = navController.context
        val token = ApiClient.getSavedToken(context)
        if (token.isNullOrEmpty()) {
            startDestination = "login"
        } else {
            authRepository.getCurrentUser().onSuccess { userInfo ->
                startDestination = when {
                    userInfo.dietitianApplicationStatus == "PENDING" || userInfo.dietitianApplicationStatus == "REJECTED" -> {
                        "application_status"
                    }
                    userInfo.role == "ROLE_USER" && (userInfo.height == null || userInfo.category == null) -> {
                        "complete_profile"
                    }
                    else -> {
                        "home"
                    }
                }
            }.onFailure {
                ApiClient.clearToken(context)
                startDestination = "login"
            }
        }
    }

    if (startDestination == "splash") {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.CircularProgressIndicator()
        }
    } else {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(authRepository)
                )
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = { role, isProfileComplete, appStatus ->
                        val route = when {
                            appStatus == "PENDING" || appStatus == "REJECTED" -> "application_status"
                            role == "ROLE_USER" && !isProfileComplete -> "complete_profile"
                            else -> "home"
                        }
                        navController.navigate(route) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("complete_profile") {
                val profileViewModel: CompleteProfileViewModel = viewModel(
                    factory = CompleteProfileViewModelFactory(authRepository)
                )
                CompleteProfileScreen(
                    viewModel = profileViewModel,
                    onSaveSuccess = {
                        navController.navigate("home") {
                            popUpTo("complete_profile") { inclusive = true }
                        }
                    },
                    onLogout = {
                        ApiClient.clearToken(navController.context)
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }
            composable("application_status") {
                ApplicationStatusScreen(
                    authRepository = authRepository,
                    onStatusApproved = {
                        navController.navigate("home") {
                            popUpTo("application_status") { inclusive = true }
                        }
                    },
                    onLogout = {
                        ApiClient.clearToken(navController.context)
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }
            composable("home") {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(apiService)
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onLogout = {
                        ApiClient.clearToken(navController.context)
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    },
                    onNavigateToExplore = {
                        navController.navigate("explore") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("explore") {
                val exploreViewModel: ExploreViewModel = viewModel(
                    factory = ExploreViewModelFactory(apiService)
                )
                ExploreScreen(
                    viewModel = exploreViewModel,
                    onNavigateToHome = {
                        navController.navigate("home") {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
