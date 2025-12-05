package com.example.kostlin.ui.screen.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kostlin.ui.theme.ButtonBlue
import com.example.kostlin.ui.theme.DarkText
import com.example.kostlin.ui.theme.LightText

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageUrl: String
)

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Dapatkan Akses Lebih Awal",
            description = "Booking duluan sebelum yang lain!",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=500&h=800&fit=crop"
        ),
        OnboardingPage(
            title = "Cek Kost Melalui Aplikasi Saja",
            description = "Cek kost dimanapun dan kapanpun!",
            imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=500&h=800&fit=crop"
        ),
        OnboardingPage(
            title = "Temukan Kost Idamanmu",
            description = "Kami menyediakan berbagai tipe kost yang bisa menjadi impianmu!",
            imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=500&h=800&fit=crop"
        )
    )

    var currentPage by remember { mutableStateOf(0) }
    var isTransitioning by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background Image with Overlay
        AnimatedVisibility(
            visible = !isTransitioning,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = pages[currentPage].imageUrl,
                    contentDescription = pages[currentPage].title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Page Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = !isTransitioning,
                    enter = slideInHorizontally(
                        initialOffsetX = { 300 },
                        animationSpec = tween(500)
                    ) + fadeIn(animationSpec = tween(500)),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -300 },
                        animationSpec = tween(500)
                    ) + fadeOut(animationSpec = tween(500))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = pages[currentPage].title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = pages[currentPage].description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val scale by animateFloatAsState(
                        targetValue = if (index == currentPage) 1.2f else 1f,
                        animationSpec = tween(300)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentPage) ButtonBlue
                                else Color.White.copy(alpha = 0.5f)
                            )
                            .let {
                                if (index == currentPage) {
                                    it
                                        .padding(horizontal = 4.dp)
                                        .size(width = 24.dp, height = 8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                } else {
                                    it
                                }
                            }
                    )
                    if (index < pages.size - 1) {
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }

            // Button
            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        isTransitioning = true
                        currentPage++
                        isTransitioning = false
                    } else {
                        onOnboardingFinished()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
            ) {
                Text(
                    text = if (currentPage == pages.size - 1) "Get Started" else "Continue",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(onOnboardingFinished = {})
}
