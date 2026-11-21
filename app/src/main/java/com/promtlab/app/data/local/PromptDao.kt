package com.promtlab.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {

    @Query("SELECT * FROM prompts ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun observeFavorites(): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<PromptEntity?>

    @Insert
    suspend fun insert(entity: PromptEntity): Long

    @Query("UPDATE prompts SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query("UPDATE prompts SET title = :title WHERE id = :id")
    suspend fun rename(id: Long, title: String)

    @Query("UPDATE prompts SET title = :title, body = :body WHERE id = :id")
    suspend fun updateContent(id: Long, title: String, body: String)

    @Query("DELETE FROM prompts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM prompts")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM prompts")
    fun observeCount(): Flow<Int>
}
