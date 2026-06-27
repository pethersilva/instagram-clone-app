package com.app.instagramclone.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorUsername: String,
    val authorAvatarUrl: String?,
    val mediaUrl: String?,
    val caption: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val likedByMe: Boolean,
    val createdAt: String
)
