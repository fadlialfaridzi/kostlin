package com.example.kostlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.example.kostlin.ui.theme.KostlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Menonaktifkan batas atas dan bawah untuk tampilan penuh

        setContent {
            KostlinTheme {
                Surface {
                    Navigation()
                }

            }
        }
    }
}
