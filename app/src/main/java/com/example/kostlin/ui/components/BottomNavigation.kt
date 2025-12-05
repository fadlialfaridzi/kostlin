package com.example.kostlin.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

enum class BottomNavRoute(val route: String) {
    HOME("home"),
    FAVORITE("favorite"),
    ADD("add"),
    PROFILE("profile")
}

@Composable
fun BottomNavigation(
    currentRoute: String = BottomNavRoute.HOME.route,
    onNavigate: (String) -> Unit = {}
) {
    val items = listOf(
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "Beranda",
            route = BottomNavRoute.HOME.route
        ),
        BottomNavItem(
            icon = Icons.Default.FavoriteBorder,
            label = "Kos Favorit",
            route = BottomNavRoute.FAVORITE.route
        ),
        BottomNavItem(
            icon = Icons.Default.Add,
            label = "Tambah",
            route = BottomNavRoute.ADD.route
        ),
        BottomNavItem(
            icon = Icons.Default.Person,
            label = "Profil",
            route = BottomNavRoute.PROFILE.route
        )
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        windowInsets = WindowInsets(0.dp),
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val icon = when (item.route) {
                BottomNavRoute.PROFILE.route -> {
                    if (isSelected) Icons.Filled.Person else Icons.Outlined.PersonOutline
                }
                else -> item.icon
            }
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = item.label,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
