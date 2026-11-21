package com.promtlab.app.ui.screens.templates

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.promtlab.app.data.TemplateLibrary
import com.promtlab.app.data.model.PromptCategory
import com.promtlab.app.data.model.PromptTemplate
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.components.CategoryBadge
import com.promtlab.app.ui.components.ChoiceChips
import com.promtlab.app.ui.components.EmptyState
import com.promtlab.app.ui.components.InfoPill
import com.promtlab.app.ui.components.ScreenHeader
import com.promtlab.app.ui.components.SearchField
import com.promtlab.app.ui.components.SoftButton

@Composable
fun TemplatesScreen(
    viewModel: AppViewModel,
    onTemplateApplied: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<PromptCategory?>(null) }

    val templates = remember(query, category) {
        TemplateLibrary.templates.filter { template ->
            val matchesCategory = category == null || template.category == category
            val matchesQuery = query.isBlank() ||
                template.title.contains(query, ignoreCase = true) ||
                template.description.contains(query, ignoreCase = true) ||
                template.preset.task.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "Шаблоны",
                subtitle = "Проверенные промты — применяйте в один тап"
            )
        }
        item {
            SearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Поиск по шаблонам и задачам"
            )
        }
        item {
            ChoiceChips(
                options = listOf<PromptCategory?>(null) + PromptCategory.entries,
                selected = category,
                label = { it?.label ?: "Все" },
                onSelect = { category = it }
            )
        }
        item {
            Text(
                text = "Найдено: ${templates.size} из ${TemplateLibrary.templates.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (templates.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Rounded.GridView,
                    title = "Шаблоны не найдены",
                    message = "Попробуйте другой запрос или сбросьте фильтр категории.",
                    actionLabel = "Сбросить фильтры",
                    onAction = {
                        query = ""
                        category = null
                    }
                )
            }
        } else {
            items(items = templates, key = { it.id }) { template ->
                TemplateCard(
                    template = template,
                    onUse = {
                        viewModel.applyTemplate(template)
                        onTemplateApplied()
                    }
                )
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: PromptTemplate,
    onUse: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onUse),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surfaceContainerLow),
        border = BorderStroke(1.dp, cs.outlineVariant.copy(alpha = 0.65f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                CategoryBadge(emoji = template.category.emoji)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = template.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = cs.onSurface
                    )
                    Text(
                        text = template.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = cs.primary
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = cs.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoPill(text = template.preset.format.label)
                InfoPill(text = template.preset.tone.label, tint = cs.secondary)
                Spacer(Modifier.weight(1f))
                SoftButton(
                    text = "Использовать",
                    onClick = onUse,
                    icon = Icons.Rounded.ArrowForward
                )
            }
        }
    }
}
