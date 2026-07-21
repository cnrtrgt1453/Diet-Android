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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.diet.android.data.api.ApiClient
import com.diet.android.data.api.ApiService
import com.diet.android.data.repository.AuthRepository
import com.diet.android.ui.screens.home.HomeScreen
import com.diet.android.ui.screens.home.AvailabilitySlotsScreen
import com.diet.android.ui.screens.home.AnalyticsScreen
import com.diet.android.ui.screens.home.AppointmentBookingScreen
import com.diet.android.ui.screens.home.DietitianMessagesScreen
import com.diet.android.ui.screens.home.NotificationsScreen
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
import com.diet.android.ui.screens.profile.ProfileScreen
import com.diet.android.ui.screens.profile.ProfileEditScreen
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
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(apiService)
    )
    val exploreViewModel: ExploreViewModel = viewModel(
        factory = ExploreViewModelFactory(apiService)
    )

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
            composable(
                route = "home?dialog={dialog}",
                arguments = listOf(
                    navArgument("dialog") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val dialog = backStackEntry.arguments?.getString("dialog")
                HomeScreen(
                    viewModel = homeViewModel,
                    initialDialog = dialog,
                    onNavigateToAppointmentBooking = {
                        navController.navigate("appointment_booking") {
                            launchSingleTop = true
                        }
                    },
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
                    },
                    onNavigateToSlots = {
                        navController.navigate("slots") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToMessages = {
                        navController.navigate("messages") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAnalytics = {
                        navController.navigate("analytics") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToNotifications = {
                        navController.navigate("notifications") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("explore") {
                ExploreScreen(
                    viewModel = exploreViewModel,
                    onNavigateToHome = { dialog ->
                        if (dialog == "appointment") {
                            navController.navigate("appointment_booking") {
                                launchSingleTop = true
                            }
                        } else {
                            val route = if (dialog != null) "home?dialog=$dialog" else "home"
                            navController.navigate(route) {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToSlots = {
                        navController.navigate("slots") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToMessages = {
                        navController.navigate("messages") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAnalytics = {
                        navController.navigate("analytics") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("messages") {
                DietitianMessagesScreen(
                    viewModel = exploreViewModel,
                    onNavigateToHome = { dialog ->
                        if (dialog == "appointment") {
                            navController.navigate("appointment_booking") {
                                launchSingleTop = true
                            }
                        } else {
                            val route = if (dialog != null) "home?dialog=$dialog" else "home"
                            navController.navigate(route) {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToExplore = {
                        navController.navigate("explore") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToSlots = {
                        navController.navigate("slots") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAnalytics = {
                        navController.navigate("analytics") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("slots") {
                AvailabilitySlotsScreen(
                    viewModel = homeViewModel,
                    onNavigateToHome = { dialog ->
                        if (dialog == "appointment") {
                            navController.navigate("appointment_booking") {
                                launchSingleTop = true
                            }
                        } else {
                            val route = if (dialog != null) "home?dialog=$dialog" else "home"
                            navController.navigate(route) {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToExplore = {
                        navController.navigate("explore") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToMessages = {
                        navController.navigate("messages") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAnalytics = {
                        navController.navigate("analytics") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("analytics") {
                AnalyticsScreen(
                    viewModel = homeViewModel,
                    onNavigateToHome = { dialog ->
                        if (dialog == "appointment") {
                            navController.navigate("appointment_booking") {
                                launchSingleTop = true
                            }
                        } else {
                            val route = if (dialog != null) "home?dialog=$dialog" else "home"
                            navController.navigate(route) {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToExplore = {
                        navController.navigate("explore") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToSlots = {
                        navController.navigate("slots") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToMessages = {
                        navController.navigate("messages") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("profile") {
                ProfileScreen(
                    viewModel = homeViewModel,
                    onNavigateToHome = { dialog ->
                        if (dialog == "appointment") {
                            navController.navigate("appointment_booking") {
                                launchSingleTop = true
                            }
                        } else {
                            val route = if (dialog != null) "home?dialog=$dialog" else "home"
                            navController.navigate(route) {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToExplore = {
                        navController.navigate("explore") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToSlots = {
                        navController.navigate("slots") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToMessages = {
                        navController.navigate("messages") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToAnalytics = {
                        navController.navigate("analytics") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfileEdit = {
                        navController.navigate("profile_edit") {
                            launchSingleTop = true
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
            composable("profile_edit") {
                ProfileEditScreen(
                    viewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("appointment_booking") {
                AppointmentBookingScreen(
                    viewModel = homeViewModel,
                    onNavigateToHome = { dialog ->
                        val route = if (dialog != null) "home?dialog=$dialog" else "home"
                        navController.navigate(route) {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToExplore = {
                        navController.navigate("explore") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToMessages = {
                        navController.navigate("messages") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("notifications") {
                NotificationsScreen(
                    viewModel = homeViewModel,
                    onNavigateBack = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
