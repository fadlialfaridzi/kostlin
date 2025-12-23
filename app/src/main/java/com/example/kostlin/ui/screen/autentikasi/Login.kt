package com.example.kostlin.ui.screen.autentikasi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.kostlin.ui.theme.ButtonBlue
import com.example.kostlin.ui.theme.DarkText
import com.example.kostlin.ui.theme.LightText
import com.example.kostlin.ui.theme.LinkColor
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String, rememberMe: Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    isLoading: Boolean = false,
    externalError: String? = null,
    onClearError: () -> Unit = {},
    showLoginSuccess: Boolean = false,
    onLoginSuccessDismissed: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRememberMeChecked by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var localErrorMessage by remember { mutableStateOf("") }
    
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val buttonInteractionSource = remember { MutableInteractionSource() }
    val isButtonPressed by buttonInteractionSource.collectIsPressedAsState()
    
    // Trigger entrance animation
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    // Button press animation
    val buttonScale by animateFloatAsState(
        targetValue = if (isButtonPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )
    
    val buttonElevation by animateDpAsState(
        targetValue = if (isButtonPressed) 2.dp else 8.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "buttonElevation"
    )

    val errorMessage = when {
        localErrorMessage.isNotBlank() -> localErrorMessage
        !externalError.isNullOrBlank() -> externalError
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Animated Title
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 100)) + slideInVertically(
                    initialOffsetY = { -30 },
                    animationSpec = tween(500, delayMillis = 100)
                )
            ) {
                Text(
                    text = "Let's Sign you in",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = DarkText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Animated Subtitle
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { -20 },
                    animationSpec = tween(500, delayMillis = 200)
                )
            ) {
                Text(
                    text = "Selamat Datang, Silahkan Login Terlebih Dahulu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightText,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            // Animated Email Input
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 300)) + slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Text(
                        text = "Email Address",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = DarkText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            localErrorMessage = ""
                            onClearError()
                        },
                        placeholder = { Text("Enter your email address", color = LightText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = ButtonBlue
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true
                    )
                }
            }

            // Animated Password Input
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = DarkText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            localErrorMessage = ""
                            onClearError()
                        },
                        placeholder = { Text("Enter your password", color = LightText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = ButtonBlue
                        ),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        singleLine = true
                    )
                }
            }

            // Error message with shake animation
            AnimatedVisibility(
                visible = !errorMessage.isNullOrEmpty(),
                enter = fadeIn() + slideInVertically(initialOffsetY = { -10 })
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Remember Me and Forgot Password
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 500))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isRememberMeChecked,
                            onCheckedChange = { isRememberMeChecked = it }
                        )
                        Text(
                            "Remember Me",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkText
                        )
                    }
                    Text(
                        text = "Forgot Password?",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { onNavigateToForgotPassword() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Sign In Button with press effect
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 600)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            ) {
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            localErrorMessage = "Please fill all fields"
                        } else {
                            localErrorMessage = ""
                            onLogin(email.trim(), password, isRememberMeChecked)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .scale(buttonScale)
                        .shadow(buttonElevation, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                    interactionSource = buttonInteractionSource
                ) {
                    Text(text = "Sign In", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Link
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 700))
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Text("Don't have an account?", color = DarkText)
                    Text(
                        text = " Sign Up",
                        color = LinkColor,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            }



            Spacer(modifier = Modifier.weight(1f))

            // Terms and Conditions
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 900))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "By signing up you agree to our ",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightText
                    )
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text(
                            text = "Terms",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = DarkText,
                            modifier = Modifier.clickable { }
                        )
                        Text(
                            text = " and ",
                            style = MaterialTheme.typography.bodySmall,
                            color = LightText
                        )
                        Text(
                            text = "Conditions of Use",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = DarkText,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }
        }

        // Loading overlay with animation
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ButtonBlue)
            }
        }
    }
}
