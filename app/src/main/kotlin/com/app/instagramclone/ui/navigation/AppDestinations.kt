package com.app.instagramclone.ui.navigation

sealed class AppDestinations(val route: String) {
    object Login : AppDestinations("login")
    object Feed : AppDestinations("feed")
    object PostDetail : AppDestinations("post/{postId}") {
        fun createRoute(postId: String) = "post/$postId"
    }
    object Profile : AppDestinations("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
}
