package com.example.kostlin.ui.component.button

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SocialLoginButton(icon: String) {
    Button(
        onClick = { /* Handle social login */ },
        modifier = Modifier.width(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Gray.copy(alpha = 0.1f)
        )
    ) {
        Text(text = icon, color = Color.Black)
    }
}