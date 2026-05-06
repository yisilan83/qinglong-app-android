package com.qinglong.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qinglong.feature.login.HomeRoute
import com.qinglong.feature.login.LoginRoute
import com.qinglong.feature.login.LoginScreen
import com.qinglong.feature.login.LoginUiState

@Composable
fun QingLongApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginRoute
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
            // TODO: 后续实现首页
            androidx.compose.material3.Text("登录成功！")
        }
    }
}
