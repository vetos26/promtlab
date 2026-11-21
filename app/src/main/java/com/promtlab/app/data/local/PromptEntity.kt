package com.promtlab.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.promtlab.app.data.model.PromptCategory
import com.promtlab.app.data.model.SavedPrompt

@Entity(tableName = "prompts")
data class PromptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val body: String,
    val category: String,
    val tone: String,
    val format: String,
    val createdAt: Long,
    val isFavorite: Boolean
)

fun PromptEntity.toModel(): SavedPrompt = SavedPrompt(
    id = id,
    title = title,
    body = body,
    category = PromptCategory.fromId(category),
    tone = tone,
    format = format,
    createdAt = createdAt,
    isFavorite = isFavorite
)

fun SavedPrompt.toEntity(): PromptEntity = PromptEntity(
    id = id,
    title = title,
    body = body,
    category = category.name,
    tone = tone,
    format = format,
    createdAt = createdAt,
    isFavorite = isFavorite
)
