package com.promtlab.app.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.components.ChoiceChips
import com.promtlab.app.ui.components.EmptyState
import com.promtlab.app.ui.components.PromptItemCard
import com.promtlab.app.ui.components.ScreenHeader
import com.promtlab.app.ui.components.SearchField

@Composable
fun HistoryScreen(
    viewModel: AppViewModel,
    onOpenPrompt: (Long) -> Unit,
    onCreatePrompt: () -> Unit
) {
    val prompts by viewModel.filteredPrompts.collectAsStateWithLifecycle()
    val allPrompts by viewModel.savedPrompts.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val favoritesOnly by viewModel.favoritesOnly.collectAsStateWithLifecycle()

    val favoritesCount = allPrompts.count { it.isFavorite }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "История",
                subtitle = if (allPrompts.isEmpty()) {
                    "Сохраняйте удачные промты, чтобы переиспользовать"
                } else {
                    "Сохранено ${allPrompts.size} · в избранном $favoritesCount"
                }
            )
        }
        item {
            SearchField(
                value = query,
                onValueChange = viewModel::setSearchQuery,
                placeholder = "Поиск по названию, тексту или категории"
            )
        }
        item {
            ChoiceChips(
                options = listOf(false, true),
                selected = favoritesOnly,
                label = { if (it) "Избранное" else "Все промты" },
                onSelect = viewModel::setFavoritesOnly
            )
        }

        when {
            allPrompts.isEmpty() -> item {
                EmptyState(
                    icon = Icons.Rounded.History,
                    title = "История пока пуста",
                    message = "Соберите промт в конструкторе и нажмите «Сохранить» — он появится здесь.",
                    actionLabel = "Создать промт",
                    onAction = onCreatePrompt
                )
            }

            prompts.isEmpty() -> item {
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
                        viewModel.setSearchQuery("")
                        viewModel.setFavoritesOnly(false)
                    }
                )
            }

            else -> {
                item {
                    Text(
                        text = "Показано ${prompts.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(items = prompts, key = { it.id }) { prompt ->
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
