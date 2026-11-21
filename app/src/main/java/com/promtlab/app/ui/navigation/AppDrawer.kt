package com.promtlab.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promtlab.app.data.settings.ThemeMode

/**
 * Выдвижное меню слева: навигация по разделам, прогресс обучения
 * и быстрый переключатель темы.
 */
@Composable
fun AppDrawerSheet(
    selected: Dest?,
    themeMode: ThemeMode,
    isDark: Boolean,
    completedLessons: Int,
    totalLessons: Int,
    savedCount: Int,
    favoriteCount: Int,
    onSelect: (Dest) -> Unit,
    onThemeToggle: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    ModalDrawerSheet(
        modifier = Modifier.width(312.dp),
        drawerShape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp),
        drawerContainerColor = cs.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 14.dp, vertical = 20.dp)
        ) {
            DrawerHeader()

            Spacer(Modifier.height(22.dp))
            Text(
                text = "НАВИГАЦИЯ",
                style = MaterialTheme.typography.labelSmall,
                color = cs.onSurfaceVariant,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 10.dp, bottom = 6.dp)
            )

            Dest.items.forEach { dest ->
                DrawerItem(
                    dest = dest,
                    selected = dest == selected,
                    badge = when (dest) {
                        Dest.Favorites -> favoriteCount.takeIf { it > 0 }?.toString()
                        Dest.Generate -> savedCount.takeIf { it > 0 }?.toString()
                        else -> null
                    },
                    onClick = { onSelect(dest) }
                )
            }

            Spacer(Modifier.weight(1f))

            LearningProgressCard(completed = completedLessons, total = totalLessons)

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = cs.outlineVariant.copy(alpha = 0.6f))
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onThemeToggle)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                    contentDescription = null,
                    tint = cs.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Тёмная тема",
                        style = MaterialTheme.typography.titleSmall,
                        color = cs.onSurface
                    )
                    Text(
                        text = "Режим: ${themeMode.label.lowercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = cs.onSurfaceVariant
                    )
                }
                Switch(checked = isDark, onCheckedChange = { onThemeToggle() })
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "PromtLab 1.0.0 · Kotlin + Compose",
                style = MaterialTheme.typography.labelSmall,
                color = cs.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
private fun DrawerHeader() {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 6.dp, top = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(Brush.linearGradient(listOf(cs.primary, cs.tertiary))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "PromtLab",
                style = MaterialTheme.typography.titleLarge,
                color = cs.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Промт-инженерия под рукой",
                style = MaterialTheme.typography.labelSmall,
                color = cs.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrawerItem(
    dest: Dest,
    selected: Boolean,
    badge: String?,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val background by animateColorAsState(
        targetValue = if (selected) cs.primaryContainer.copy(alpha = 0.75f) else Color.Transparent,
        label = "drawerItemBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) cs.onPrimaryContainer else cs.onSurfaceVariant,
        label = "drawerItemContent"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    if (selected) {
                        cs.primary.copy(alpha = 0.16f)
                    } else {
                        cs.surfaceContainerHigh
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = dest.icon,
                contentDescription = null,
                tint = if (selected) cs.primary else cs.onSurfaceVariant,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = dest.label,
                style = MaterialTheme.typography.titleSmall,
                color = if (selected) cs.onSurface else contentColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = dest.description,
                style = MaterialTheme.typography.labelSmall,
                color = cs.onSurfaceVariant.copy(alpha = if (selected) 1f else 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(cs.primary.copy(alpha = 0.14f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = cs.primary
                )
            }
        } else if (selected) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(cs.primary)
            )
        }
    }
}

@Composable
private fun LearningProgressCard(completed: Int, total: Int) {
    val cs = MaterialTheme.colorScheme
    val fraction = if (total == 0) 0f else completed.toFloat() / total

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cs.surfaceContainerHigh.copy(alpha = 0.7f))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Обучение",
                    style = MaterialTheme.typography.titleSmall,
                    color = cs.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$completed / $total",
                    style = MaterialTheme.typography.labelSmall,
                    color = cs.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(CircleShape)
                    .background(cs.surfaceContainerLowest.copy(alpha = 0.6f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(cs.primary, cs.tertiary)))
                )
            }
            Text(
                text = if (completed == total) {
                    "Все уроки изучены ✨"
                } else {
                    "Осталось ${total - completed} уроков"
                },
                style = MaterialTheme.typography.labelSmall,
                color = cs.onSurfaceVariant
            )
        }
    }
}
