package com.app.instagramclone.domain.model

data class Post(
    val id: String,
    val author: Author,
    val mediaUrl: String?,
    val caption: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val likedByMe: Boolean,
    val createdAt: String
)
