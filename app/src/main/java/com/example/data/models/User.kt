package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val name: String,
    val passwordHash: String,
    val role: String = "student", // "student" or "instructor"
    val avatarColorHex: String = "#6366F1",
    val titleOrBio: String = "Passionate Lifelong Learner",
    val isLoggedIn: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
