package com.example.kostlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.kostlin.core.di.AppContainer
import com.example.kostlin.navigation.AuthScreen
import com.example.kostlin.ui.component.dialog.SuccessDialog
import com.example.kostlin.ui.screen.autentikasi.AuthViewModel
import com.example.kostlin.ui.screen.autentikasi.AuthViewModelFactory
import com.example.kostlin.ui.screen.autentikasi.CreateNewPasswordScreen
import com.example.kostlin.ui.screen.autentikasi.EnterOTPScreen
import com.example.kostlin.ui.screen.autentikasi.ForgotPasswordScreen
import com.example.kostlin.ui.screen.autentikasi.LoginScreen
import com.example.kostlin.ui.screen.autentikasi.RegisterScreen
import com.example.kostlin.ui.screen.home.HomeScreen
import com.example.kostlin.ui.screen.home.HomeViewModel
import com.example.kostlin.ui.screen.home.HomeViewModelFactory
import com.example.kostlin.ui.screen.onboarding.OnboardingScreen
import com.example.kostlin.ui.screen.splash.SplashScreen
import com.example.kostlin.ui.theme.KostlinTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AppContainer.authRepository)
    }

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(AppContainer.kosRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Request notification permission for Android 13+ (API 33+)
        requestNotificationPermission()

        setContent {
            KostlinTheme {
                var showSplash by remember { mutableStateOf(true) }
                var showOnboarding by remember { mutableStateOf(true) }
                var isLoggedIn by remember { mutableStateOf(false) }
                var activeUserName by remember { mutableStateOf("Mr. Jiharmok") }
                var showLoginSuccess by remember { mutableStateOf(false) }
                val authUiState by authViewModel.uiState.collectAsState()

                LaunchedEffect(authUiState.loggedInUser) {
                    authUiState.loggedInUser?.let { user ->
                        activeUserName = user.fullName.ifBlank {
                            user.email.substringBefore("@", "Mr. Jiharmok")
                        }
                        showLoginSuccess = true
                        authViewModel.consumeAuthSuccess()
                    }
                }

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
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .windowInsetsPadding(WindowInsets.safeDrawing)
                        ) {
                            if (isLoggedIn) {
                                HomeScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    userName = activeUserName,
                                    onLogout = {
                                        authViewModel.logout()
                                        isLoggedIn = false
                                    },
                                    homeViewModel = homeViewModel
                                )
                            } else {
                                AuthNavigation(
                                    modifier = Modifier.fillMaxSize(),
                                    authViewModel = authViewModel,
                                    onLoginSuccess = { userName ->
                                        activeUserName = userName.ifBlank { "Mr. Jiharmok" }
                                        showLoginSuccess = true
                                    }
                                )
                                
                                // Login Success Dialog
                                SuccessDialog(
                                    visible = showLoginSuccess,
                                    title = "Login Berhasil!",
                                    message = "Selamat datang kembali, $activeUserName",
                                    onDismiss = {
                                        showLoginSuccess = false
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
    
    /**
     * Request notification permission for Android 13+ (API 33+)
     * This is required to show push notifications on newer Android versions
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    
    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }
}

@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    onLoginSuccess: (userName: String) -> Unit
) {
    var currentScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.Login) }
    var emailForOTP by remember { mutableStateOf("") }
    var registeredName by remember { mutableStateOf("Mr. Jiharmok") }
    var registeredEmail by remember { mutableStateOf("") }
    var showRegisterSuccess by remember { mutableStateOf(false) }
    val authUiState by authViewModel.uiState.collectAsState()
    
    // Handle registration success
    LaunchedEffect(authUiState.registrationSuccessEmail) {
        authUiState.registrationSuccessEmail?.let { email ->
            registeredEmail = email
            registeredName = authUiState.registrationSuccessName ?: "User"
            showRegisterSuccess = true
            authViewModel.consumeRegistrationSuccess()
        }
    }

    Box(modifier = modifier) {
        when (currentScreen) {
            is AuthScreen.Login -> {
                LoginScreen(
                    onLogin = { email, password, rememberMe ->
                        registeredName = email.substringBefore("@", "Mr. Jiharmok")
                        authViewModel.login(email, password)
                    },
                    onNavigateToRegister = {
                        currentScreen = AuthScreen.Register
                    },
                    onNavigateToForgotPassword = {
                        currentScreen = AuthScreen.ForgotPassword
                    },
                    isLoading = authUiState.isLoading,
                    externalError = authUiState.errorMessage,
                    onClearError = authViewModel::clearError
                )
                authUiState.loggedInUser?.let { user ->
                    val displayName = user.fullName.ifBlank { registeredName }
                    onLoginSuccess(displayName)
                }
            }
            is AuthScreen.Register -> {
                RegisterScreen(
                    onNavigateBack = {
                        currentScreen = AuthScreen.Login
                    },
                    onRegisterSuccess = { name, email, password, phoneNumber ->
                        registeredName = name.ifBlank { "Mr. Jiharmok" }
                        emailForOTP = email
                        authViewModel.register(name, email, password, phoneNumber)
                    },
                    isLoading = authUiState.isLoading,
                    externalError = authUiState.errorMessage,
                    onClearError = authViewModel::clearError
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
        
        // Register Success Dialog
        SuccessDialog(
            visible = showRegisterSuccess,
            title = "Registrasi Berhasil!",
            message = "Akun $registeredEmail berhasil dibuat.\nSilahkan login untuk melanjutkan.",
            onDismiss = {
                showRegisterSuccess = false
                currentScreen = AuthScreen.Login
            },
            autoDismissMillis = 2500L
        )
    }
}