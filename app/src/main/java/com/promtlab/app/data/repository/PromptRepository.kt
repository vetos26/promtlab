package com.promtlab.app.data.repository

import com.promtlab.app.data.local.PromptDao
import com.promtlab.app.data.local.toEntity
import com.promtlab.app.data.local.toModel
import com.promtlab.app.data.model.SavedPrompt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PromptRepository(private val dao: PromptDao) {

    fun observeAll(): Flow<List<SavedPrompt>> =
        dao.observeAll().map { list -> list.map { it.toModel() } }

    fun observeFavorites(): Flow<List<SavedPrompt>> =
        dao.observeFavorites().map { list -> list.map { it.toModel() } }

    fun observeCount(): Flow<Int> = dao.observeCount()

    suspend fun save(prompt: SavedPrompt): Long = dao.insert(prompt.toEntity())

    suspend fun setFavorite(id: Long, favorite: Boolean) = dao.setFavorite(id, favorite)

    suspend fun rename(id: Long, title: String) = dao.rename(id, title.trim())

    suspend fun updateContent(id: Long, title: String, body: String) =
        dao.updateContent(id, title.trim(), body.trim())

    suspend fun delete(id: Long) = dao.deleteById(id)

    suspend fun clear() = dao.clear()
}
