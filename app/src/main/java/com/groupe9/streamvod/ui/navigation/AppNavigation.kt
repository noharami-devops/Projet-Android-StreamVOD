package com.groupe9.streamvod.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.groupe9.streamvod.ui.auth.LoginScreen
import com.groupe9.streamvod.ui.auth.RegisterScreen
import com.groupe9.streamvod.ui.detail.DetailScreen
import com.groupe9.streamvod.ui.home.HomeScreen
import com.groupe9.streamvod.ui.profile.ProfileScreen
import com.groupe9.streamvod.ui.favorites.FavoritesScreen
import com.groupe9.streamvod.ui.search.SearchScreen
import com.groupe9.streamvod.ui.player.PlayerScreen
import com.groupe9.streamvod.ui.community.CommunityScreen
import com.groupe9.streamvod.ui.community.StreamPlayerScreen
import com.groupe9.streamvod.ui.community.MyVideosScreen
import com.groupe9.streamvod.ui.history.HistoryScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf("home", "search", "community", "favorites", "profile")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute ?: "home",
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable("home") {
                HomeScreen(
                    onMovieClick = { movieId ->
                        navController.navigate("detail/$movieId")
                    }
                )
            }
            composable("search") {
                SearchScreen(
                    onMovieClick = { movieId ->
                        navController.navigate("detail/$movieId")
                    }
                )
            }
            composable("detail/{movieId}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toInt() ?: 0
                DetailScreen(
                    movieId = movieId,
                    onBackClick = { navController.popBackStack() },
                    onWatchClick = { id ->
                        navController.navigate("player/$id")
                    },
                    onRecommendationClick = { id ->
                        navController.navigate("detail/$id")
                    }
                )
            }
            composable("favorites") {
                FavoritesScreen(
                    onMovieClick = { movieId ->
                        navController.navigate("detail/$movieId")
                    }
                )
            }
            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onMyVideosClick = {
                        navController.navigate("my_videos")
                    },
                    onHistoryClick = {
                        navController.navigate("history")
                    }
                )
            }
            composable("player/{movieId}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toInt() ?: 0
                PlayerScreen(
                    movieId = movieId,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("community") {
                CommunityScreen(
                    onVideoClick = { videoUrl, title ->
                        navController.navigate("stream/${Uri.encode(videoUrl)}/${Uri.encode(title)}")
                    }
                )
            }
            composable("stream/{videoUrl}/{title}") { backStackEntry ->
                val videoUrl = Uri.decode(backStackEntry.arguments?.getString("videoUrl") ?: "")
                val title = Uri.decode(backStackEntry.arguments?.getString("title") ?: "Vidéo")
                StreamPlayerScreen(
                    videoUrl = videoUrl,
                    title = title,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("my_videos") {
                MyVideosScreen(
                    onBackClick = { navController.popBackStack() },
                    onVideoClick = { videoUrl, title ->
                        navController.navigate("stream/${Uri.encode(videoUrl)}/${Uri.encode(title)}")
                    }
                )
            }
            composable("history") {
                HistoryScreen(
                    onBackClick = { navController.popBackStack() },
                    onItemClick = { item ->
                        if (item.type == "movie") {
                            val movieId = item.itemId.removePrefix("movie_").toIntOrNull()
                            if (movieId != null) {
                                navController.navigate("detail/$movieId")
                            }
                        } else {
                            val videoUrl = item.itemId.removePrefix("uservideo_")
                            navController.navigate("stream/${Uri.encode(videoUrl)}/${Uri.encode(item.title)}")
                        }
                    }
                )
            }
        }
    }
}