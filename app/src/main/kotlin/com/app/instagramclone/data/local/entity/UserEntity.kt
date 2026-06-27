package com.app.instagramclone.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val name: String,
    val bio: String?,
    val avatarUrl: String?,
    val createdAt: String
)
