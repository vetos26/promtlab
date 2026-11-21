package com.promtlab.app.ui.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.components.ChoiceChips
import com.promtlab.app.ui.components.EmptyState
import com.promtlab.app.ui.components.PromptItemCard
import com.promtlab.app.ui.components.ScreenHeader
import com.promtlab.app.ui.components.SearchField

/**
 * Сохранённые промты: по умолчанию показаны избранные,
 * переключатель открывает всю коллекцию.
 */
@Composable
fun FavoritesScreen(
    viewModel: AppViewModel,
    onOpenPrompt: (Long) -> Unit,
    onCreatePrompt: () -> Unit
) {
    val prompts by viewModel.savedPrompts.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var favoritesOnly by remember { mutableStateOf(true) }

    val favoritesCount = prompts.count { it.isFavorite }
    val visible = remember(prompts, query, favoritesOnly) {
        prompts.filter { prompt ->
            val matchesFilter = !favoritesOnly || prompt.isFavorite
            val matchesQuery = query.isBlank() ||
                prompt.title.contains(query, ignoreCase = true) ||
                prompt.body.contains(query, ignoreCase = true) ||
                prompt.category.label.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "Избранное",
                subtitle = if (prompts.isEmpty()) {
                    "Сохраняйте промты — они появятся здесь"
                } else {
                    "В избранном $favoritesCount · всего сохранено ${prompts.size}"
                }
            )
        }
        item {
            SearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Поиск по названию, тексту или категории"
            )
        }
        item {
            ChoiceChips(
                options = listOf(true, false),
                selected = favoritesOnly,
                label = { if (it) "Избранное" else "Все промты" },
                onSelect = { favoritesOnly = it }
            )
        }

        when {
            prompts.isEmpty() -> item {
                EmptyState(
                    icon = Icons.Rounded.Star,
                    title = "Пока ничего не сохранено",
                    message = "Соберите промт в генераторе и нажмите «Сохранить» — " +
                        "он появится здесь.",
                    actionLabel = "Создать промт",
                    onAction = onCreatePrompt
                )
            }

            visible.isEmpty() -> item {
                EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    title = "Ничего не найдено",
                    message = if (favoritesOnly) {
                        "В избранном нет промтов по этому запросу."
                    } else {
                        "Попробуйте изменить запрос или сбросить фильтр."
                    },
                    actionLabel = "Сбросить фильтры",
                    onAction = {
                        query = ""
                        favoritesOnly = false
                    }
                )
            }

            else -> {
                item {
                    Text(
                        text = "Показано ${visible.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(items = visible, key = { it.id }) { prompt ->
                    PromptItemCard(
                        prompt = prompt,
                        onClick = { onOpenPrompt(prompt.id) },
                        onToggleFavorite = { viewModel.toggleFavorite(prompt) }
                    )
                }
            }
        }
    }
}
