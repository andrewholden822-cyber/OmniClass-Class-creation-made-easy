package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.export.WebExporter
import com.example.data.local.SampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("OmniClass", appName)
    }

    @Test
    fun `web exporter generates valid responsive html website`() {
        val sampleCourse = SampleData.starterClasses.first()
        val sampleModules = SampleData.getSampleModules()
        val sampleQuizzes = SampleData.getSampleQuizzes()
        val sampleFlashcards = SampleData.getSampleFlashcards()
        val sampleRoster = SampleData.getSampleRoster()

        val html = WebExporter.generateCourseWebsiteHtml(
            course = sampleCourse,
            modules = sampleModules,
            quizzes = sampleQuizzes,
            flashcards = sampleFlashcards,
            roster = sampleRoster
        )

        assertTrue(html.contains("<!DOCTYPE html>"))
        assertTrue(html.contains("OmniClass Portal"))
        assertTrue(html.contains("Modern Android"))
        assertTrue(html.contains("AI Engineering"))
        assertTrue(html.contains("authModal"))
        assertTrue(html.contains("quiz-box"))
        assertTrue(html.contains("flashcard"))
    }
}
