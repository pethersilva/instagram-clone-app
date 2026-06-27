package com.app.instagramclone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.instagramclone.ui.screen.auth.LoginScreen
import com.app.instagramclone.ui.screen.detail.PostDetailScreen
import com.app.instagramclone.ui.screen.feed.FeedScreen
import com.app.instagramclone.ui.screen.profile.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.Login.route
    ) {
        composable(AppDestinations.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppDestinations.Feed.route) {
                        popUpTo(AppDestinations.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.Feed.route) {
            FeedScreen(
                onPostClick = { postId ->
                    navController.navigate(AppDestinations.PostDetail.createRoute(postId))
                },
                onProfileClick = { userId ->
                    navController.navigate(AppDestinations.Profile.createRoute(userId))
                }
            )
        }

        composable(
            route = AppDestinations.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId").orEmpty()
            PostDetailScreen(postId = postId)
        }

        composable(
            route = AppDestinations.Profile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId").orEmpty()
            ProfileScreen(
                userId = userId,
                onPostClick = { postId ->
                    navController.navigate(AppDestinations.PostDetail.createRoute(postId))
                }
            )
        }
    }
}
