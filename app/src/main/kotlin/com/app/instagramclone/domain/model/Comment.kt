package com.app.instagramclone.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val author: Author,
    val content: String,
    val createdAt: String
)
