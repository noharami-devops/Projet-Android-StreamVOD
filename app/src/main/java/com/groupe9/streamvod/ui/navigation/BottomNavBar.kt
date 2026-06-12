package com.groupe9.streamvod.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.groupe9.streamvod.ui.theme.Primary

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Accueil")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Favoris")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profil")
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onItemClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = androidx.compose.ui.graphics.Color(0xFF1F1F1F)
    ) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Favorites,
            BottomNavItem.Profile
        )
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onItemClick(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = androidx.compose.ui.graphics.Color(0xFFAAAAAA),
                    unselectedTextColor = androidx.compose.ui.graphics.Color(0xFFAAAAAA)
                )
            )
        }
    }
}