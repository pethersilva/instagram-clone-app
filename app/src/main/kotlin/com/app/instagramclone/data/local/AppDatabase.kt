package com.app.instagramclone.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.instagramclone.data.local.dao.PostDao
import com.app.instagramclone.data.local.dao.UserDao
import com.app.instagramclone.data.local.entity.PostEntity
import com.app.instagramclone.data.local.entity.UserEntity

@Database(
    entities = [PostEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
}
