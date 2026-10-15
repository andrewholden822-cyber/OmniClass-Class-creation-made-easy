package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.ClassEntity
import com.example.data.models.FlashcardEntity
import com.example.data.models.ModuleEntity
import com.example.data.models.QuizQuestionEntity
import com.example.data.models.StudentRosterEntity
import com.example.data.models.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getActiveUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAll()
}

@Dao
interface ClassDao {
    @Query("SELECT * FROM classes ORDER BY createdAt DESC")
    fun getAllClasses(): Flow<List<ClassEntity>>

    @Query("SELECT * FROM classes WHERE isEnrolled = 1 ORDER BY createdAt DESC")
    fun getEnrolledClasses(): Flow<List<ClassEntity>>

    @Query("SELECT * FROM classes WHERE id = :id LIMIT 1")
    fun getClassById(id: String): Flow<ClassEntity?>

    @Query("SELECT * FROM classes WHERE id = :id LIMIT 1")
    suspend fun getClassByIdSync(id: String): ClassEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classEntity: ClassEntity)

    @Update
    suspend fun updateClass(classEntity: ClassEntity)

    @Query("UPDATE classes SET isEnrolled = :isEnrolled WHERE id = :classId")
    suspend fun setEnrollment(classId: String, isEnrolled: Boolean)

    @Query("UPDATE classes SET progressPercent = :progress WHERE id = :classId")
    suspend fun updateProgress(classId: String, progress: Int)

    @Query("DELETE FROM classes WHERE id = :classId")
    suspend fun deleteClassById(classId: String)

    // Modules
    @Query("SELECT * FROM modules WHERE classId = :classId ORDER BY orderIndex ASC")
    fun getModulesForClass(classId: String): Flow<List<ModuleEntity>>

    @Query("SELECT * FROM modules WHERE classId = :classId ORDER BY orderIndex ASC")
    suspend fun getModulesForClassSync(classId: String): List<ModuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ModuleEntity>)

    @Query("UPDATE modules SET isCompleted = :isCompleted WHERE id = :moduleId")
    suspend fun setModuleCompleted(moduleId: String, isCompleted: Boolean)

    // Quizzes
    @Query("SELECT * FROM quizzes WHERE classId = :classId")
    fun getQuizzesForClass(classId: String): Flow<List<QuizQuestionEntity>>

    @Query("SELECT * FROM quizzes WHERE classId = :classId")
    suspend fun getQuizzesForClassSync(classId: String): List<QuizQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<QuizQuestionEntity>)

    // Flashcards
    @Query("SELECT * FROM flashcards WHERE classId = :classId")
    fun getFlashcardsForClass(classId: String): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE classId = :classId")
    suspend fun getFlashcardsForClassSync(classId: String): List<FlashcardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<FlashcardEntity>)

    // Roster
    @Query("SELECT * FROM roster WHERE classId = :classId")
    fun getRosterForClass(classId: String): Flow<List<StudentRosterEntity>>

    @Query("SELECT * FROM roster WHERE classId = :classId")
    suspend fun getRosterForClassSync(classId: String): List<StudentRosterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoster(roster: List<StudentRosterEntity>)
}
