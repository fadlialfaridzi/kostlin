package com.example.kostlin.ui.screen.booking

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun BookingLoadingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Trigger completion after 2 seconds
    LaunchedEffect(Unit) {
        delay(2000)
        onComplete()
    }
    
    // Pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // Dot animation
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated Icon
        Box(
            modifier = Modifier
                .scale(scale)
                .size(120.dp)
                .background(Color(0xFFF0F4FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📋",
                style = MaterialTheme.typography.displayLarge
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Memproses Booking",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2633)
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Mohon tunggu sebentar...",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF6B7280)
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Loading dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        Color(0xFF5876FF).copy(alpha = dot1Alpha),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        Color(0xFF5876FF).copy(alpha = dot2Alpha),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        Color(0xFF5876FF).copy(alpha = dot3Alpha),
                        CircleShape
                    )
            )
        }
    }
}
