package com.example.kostlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.kostlin.navigation.AuthScreen
import com.example.kostlin.ui.screen.autentikasi.CreateNewPasswordScreen
import com.example.kostlin.ui.screen.autentikasi.EnterOTPScreen
import com.example.kostlin.ui.screen.autentikasi.ForgotPasswordScreen
import com.example.kostlin.ui.screen.autentikasi.LoginScreen
import com.example.kostlin.ui.screen.autentikasi.RegisterScreen
import com.example.kostlin.ui.screen.home.HomeScreen
import com.example.kostlin.ui.screen.onboarding.OnboardingScreen
import com.example.kostlin.ui.screen.splash.SplashScreen
import com.example.kostlin.ui.theme.KostlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KostlinTheme {
                var showSplash by remember { mutableStateOf(true) }
                var showOnboarding by remember { mutableStateOf(true) }
                var isLoggedIn by remember { mutableStateOf(false) }
                var activeUserName by remember { mutableStateOf("Mr. Jiharmok") }

                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = {
                            showSplash = false
                        }
                    )
                } else if (showOnboarding) {
                    OnboardingScreen(
                        onOnboardingFinished = {
                            showOnboarding = false
                        }
                    )
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        if (isLoggedIn) {
                            HomeScreen(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                userName = activeUserName,
                                onLogout = {
                                    isLoggedIn = false
                                }
                            )
                        } else {
                            AuthNavigation(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                onLoginSuccess = { userName ->
                                    activeUserName = userName.ifBlank { "Mr. Jiharmok" }
                                    isLoggedIn = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier,
    onLoginSuccess: (userName: String) -> Unit
) {
    var currentScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.Login) }
    var emailForOTP by remember { mutableStateOf("") }
    var registeredName by remember { mutableStateOf("Mr. Jiharmok") }

    Box(modifier = modifier) {
        when (currentScreen) {
            is AuthScreen.Login -> {
                LoginScreen(
                    onLogin = { email, _, _ ->
                        val fallbackName = email.substringBefore("@", "Mr. Jiharmok")
                        val nameToUse = registeredName.ifBlank { fallbackName }
                        currentScreen = AuthScreen.Login
                        onLoginSuccess(nameToUse)
                    },
                    onNavigateToRegister = {
                        currentScreen = AuthScreen.Register
                    },
                    onNavigateToForgotPassword = {
                        currentScreen = AuthScreen.ForgotPassword
                    }
                )
            }
            is AuthScreen.Register -> {
                RegisterScreen(
                    onNavigateBack = {
                        currentScreen = AuthScreen.Login
                    },
                    onRegisterSuccess = { name, email ->
                        registeredName = name.ifBlank { "Mr. Jiharmok" }
                        emailForOTP = email
                        currentScreen = AuthScreen.Login
                    }
                )
            }
            is AuthScreen.ForgotPassword -> {
                ForgotPasswordScreen(
                    onNavigateBack = {
                        currentScreen = AuthScreen.Login
                    },
                    onNavigateToOTP = { email ->
                        emailForOTP = email
                        currentScreen = AuthScreen.EnterOTP
                    }
                )
            }
            is AuthScreen.EnterOTP -> {
                EnterOTPScreen(
                    email = emailForOTP,
                    onNavigateBack = {
                        currentScreen = AuthScreen.ForgotPassword
                    },
                    onNavigateToNewPassword = { email ->
                        emailForOTP = email
                        currentScreen = AuthScreen.CreateNewPassword
                    },
                    onResendCode = { _ ->
                        // This is a UI-only flow; hook into real resend logic via this callback when backend is ready.
                    }
                )
            }
            is AuthScreen.CreateNewPassword -> {
                CreateNewPasswordScreen(
                    onNavigateBack = {
                        currentScreen = AuthScreen.EnterOTP
                    },
                    onPasswordChanged = {
                        currentScreen = AuthScreen.Login
                    }
                )
            }
        }
    }
}