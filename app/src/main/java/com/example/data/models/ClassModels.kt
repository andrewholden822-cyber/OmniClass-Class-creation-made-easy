package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val domain: String, // e.g. "Tech & Code", "Science & Academic", "Arts & Design", "Health & Fitness", "Business & Mindset"
    val tagline: String,
    val description: String,
    val level: String, // "Beginner", "Intermediate", "Advanced", "All Levels"
    val durationText: String, // e.g. "4 Weeks", "1 Day Bootcamp", "8 Modules"
    val instructorName: String,
    val instructorTitle: String,
    val instructorAvatarColor: String = "#4F46E5",
    val keyTakeawaysJson: String = "[]", // List of bullet learning outcomes
    val isEnrolled: Boolean = false,
    val progressPercent: Int = 0,
    val rating: Float = 4.9f,
    val studentCount: Int = 124,
    val bannerGradientStart: String = "#312E81",
    val bannerGradientEnd: String = "#1E1B4B",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey
    val id: String,
    val classId: String,
    val orderIndex: Int,
    val title: String,
    val summary: String,
    val durationMinutes: Int = 30,
    val lessonContentMarkdown: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "quizzes")
data class QuizQuestionEntity(
    @PrimaryKey
    val id: String,
    val classId: String,
    val question: String,
    val optionsJson: String, // JSON array of string options
    val correctIndex: Int,
    val explanation: String
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey
    val id: String,
    val classId: String,
    val frontTerm: String,
    val backDefinition: String
)

@Entity(tableName = "roster")
data class StudentRosterEntity(
    @PrimaryKey
    val id: String,
    val classId: String,
    val name: String,
    val email: String,
    val avatarColorHex: String,
    val gradePercent: Int,
    val assignmentStatus: String // "Submitted", "Graded", "Pending Review"
)

data class ChatMessage(
    val id: String,
    val classId: String,
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
