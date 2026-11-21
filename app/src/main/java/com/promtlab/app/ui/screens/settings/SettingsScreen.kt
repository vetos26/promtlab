package com.promtlab.app.ui.screens.settings

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.promtlab.app.data.settings.ThemeMode
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.components.ChoiceChips
import com.promtlab.app.ui.components.InfoPill
import com.promtlab.app.ui.components.ScreenHeader
import com.promtlab.app.ui.components.SectionCard
import com.promtlab.app.ui.components.SoftButton
import com.promtlab.app.ui.theme.AccentPalette
import com.promtlab.app.util.shareText

@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val prompts by viewModel.savedPrompts.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val cs = MaterialTheme.colorScheme
    val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            shape = RoundedCornerShape(26.dp),
            containerColor = cs.surfaceContainerHigh,
            title = { Text("Очистить историю?") },
            text = {
                Text(
                    "Будут удалены все сохранённые промты (${prompts.size} шт.). " +
                        "Действие необратимо."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearHistory()
                    }
                ) {
                    Text("Очистить", color = cs.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Отмена") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "Настройки",
                subtitle = "Оформление, данные и о приложении"
            )
        }

        item {
            SectionCard(title = "Тема", icon = Icons.Rounded.DarkMode) {
                SettingsLabel("РЕЖИМ ОФОРМЛЕНИЯ")
                ChoiceChips(
                    options = ThemeMode.entries,
                    selected = settings.themeMode,
                    label = { it.label },
                    onSelect = viewModel::setThemeMode
                )
                SettingsLabel("АКЦЕНТНЫЙ ЦВЕТ")
                AccentPicker(
                    selected = settings.accent,
                    onSelect = viewModel::setAccent
                )
                Text(
                    text = "Палитра применяется ко всем экранам, кнопкам и нижнему меню.",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
            }
        }

        item {
            SectionCard(title = "Интерфейс", icon = Icons.Rounded.Tune) {
                ToggleRow(
                    title = "Динамические цвета",
                    subtitle = if (supportsDynamicColor) {
                        "Подстраиваться под обои устройства (Android 12+)"
                    } else {
                        "Доступно на Android 12 и новее"
                    },
                    checked = settings.dynamicColor,
                    enabled = supportsDynamicColor,
                    onCheckedChange = viewModel::setDynamicColor
                )
                ToggleRow(
                    title = "Счётчик токенов",
                    subtitle = "Показывать оценку размера промта в превью",
                    checked = settings.showTokenCounter,
                    enabled = true,
                    onCheckedChange = viewModel::setShowTokenCounter
                )
            }
        }

        item {
            SectionCard(title = "Данные", icon = Icons.Rounded.Storage) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoPill(text = "${prompts.size} сохранено")
                    InfoPill(
                        text = "${prompts.count { it.isFavorite }} в избранном",
                        tint = cs.secondary
                    )
                }
                SoftButton(
                    text = "Экспортировать все промты",
                    onClick = {
                        if (prompts.isEmpty()) {
                            viewModel.showMessage("История пуста — нечего экспортировать")
                        } else {
                            context.shareText(viewModel.exportAll(), "Экспорт PromtLab")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.Share
                )
                SoftButton(
                    text = "Очистить историю",
                    onClick = {
                        if (prompts.isEmpty()) {
                            viewModel.showMessage("История уже пуста")
                        } else {
                            showClearDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.Delete,
                    tint = cs.error,
                    textColor = cs.error
                )
                Text(
                    text = "Промты хранятся локально на устройстве. Приложение не отправляет их в сеть.",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
            }
        }

        item {
            SectionCard(title = "О приложении", icon = Icons.Rounded.Info) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(listOf(cs.primary, cs.tertiary))
                            ),
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
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "PromtLab",
                            style = MaterialTheme.typography.titleMedium,
                            color = cs.onSurface
                        )
                        Text(
                            text = "Версия 1.0.0 · Kotlin + Compose",
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "Конструктор промтов для промт-инженера: роль, задача, контекст, " +
                        "приёмы и мгновенный предпросмотр. Библиотека шаблонов и история " +
                        "удачных формулировок всегда под рукой.",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoPill(text = "Kotlin")
                    InfoPill(text = "Compose", tint = cs.secondary)
                    InfoPill(text = "Material 3", tint = cs.tertiary)
                }
            }
        }

        item {
            Text(
                text = "Сделано с любовью к промт-инжинирингу ✨",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = cs.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun SettingsLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun AccentPicker(
    selected: AccentPalette,
    onSelect: (AccentPalette) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AccentPalette.entries.forEach { palette ->
            val isSelected = palette == selected
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) cs.onSurface else cs.outlineVariant,
                        shape = CircleShape
                    )
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(palette.previewColor)
                    .clickable { onSelect(palette) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = palette.label,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (enabled) cs.onSurface else cs.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.6f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}
