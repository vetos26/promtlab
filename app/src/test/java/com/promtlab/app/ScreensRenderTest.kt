package com.promtlab.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.navigation.PromtLabApp
import com.promtlab.app.ui.screens.about.AboutScreen
import com.promtlab.app.ui.screens.detail.PromptDetailScreen
import com.promtlab.app.ui.screens.favorites.FavoritesScreen
import com.promtlab.app.ui.screens.generate.GenerateScreen
import com.promtlab.app.ui.screens.learn.LearnScreen
import com.promtlab.app.ui.screens.settings.SettingsScreen
import com.promtlab.app.ui.screens.templates.TemplatesScreen
import com.promtlab.app.ui.theme.PromtLabTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Рендер каждого экрана в реальной Compose-среде (Robolectric).
 * Ловит падения темы, навигации, Room/DataStore и ленивых списков до релиза.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [26, 34], qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.LEGACY)
class ScreensRenderTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val viewModel: AppViewModel by lazy {
        AppViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun appRootWithDrawerRenders() {
        composeRule.setContent {
            PromtLabTheme {
                PromtLabApp(viewModel = viewModel)
            }
        }
        composeRule.onAllNodesWithText("Создать промт")[0].assertIsDisplayed()
    }

    @Test
    fun generateScreenRenders() {
        composeRule.setContent {
            PromtLabTheme { GenerateScreen(viewModel = viewModel) }
        }
        composeRule.onNodeWithText("Качество промта").assertIsDisplayed()
        composeRule.onNodeWithText("Роль модели").assertIsDisplayed()
    }

    @Test
    fun templatesScreenRenders() {
        composeRule.setContent {
            PromtLabTheme {
                TemplatesScreen(viewModel = viewModel, onTemplateApplied = {})
            }
        }
        composeRule.onNodeWithText("Поиск по шаблонам и задачам").assertIsDisplayed()
    }

    @Test
    fun favoritesScreenRenders() {
        composeRule.setContent {
            PromtLabTheme {
                FavoritesScreen(
                    viewModel = viewModel,
                    onOpenPrompt = {},
                    onCreatePrompt = {}
                )
            }
        }
        composeRule.onAllNodesWithText("Избранное")[0].assertIsDisplayed()
    }

    @Test
    fun learnScreenRenders() {
        composeRule.setContent {
            PromtLabTheme { LearnScreen(viewModel = viewModel) }
        }
        composeRule.onNodeWithText("Ваш прогресс").assertIsDisplayed()
    }

    @Test
    fun settingsScreenRenders() {
        composeRule.setContent {
            PromtLabTheme { SettingsScreen(viewModel = viewModel) }
        }
        composeRule.onNodeWithText("РЕЖИМ ОФОРМЛЕНИЯ").assertIsDisplayed()
    }

    @Test
    fun aboutScreenRenders() {
        composeRule.setContent {
            PromtLabTheme { AboutScreen(viewModel = viewModel) }
        }
        composeRule.onNodeWithText("Что умеет").assertIsDisplayed()
    }

    @Test
    fun promptDetailRendersForMissingId() {
        composeRule.setContent {
            PromtLabTheme {
                PromptDetailScreen(
                    viewModel = viewModel,
                    promptId = 999L,
                    onBack = {},
                    onGoCreate = {}
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Промт не найден").assertIsDisplayed()
    }
}
