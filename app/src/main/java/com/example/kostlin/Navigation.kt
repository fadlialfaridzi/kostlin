package com.example.kostlin

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kostlin.ui.screen.autentikasi.LoginScreen
import com.example.kostlin.ui.screen.splash.SplashScreen

@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        //Splash Screen
        composable("splash") {
            SplashScreen(onSplashFinished = {
                navController.navigate("LoginScreen") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            })
        }

        composable("LoginScreen") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("HomeScreen") {
                    popUpTo("LoginScreen") {
                        inclusive = true
                    }
                }
            })
        }
    }
}

