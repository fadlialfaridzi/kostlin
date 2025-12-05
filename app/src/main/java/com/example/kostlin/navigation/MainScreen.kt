package com.example.kostlin.navigation

/**
 * Sealed class untuk menangani navigasi antar layar utama aplikasi
 */
sealed class MainScreen(val route: String) {
    object Home : MainScreen("home")
    object Profile : MainScreen("profile")
    // Tambahkan layar utama lainnya di sini
}
