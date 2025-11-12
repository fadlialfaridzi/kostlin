package com.example.kostlin.navigation

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login")
    object Register : AuthScreen("register")
    object ForgotPassword : AuthScreen("forgot_password")
    object EnterOTP : AuthScreen("enter_otp")
    object CreateNewPassword : AuthScreen("create_new_password")
}

