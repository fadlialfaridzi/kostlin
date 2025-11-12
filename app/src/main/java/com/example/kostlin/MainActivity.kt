package com.example.kostlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.kostlin.data.UserRepository
import com.example.kostlin.navigation.AuthScreen
import com.example.kostlin.ui.screen.autentikasi.CreateNewPasswordScreen
import com.example.kostlin.ui.screen.autentikasi.EnterOTPScreen
import com.example.kostlin.ui.screen.autentikasi.ForgotPasswordScreen
import com.example.kostlin.ui.screen.autentikasi.LoginScreen
import com.example.kostlin.ui.screen.autentikasi.RegisterScreen
import com.example.kostlin.ui.screen.home.HomeScreen
import com.example.kostlin.ui.theme.KostlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userRepository = UserRepository(this)

        setContent {
            KostlinTheme {
                var isLoggedIn by remember { mutableStateOf(userRepository.isUserLoggedIn()) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isLoggedIn) {
                        HomeScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            user = userRepository.getLoggedInUser(),
                            onLogout = {
                                userRepository.clearLoggedInUser()
                                isLoggedIn = false
                            }
                        )
                    } else {
                        AuthNavigation(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            userRepository = userRepository,
                            onLoginSuccess = {
                                isLoggedIn = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier,
    userRepository: UserRepository,
    onLoginSuccess: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.Login) }
    var emailForOTP by remember { mutableStateOf("") }

    androidx.compose.foundation.layout.Box(modifier = modifier) {
        when (currentScreen) {
            is AuthScreen.Login -> {
                LoginScreen(
                    onLoginSuccess = {
                        currentScreen = AuthScreen.Login
                        onLoginSuccess()
                    },
                    onNavigateToRegister = {
                        currentScreen = AuthScreen.Register
                    },
                    onNavigateToForgotPassword = {
                        currentScreen = AuthScreen.ForgotPassword
                    },
                    userRepository = userRepository
                )
            }
            is AuthScreen.Register -> {
                RegisterScreen(
                    onNavigateBack = {
                        currentScreen = AuthScreen.Login
                    },
                    onRegisterSuccess = {
                        currentScreen = AuthScreen.Login
                    },
                    userRepository = userRepository
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
                    },
                    userRepository = userRepository
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
                    userRepository = userRepository
                )
            }
            is AuthScreen.CreateNewPassword -> {
                CreateNewPasswordScreen(
                    email = emailForOTP,
                    onNavigateBack = {
                        currentScreen = AuthScreen.EnterOTP
                    },
                    onPasswordChanged = {
                        currentScreen = AuthScreen.Login
                    },
                    userRepository = userRepository
                )
            }
        }
    }
}