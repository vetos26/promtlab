package com.promtlab.app.ui.screens.learn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.promtlab.app.data.LearningLesson
import com.promtlab.app.data.LearningLibrary
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.components.ScreenHeader
import com.promtlab.app.ui.components.SectionCard
import com.promtlab.app.ui.components.SoftButton
import com.promtlab.app.util.copyToClipboard

/**
 * Короткие уроки по промт-инжинирингу с прогрессом чтения
 * и готовыми примерами промтов.
 */
@Composable
fun LearnScreen(viewModel: AppViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val completed = settings.completedLessons
    var expandedId by remember { mutableStateOf<String?>(null) }

    val lessons = LearningLibrary.lessons

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "Обучение",
                subtitle = "Короткие уроки: от структуры промта до изображений"
            )
        }
        item {
            ProgressCard(
                completed = completed.size.coerceAtMost(lessons.size),
                total = lessons.size
            )
        }
        items(items = lessons, key = { it.id }) { lesson ->
            LessonCard(
                lesson = lesson,
                isRead = lesson.id in completed,
                expanded = expandedId == lesson.id,
                onToggleExpand = {
                    expandedId = if (expandedId == lesson.id) null else lesson.id
                },
                onToggleRead = { viewModel.toggleLessonCompleted(lesson.id) },
                onCopyExample = { example ->
                    context.copyToClipboard(example, "Пример промта")
                    viewModel.showMessage("Пример скопирован")
                }
            )
        }
        item {
            SectionCard(title = "Как пользоваться материалами") {
                Text(
                    text = "Открывайте уроки по порядку, пробуйте примеры в генераторе " +
                        "и отмечайте прочитанное — прогресс сохраняется и виден в меню.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProgressCard(completed: Int, total: Int) {
    val cs = MaterialTheme.colorScheme
    val target = if (total == 0) 0f else completed.toFloat() / total
    val fraction by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(500),
        label = "lessonProgress"
    )

    SectionCard(title = "Ваш прогресс", icon = Icons.Rounded.CheckCircle) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$completed из $total",
                style = MaterialTheme.typography.headlineSmall,
                color = cs.primary
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = if (completed == total && total > 0) {
                    "Все уроки изучены — отлично!"
                } else {
                    "уроков изучено"
                },
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .background(cs.surfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(cs.primary, cs.tertiary)))
            )
        }
    }
}

@Composable
private fun LessonCard(
    lesson: LearningLesson,
    isRead: Boolean,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleRead: () -> Unit,
    onCopyExample: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val chevronColor by animateColorAsState(
        targetValue = if (expanded) cs.primary else cs.onSurfaceVariant,
        label = "chevronColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surfaceContainerLow),
        border = BorderStroke(1.dp, cs.outlineVariant.copy(alpha = 0.65f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(cs.primaryContainer.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = lesson.emoji, fontSize = 19.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = cs.onSurface
                    )
                    Text(
                        text = lesson.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = if (expanded) "Свернуть" else "Развернуть",
                    tint = chevronColor
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    lesson.paragraphs.forEach { paragraph ->
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyMedium,
                            color = cs.onSurfaceVariant
                        )
                    }

                    lesson.example?.let { example ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "ПРИМЕР ПРОМТА",
                                style = MaterialTheme.typography.labelSmall,
                                color = cs.primary,
                                letterSpacing = 0.8.sp
                            )
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = cs.surfaceContainerHighest.copy(alpha = 0.5f),
                                border = BorderStroke(
                                    1.dp,
                                    cs.outlineVariant.copy(alpha = 0.6f)
                                )
                            ) {
                                Text(
                                    text = example,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 18.sp
                                    ),
                                    color = cs.onSurface
                                )
                            }
                            SoftButton(
                                text = "Скопировать пример",
                                onClick = { onCopyExample(example) },
                                icon = Icons.Rounded.ContentCopy
                            )
                        }
                    }

                    if (lesson.checklist.isNotEmpty()) {
                        Text(
                            text = "ЧЕК-ЛИСТ",
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.primary,
                            letterSpacing = 0.8.sp
                        )
                        lesson.checklist.forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = cs.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cs.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isRead) {
                                    cs.primary.copy(alpha = 0.12f)
                                } else {
                                    cs.surfaceContainerHigh
                                }
                            )
                            .clickable(onClick = onToggleRead)
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isRead) {
                                Icons.Rounded.CheckCircle
                            } else {
                                Icons.Rounded.RadioButtonUnchecked
                            },
                            contentDescription = null,
                            tint = if (isRead) cs.primary else cs.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = if (isRead) "Прочитано" else "Отметить как прочитанное",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isRead) cs.primary else cs.onSurface
                        )
                    }
                }
            }
        }
    }
}

