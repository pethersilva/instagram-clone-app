package com.app.instagramclone.domain.model

data class User(
    val id: String,
    val username: String,
    val name: String,
    val bio: String?,
    val avatarUrl: String?,
    val createdAt: String
)

data class Author(
    val id: String,
    val username: String,
    val avatarUrl: String?
)
