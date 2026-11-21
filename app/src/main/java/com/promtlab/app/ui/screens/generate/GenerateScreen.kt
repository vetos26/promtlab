package com.promtlab.app.ui.screens.generate

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.promtlab.app.data.model.AnswerLength
import com.promtlab.app.data.model.OutputFormat
import com.promtlab.app.data.model.PromptForm
import com.promtlab.app.data.model.PromptLanguage
import com.promtlab.app.data.model.Technique
import com.promtlab.app.data.model.Tone
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.GeneratorMode
import com.promtlab.app.ui.components.ChoiceChips
import com.promtlab.app.ui.components.CircleActionButton
import com.promtlab.app.ui.components.GradientButton
import com.promtlab.app.ui.components.GradientIconButton
import com.promtlab.app.ui.components.InfoPill
import com.promtlab.app.ui.components.LabeledField
import com.promtlab.app.ui.components.MultiChoiceChips
import com.promtlab.app.ui.components.QualityCard
import com.promtlab.app.ui.components.SavePromptDialog
import com.promtlab.app.ui.components.ScreenHeader
import com.promtlab.app.ui.components.SectionCard
import com.promtlab.app.util.copyToClipboard
import com.promtlab.app.util.shareText
import com.promtlab.app.util.titleFromPrompt

/**
 * Единый экран генератора: текстовые промты и промты для изображений
 * переключаются сегментом в верхней части экрана.
 */
@Composable
fun GenerateScreen(viewModel: AppViewModel) {
    val mode by viewModel.generatorMode.collectAsStateWithLifecycle()

    val onRandomize = {
        if (mode == GeneratorMode.IMAGE) viewModel.randomizeImage() else viewModel.randomize()
    }
    val onReset = {
        if (mode == GeneratorMode.IMAGE) viewModel.resetImageForm() else viewModel.resetForm()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(
                title = "Создать промт",
                subtitle = when (mode) {
                    GeneratorMode.TEXT -> "Текст: роль модели, задача и приёмы"
                    GeneratorMode.IMAGE -> "Изображение: сюжет, стиль, свет и параметры"
                },
                action = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircleActionButton(
                            icon = Icons.Rounded.Refresh,
                            contentDescription = "Очистить",
                            onClick = onReset,
                            size = 48
                        )
                        GradientIconButton(
                            icon = Icons.Rounded.Casino,
                            contentDescription = "Случайная идея",
                            onClick = onRandomize,
                            size = 48
                        )
                    }
                }
            )
            Spacer(Modifier.size(14.dp))
            ModeSwitcher(
                mode = mode,
                onModeChange = viewModel::setGeneratorMode
            )
            Spacer(Modifier.size(6.dp))
        }

        when (mode) {
            GeneratorMode.TEXT -> TextPromptPane(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )

            GeneratorMode.IMAGE -> ImagePromptPane(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ModeSwitcher(
    mode: GeneratorMode,
    onModeChange: (GeneratorMode) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cs.surfaceContainerHigh)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        GeneratorMode.entries.forEach { item ->
            val selected = item == mode
            val pillColor by animateColorAsState(
                targetValue = if (selected) cs.primaryContainer else Color.Transparent,
                label = "modePill"
            )
            val contentColor by animateColorAsState(
                targetValue = if (selected) cs.onPrimaryContainer else cs.onSurfaceVariant,
                label = "modeContent"
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(pillColor)
                    .clickable { onModeChange(item) }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (item == GeneratorMode.TEXT) {
                        Icons.Rounded.AutoAwesome
                    } else {
                        Icons.Rounded.Image
                    },
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor
                )
            }
        }
    }
}

/* ------------------------------------------------------------------------ */
/*                          Режим «Текст»                                   */
/* ------------------------------------------------------------------------ */

@Composable
fun TextPromptPane(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val generated by viewModel.generated.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showSaveDialog by remember { mutableStateOf(false) }
    var previewExpanded by remember { mutableStateOf(false) }

    if (showSaveDialog) {
        SavePromptDialog(
            defaultTitle = titleFromPrompt(generated.text),
            onDismiss = { showSaveDialog = false },
            onConfirm = { title, category ->
                viewModel.saveTextPrompt(title, category)
                showSaveDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            QualityCard(score = generated.score, hints = generated.hints)
        }

        item {
            SectionCard(title = "Роль модели", icon = Icons.Rounded.Description) {
                Text(
                    text = "Кем должна быть модель — это задаёт уровень экспертизы и стиль.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                RoleSuggestions(
                    onPick = { role ->
                        viewModel.updateForm { current -> current.copy(role = role) }
                    }
                )
                LabeledField(
                    label = "Роль",
                    value = form.role,
                    onValueChange = { value ->
                        viewModel.updateForm { current -> current.copy(role = value) }
                    },
                    placeholder = "Например: senior-редактор с опытом в EdTech",
                    maxLines = 3
                )
                LabeledField(
                    label = "Задача",
                    value = form.task,
                    onValueChange = { value ->
                        viewModel.updateForm { current -> current.copy(task = value) }
                    },
                    placeholder = "Что именно нужно сделать? Начните с глагола: «напиши», «проанализируй»…",
                    minLines = 3,
                    maxLines = 7,
                    supporting = "Чем конкретнее задача, тем меньше догадок у модели"
                )
                LabeledField(
                    label = "Контекст",
                    value = form.context,
                    onValueChange = { value ->
                        viewModel.updateForm { current -> current.copy(context = value) }
                    },
                    placeholder = "Продукт, данные, метрики, ограничения, что уже пробовали",
                    minLines = 2,
                    maxLines = 6
                )
                LabeledField(
                    label = "Аудитория",
                    value = form.audience,
                    onValueChange = { value ->
                        viewModel.updateForm { current -> current.copy(audience = value) }
                    },
                    placeholder = "Для кого ответ: роль, уровень подготовки, ожидания",
                    maxLines = 3
                )
            }
        }

        item {
            SectionCard(title = "Стиль и формат", icon = Icons.Rounded.Tune) {
                FieldLabel("ТОНАЛЬНОСТЬ")
                ChoiceChips(
                    options = Tone.entries,
                    selected = form.tone,
                    label = { it.label },
                    onSelect = { tone ->
                        viewModel.updateForm { current -> current.copy(tone = tone) }
                    }
                )
                FieldLabel("ФОРМАТ ОТВЕТА")
                ChoiceChips(
                    options = OutputFormat.entries,
                    selected = form.format,
                    label = { it.label },
                    onSelect = { format ->
                        viewModel.updateForm { current -> current.copy(format = format) }
                    }
                )
                FieldLabel("ОБЪЁМ")
                ChoiceChips(
                    options = AnswerLength.entries,
                    selected = form.length,
                    label = { it.label },
                    onSelect = { length ->
                        viewModel.updateForm { current -> current.copy(length = length) }
                    }
                )
                FieldLabel("ЯЗЫК ОТВЕТА")
                ChoiceChips(
                    options = PromptLanguage.entries,
                    selected = form.language,
                    label = { it.label },
                    onSelect = { language ->
                        viewModel.updateForm { current -> current.copy(language = language) }
                    }
                )
            }
        }

        item {
            SectionCard(title = "Ограничения", icon = Icons.Rounded.Shield) {
                ConstraintSuggestions(
                    onPick = { constraint ->
                        viewModel.updateForm { current ->
                            current.copy(
                                constraints = appendConstraint(current.constraints, constraint)
                            )
                        }
                    }
                )
                LabeledField(
                    label = "Что нельзя и что важно",
                    value = form.constraints,
                    onValueChange = { value ->
                        viewModel.updateForm { current -> current.copy(constraints = value) }
                    },
                    placeholder = "Например: без кликбейта, не выдумывай цифры, уложись в 500 слов",
                    minLines = 2,
                    maxLines = 5
                )
            }
        }

        item {
            SectionCard(title = "Приёмы промт-инжиниринга", icon = Icons.Rounded.Bolt) {
                Text(
                    text = "Каждый включённый приём делает ответ управляемее и предсказуемее.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                MultiChoiceChips(
                    options = Technique.entries,
                    selected = form.techniques,
                    label = { it.label },
                    onToggle = { technique ->
                        viewModel.updateForm { current ->
                            current.copy(techniques = toggleTechnique(current.techniques, technique))
                        }
                    }
                )
            }
        }

        item {
            TextPreviewCard(
                form = form,
                text = generated.text,
                chars = generated.chars,
                words = generated.words,
                tokens = generated.tokens,
                showTokens = settings.showTokenCounter,
                expanded = previewExpanded,
                onToggleExpanded = { previewExpanded = !previewExpanded },
                onCopy = {
                    context.copyToClipboard(generated.text)
                    viewModel.showMessage("Промт скопирован в буфер обмена")
                },
                onShare = {
                    context.shareText(generated.text, subject = form.task.take(48).ifBlank { "Промт" })
                },
                onSave = { showSaveDialog = true }
            )
        }
    }
}

/* ------------------------------------------------------------------------ */
/*                              Вспомогательные                             */
/* ------------------------------------------------------------------------ */

@Composable
private fun RoleSuggestions(onPick: (String) -> Unit) {
    val roles = listOf(
        "senior-копирайтер",
        "ведущий Kotlin-разработчик",
        "продуктовый аналитик",
        "маркетолог-стратег",
        "наставник по обучению",
        "редактор научпопа"
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(roles) { role ->
            AssistChip(
                onClick = { onPick("$role с опытом 10+ лет") },
                label = {
                    Text(text = role, style = MaterialTheme.typography.labelSmall)
                },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun ConstraintSuggestions(onPick: (String) -> Unit) {
    val constraints = listOf(
        "Без воды",
        "Только факты",
        "До 500 слов",
        "Без кликбейта",
        "Не выдумывай данные"
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(constraints) { constraint ->
            AssistChip(
                onClick = { onPick(constraint) },
                label = {
                    Text(text = "+ $constraint", style = MaterialTheme.typography.labelSmall)
                },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun TextPreviewCard(
    form: PromptForm,
    text: String,
    chars: Int,
    words: Int,
    tokens: Int,
    showTokens: Boolean,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    SectionCard(title = "Готовый промт", icon = Icons.Rounded.AutoAwesome) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoPill(text = "$chars символов")
            InfoPill(text = "$words слов")
            if (showTokens) {
                InfoPill(text = "≈$tokens токенов", tint = cs.secondary)
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = cs.surfaceContainerHighest.copy(alpha = 0.45f),
            border = BorderStroke(1.dp, cs.outlineVariant.copy(alpha = 0.6f))
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 19.sp
                ),
                color = cs.onSurface,
                maxLines = if (expanded) Int.MAX_VALUE else 10,
                overflow = TextOverflow.Ellipsis
            )
        }
        TextButton(onClick = onToggleExpanded) {
            Text(
                text = if (expanded) "Свернуть" else "Показать полностью",
                style = MaterialTheme.typography.labelLarge
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientButton(
                text = "Копировать",
                onClick = onCopy,
                icon = Icons.Rounded.ContentCopy,
                modifier = Modifier.weight(1f)
            )
            CircleActionButton(
                icon = Icons.Rounded.Share,
                contentDescription = "Поделиться промтом",
                onClick = onShare
            )
            CircleActionButton(
                icon = Icons.Rounded.BookmarkAdd,
                contentDescription = "Сохранить в историю",
                onClick = onSave
            )
        }
        Text(
            text = "Формат: ${form.format.label} · Тон: ${form.tone.label} · ${form.length.label}",
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant
        )
    }
}

/* ------------------------------ Утилиты формы ---------------------------- */

private fun toggleTechnique(set: Set<Technique>, technique: Technique): Set<Technique> =
    if (technique in set) set - technique else set + technique

private fun appendConstraint(current: String, constraint: String): String {
    if (current.contains(constraint, ignoreCase = true)) return current
    return when {
        current.isBlank() -> constraint
        current.trimEnd().endsWith(",") -> "$current $constraint"
        else -> "${current.trimEnd()}, $constraint"
    }
}
