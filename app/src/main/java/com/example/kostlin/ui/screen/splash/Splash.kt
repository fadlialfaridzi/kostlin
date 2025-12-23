package com.example.kostlin.ui.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kostlin.R
import com.example.kostlin.ui.theme.DarkBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }
    
    // Logo scale animation - bounce effect
    val logoScale = remember { Animatable(0f) }
    
    // Logo alpha animation
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "logoAlpha"
    )
    
    // Logo rotation for subtle effect
    val logoRotation = remember { Animatable(0f) }
    
    // Background fade
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "bgAlpha"
    )
    
    // Pulse animation for glow effect
    var pulseActive by remember { mutableStateOf(false) }
    val pulseScale by animateFloatAsState(
        targetValue = if (pulseActive) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        
        // Start logo bounce animation
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        
        // Subtle rotation wiggle
        launch {
            delay(300)
            logoRotation.animateTo(
                targetValue = 5f,
                animationSpec = tween(150)
            )
            logoRotation.animateTo(
                targetValue = -5f,
                animationSpec = tween(150)
            )
            logoRotation.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        
        // Pulse effect
        launch {
            delay(800)
            pulseActive = true
            delay(300)
            pulseActive = false
        }
        
        // Finish splash after animations
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(backgroundAlpha)
            .background(color = DarkBlue)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .offset(y = (-40).dp) // Move content slightly upward
        ) {
            // Animated Logo
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(500.dp)
                    .scale(logoScale.value * pulseScale)
                    .alpha(logoAlpha)
                    .graphicsLayer {
                        rotationZ = logoRotation.value
                    }
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onSplashFinished = {})
}