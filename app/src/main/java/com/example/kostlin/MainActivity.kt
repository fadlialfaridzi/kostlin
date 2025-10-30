package com.example.kostlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kostlin.ui.screen.autentikasi.LoginScreen
import com.example.kostlin.ui.theme.KostlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Menonaktifkan batas atas dan bawah untuk tampilan penuh

        setContent {
            KostlinTheme {
                // Scaffold untuk pengaturan dasar UI
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Panggil LoginScreen yang sudah Anda buat
                    LoginScreen(
                        onLoginSuccess = {
                            // Logika untuk menampilkan HomeScreen atau layar lain setelah login berhasil
                            // Misalnya, Anda bisa melanjutkan ke layar berikutnya
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KostlinTheme {
        Greeting("Android")
    }
}