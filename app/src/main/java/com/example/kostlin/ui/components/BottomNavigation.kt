package com.example.kostlin.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
        containerColor = Color.White,
        tonalElevation = 6.dp,
        windowInsets = WindowInsets(bottom = 0.dp)
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Color(0xFF5876FF) else Color(0xFF4B5C6B)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Color(0xFF5876FF) else Color(0xFF4B5C6B),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}
