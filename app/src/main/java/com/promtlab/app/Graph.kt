package com.promtlab.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.promtlab.app.data.local.AppDatabase
import com.promtlab.app.data.repository.PromptRepository
import com.promtlab.app.data.settings.SettingsRepository

/**
 * Простой сервис-локатор: приложение небольшое, поэтому DI-фреймворк не нужен.
 */
object Graph {

    @Volatile
    private var database: AppDatabase? = null

    fun database(context: Context): AppDatabase =
        database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "promtlab.db"
            ).fallbackToDestructiveMigration().build().also { database = it }
        }

    fun promptRepository(context: Context): PromptRepository =
        PromptRepository(database(context).promptDao())

    fun settingsRepository(context: Context): SettingsRepository =
        SettingsRepository(context.applicationContext)
}

class PromtLabApp : Application()
