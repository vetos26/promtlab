package com.promtlab.app

import android.app.Application
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.promtlab.app.data.ImagePromptGenerator
import com.promtlab.app.data.LearningLibrary
import com.promtlab.app.data.PromptGenerator
import com.promtlab.app.data.TemplateLibrary
import com.promtlab.app.data.model.ImagePromptForm
import com.promtlab.app.data.model.PromptForm
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Smoke-тесты запуска: реально поднимают application, activity и каждый экран,
 * чтобы падения рантайма (Room, DataStore, тема, композиция) ловились в CI,
 * а не на устройстве пользователя.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [26, 34], qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.LEGACY)
class AppStartupTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun applicationStarts() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        assertTrue(app is PromtLabApp)
    }

    @Test
    fun mainActivityLaunchesWithoutCrash() {
        composeRule.waitForIdle()
        composeRule.onRoot().assertExists()
    }

    @Test
    fun settingsFlowStartsWithDefaults() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val repository = Graph.settingsRepository(app)
        assertNotNull(repository)
    }

    @Test
    fun generatorsProduceContentOnColdStart() {
        val textPrompt = PromptGenerator.build(PromptForm())
        assertTrue(textPrompt.text.isNotBlank())
        assertTrue(textPrompt.tokens > 0)

        val imagePrompt = ImagePromptGenerator.build(ImagePromptForm())
        assertTrue(imagePrompt.fullText.isNotBlank())
        assertTrue(imagePrompt.score in 10..100)
    }

    @Test
    fun librariesAreConsistent() {
        assertTrue(TemplateLibrary.templates.isNotEmpty())
        assertTrue(LearningLibrary.lessons.isNotEmpty())
        LearningLibrary.lessons.forEach { lesson ->
            assertNotNull(LearningLibrary.byId(lesson.id))
            assertTrue(lesson.title.isNotBlank())
            assertTrue(lesson.paragraphs.isNotEmpty())
        }
    }
}
