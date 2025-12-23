package com.example.kostlin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class SlideDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    BOTTOM_TO_TOP,
    TOP_TO_BOTTOM
}

@Composable
fun AnimatedScreenTransition(
    visible: Boolean,
    direction: SlideDirection = SlideDirection.RIGHT_TO_LEFT,
    durationMillis: Int = 300,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val enterTransition = when (direction) {
        SlideDirection.LEFT_TO_RIGHT -> slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(durationMillis)
        ) + fadeIn(animationSpec = tween(durationMillis))
        
        SlideDirection.RIGHT_TO_LEFT -> slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(durationMillis)
        ) + fadeIn(animationSpec = tween(durationMillis))
        
        SlideDirection.BOTTOM_TO_TOP -> slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis)
        ) + fadeIn(animationSpec = tween(durationMillis))
        
        SlideDirection.TOP_TO_BOTTOM -> slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis)
        ) + fadeIn(animationSpec = tween(durationMillis))
    }
    
    val exitTransition = when (direction) {
        SlideDirection.LEFT_TO_RIGHT -> slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(durationMillis)
        ) + fadeOut(animationSpec = tween(durationMillis))
        
        SlideDirection.RIGHT_TO_LEFT -> slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(durationMillis)
        ) + fadeOut(animationSpec = tween(durationMillis))
        
        SlideDirection.BOTTOM_TO_TOP -> slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis)
        ) + fadeOut(animationSpec = tween(durationMillis))
        
        SlideDirection.TOP_TO_BOTTOM -> slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis)
        ) + fadeOut(animationSpec = tween(durationMillis))
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition,
        modifier = modifier,
        content = content
    )
}

// Simplified version for common use cases
@Composable
fun SlideInFromRight(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedScreenTransition(
        visible = visible,
        direction = SlideDirection.RIGHT_TO_LEFT,
        modifier = modifier,
        content = content
    )
}

@Composable
fun SlideInFromBottom(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedScreenTransition(
        visible = visible,
        direction = SlideDirection.BOTTOM_TO_TOP,
        modifier = modifier,
        content = content
    )
}

@Composable
fun FadeInScreen(
    visible: Boolean,
    durationMillis: Int = 300,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis)),
        modifier = modifier,
        content = content
    )
}
