package com.promtlab.app.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.components.CategoryBadge
import com.promtlab.app.ui.components.EmptyState
import com.promtlab.app.ui.components.GradientButton
import com.promtlab.app.ui.components.InfoPill
import com.promtlab.app.ui.components.LabeledField
import com.promtlab.app.ui.components.SectionCard
import com.promtlab.app.ui.components.SoftButton
import com.promtlab.app.util.copyToClipboard
import com.promtlab.app.util.formatFullDate
import com.promtlab.app.util.shareText

@Composable
fun PromptDetailScreen(
    viewModel: AppViewModel,
    promptId: Long,
    onBack: () -> Unit,
    onGoCreate: () -> Unit
) {
    val prompts by viewModel.savedPrompts.collectAsStateWithLifecycle()
    val prompt = prompts.firstOrNull { it.id == promptId }
    val context = LocalContext.current
    val cs = MaterialTheme.colorScheme

    var title by remember(promptId) { mutableStateOf("") }
    var body by remember(promptId) { mutableStateOf("") }
    var initialized by remember(promptId) { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(prompt) {
        val current = prompt
        if (!initialized && current != null) {
            title = current.title
            body = current.body
            initialized = true
        }
    }

    if (showDeleteDialog && prompt != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(26.dp),
            containerColor = cs.surfaceContainerHigh,
            title = { Text("Удалить промт?") },
            text = { Text("Действие необратимо: промт «${prompt.title}» исчезнет из истории.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deletePrompt(prompt)
                        onBack()
                    }
                ) {
                    Text("Удалить", color = cs.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            }
        )
    }

    if (prompt == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Назад")
            }
            EmptyState(
                icon = Icons.Rounded.Description,
                title = "Промт не найден",
                message = "Возможно, он был удалён. Вернитесь в историю или создайте новый.",
                actionLabel = "Создать промт",
                onAction = onGoCreate
            )
        }
        return
    }

    val promptText = body.ifBlank { prompt.body }
    val isDirty = title != prompt.title || body != prompt.body

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Назад",
                        tint = cs.onSurface
                    )
                }
                Spacer(Modifier.width(4.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Промт",
                        style = MaterialTheme.typography.titleLarge,
                        color = cs.onSurface
                    )
                    Text(
                        text = formatFullDate(prompt.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = cs.onSurfaceVariant
                    )
                }
                IconButton(onClick = { viewModel.toggleFavorite(prompt) }) {
                    Icon(
                        imageVector = if (prompt.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                        contentDescription = "Избранное",
                        tint = if (prompt.isFavorite) Color(0xFFFFB300) else cs.onSurfaceVariant
                    )
                }
                IconButton(onClick = { context.shareText(promptText, prompt.title) }) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = "Поделиться",
                        tint = cs.onSurfaceVariant
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Удалить",
                        tint = cs.error
                    )
                }
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryBadge(emoji = prompt.category.emoji)
                Column(Modifier.weight(1f)) {
                    Text(
                        text = prompt.category.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = cs.onSurface
                    )
                    Text(
                        text = "Тон: ${prompt.tone}",
                        style = MaterialTheme.typography.labelSmall,
                        color = cs.onSurfaceVariant
                    )
                }
                if (prompt.isFavorite) {
                    InfoPill(text = "Избранное", icon = Icons.Rounded.Star, tint = Color(0xFFFFB300))
                }
            }
        }

        item {
            SectionCard(title = "Содержание", icon = Icons.Rounded.Description) {
                LabeledField(
                    label = "Название",
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Название промта",
                    maxLines = 2
                )
                LabeledField(
                    label = "Текст промта",
                    value = body,
                    onValueChange = { body = it },
                    placeholder = "Текст промта",
                    minLines = 12,
                    maxLines = 40,
                    supporting = "Можно отредактировать текст и сохранить изменения"
                )
                GradientButton(
                    text = if (isDirty) "Сохранить изменения" else "Изменений нет",
                    onClick = { viewModel.updatePrompt(prompt, title, body) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isDirty
                )
            }
        }

        item {
            SectionCard(title = "Быстрые действия", icon = Icons.Rounded.Info) {
                GradientButton(
                    text = "Копировать промт",
                    onClick = {
                        context.copyToClipboard(promptText)
                        viewModel.showMessage("Промт скопирован")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.ContentCopy
                )
                SoftButton(
                    text = "Поделиться",
                    onClick = { context.shareText(promptText, prompt.title) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.Share
                )
                SoftButton(
                    text = "Удалить из истории",
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.Delete,
                    tint = cs.error,
                    textColor = cs.error
                )
            }
        }

        item {
            SectionCard(title = "Параметры", icon = Icons.Rounded.Info) {
                MetaRow(label = "Категория", value = prompt.category.label)
                MetaRow(label = "Тональность", value = prompt.tone)
                MetaRow(label = "Формат", value = prompt.format)
                MetaRow(label = "Создан", value = formatFullDate(prompt.createdAt))
                MetaRow(
                    label = "Размер",
                    value = "${prompt.body.length} символов · " +
                        "≈${(prompt.body.length / 3.2).toInt()} токенов"
                )
            }
        }
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    val cs = MaterialTheme.colorScheme
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurface
        )
    }
}
