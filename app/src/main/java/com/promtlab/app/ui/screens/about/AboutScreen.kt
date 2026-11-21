package com.promtlab.app.ui.screens.about

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.components.InfoPill
import com.promtlab.app.ui.components.ScreenHeader
import com.promtlab.app.ui.components.SectionCard
import com.promtlab.app.ui.components.SoftButton
import com.promtlab.app.util.openUrl
import com.promtlab.app.util.shareText

private const val REPO_URL = "https://github.com/vetos26/PromtLab"
private const val VERSION = "1.0.0"

@Composable
fun AboutScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val cs = MaterialTheme.colorScheme

    val features = listOf(
        "✨" to "Генератор текстовых промтов: роль, задача, контекст, тон, формат, приёмы",
        "🖼" to "Промты для изображений: стиль, свет, камера, палитра и параметры моделей",
        "📚" to "16 готовых шаблонов и обучающие материалы по промт-инжинирингу",
        "⭐" to "Избранное и вся коллекция сохранённых промтов с поиском",
        "🎨" to "Material 3, тёмная тема, 6 акцентов и dynamic color"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "О программе",
                subtitle = "PromtLab $VERSION · Kotlin + Jetpack Compose"
            )
        }

        item {
            SectionCard(title = "PromtLab", icon = Icons.Rounded.AutoAwesome) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(listOf(cs.primary, cs.tertiary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "PromtLab",
                            style = MaterialTheme.typography.titleLarge,
                            color = cs.onSurface
                        )
                        Text(
                            text = "Версия $VERSION",
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.onSurfaceVariant
                        )
                    }
                    InfoPill(text = "MIT")
                }
                Text(
                    text = "Рабочее место промт-инженера: конструктор промтов для текста " +
                        "и изображений, библиотека шаблонов, избранное и короткие уроки. " +
                        "Всё считается и хранится локально — промты не уходят в сеть.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant
                )
            }
        }

        item {
            SectionCard(title = "Что умеет", icon = Icons.Rounded.Info) {
                features.forEach { (emoji, text) ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = emoji, fontSize = 15.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodySmall,
                            color = cs.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            SectionCard(title = "Технологии", icon = Icons.Rounded.Code) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoPill(text = "Kotlin 2.0")
                    InfoPill(text = "Compose", tint = cs.secondary)
                    InfoPill(text = "Material 3", tint = cs.tertiary)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoPill(text = "Room", tint = cs.onSurfaceVariant)
                    InfoPill(text = "DataStore", tint = cs.onSurfaceVariant)
                    InfoPill(text = "Navigation", tint = cs.onSurfaceVariant)
                }
                Text(
                    text = "Архитектура: один ViewModel, StateFlow, однонаправленный поток " +
                        "данных, локальное хранилище Room и DataStore.",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
            }
        }

        item {
            SectionCard(title = "Ссылки", icon = Icons.Rounded.Share) {
                SoftButton(
                    text = "Открыть репозиторий на GitHub",
                    onClick = { context.openUrl(REPO_URL) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.Code
                )
                SoftButton(
                    text = "Поделиться приложением",
                    onClick = {
                        context.shareText(
                            "PromtLab — генератор промтов для текста и изображений: $REPO_URL",
                            subject = "PromtLab"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.Share
                )
                Text(
                    text = "Лицензия MIT: используйте, форкайте и адаптируйте под свои процессы.",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
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
