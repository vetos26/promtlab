package com.promtlab.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.promtlab.app.data.settings.SettingsRepository
import com.promtlab.app.data.settings.ThemeMode
import com.promtlab.app.ui.navigation.Dest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Диагностика инициализации: если какой-то из стартовых путей падает,
 * тест печатает полную цепочку cause, а не только верхний ExceptionInInitializerError.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [26, 34], qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.LEGACY)
class RootCauseTest {

    private fun describe(t: Throwable): String = buildString {
        append(t.toString())
        var cause = t.cause
        var depth = 0
        while (cause != null && depth < 6) {
            append("\n  CAUSE[$depth]: $cause")
            cause.stackTrace.take(8).forEach { append("\n      at $it") }
            cause = cause.cause
            depth++
        }
        append("\n  STACK:")
        t.stackTrace.take(14).forEach { append("\n      at $it") }
    }

    @Test
    fun destInitialization() {
        try {
            Dest.items.forEach { it.route; it.label; it.icon }
        } catch (t: Throwable) {
            fail("Dest init failed: " + describe(t))
        }
    }

    @Test
    fun iconVectorInitialization() {
        try {
            val icon: ImageVector = Icons.Rounded.AutoAwesome
            require(icon.name.isNotEmpty())
        } catch (t: Throwable) {
            fail("Icon init failed: " + describe(t))
        }
    }

    @Test
    fun settingsRepositoryInitialization() {
        try {
            val repository = SettingsRepository(ApplicationProvider.getApplicationContext())
            val settings = runBlocking { repository.settings.first() }
            ThemeMode.entries.forEach { it.label }
            require(settings.defaultLanguage.isNotEmpty())
        } catch (t: Throwable) {
            fail("Settings init failed: " + describe(t))
        }
    }

    @Test
    fun databaseInitialization() {
        try {
            val db = Graph.database(ApplicationProvider.getApplicationContext())
            require(db.promptDao() != null)
        } catch (t: Throwable) {
            fail("Database init failed: " + describe(t))
        }
    }
}
