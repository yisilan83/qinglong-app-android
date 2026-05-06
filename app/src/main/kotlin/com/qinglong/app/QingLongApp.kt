package com.qinglong.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qinglong.feature.login.HomeRoute
import com.qinglong.feature.login.LoginRoute
import com.qinglong.feature.login.LoginScreen

@Composable
fun QingLongApp(
    appViewModel: AppViewModel = hiltViewModel()
) {
    val isLoggedIn by appViewModel.isLoggedIn.collectAsStateWithLifecycle()

    if (isLoggedIn == null) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        return
    }

    val navController = rememberNavController()
    val startDestination = if (isLoggedIn == true) HomeRoute else LoginRoute

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                }
            )
        }
        composable<HomeRoute> {
            Surface(color = MaterialTheme.colorScheme.background) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.Text(
                        "登录成功！",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}
