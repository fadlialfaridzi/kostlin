package com.example.kostlin.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    secondary = LightBlue,
    background = BackgroundGray,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = DarkText,
    onBackground = DarkText,
    onSurface = DarkText,
    error = Color.Red
)

// Deklarasi warna untuk tema gelap
private val DarkColors = darkColorScheme(
    primary = PrimaryBlue,
    secondary = LightBlue,
    background = BackgroundGray,
    surface = Color.Black,
    onPrimary = Color.White,
    onSecondary = DarkText,
    onBackground = DarkText,
    onSurface = DarkText,
    error = Color.Red
)

@Composable
fun KostlinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Menentukan warna yang akan digunakan berdasarkan tema dan versi Android
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    // Menggunakan MaterialTheme untuk menerapkan warna dan tipografi
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Pastikan Anda memiliki objek Typography yang terdefinisi
        content = content
    )
}
