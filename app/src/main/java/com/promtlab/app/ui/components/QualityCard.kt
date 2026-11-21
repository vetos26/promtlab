package com.promtlab.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Живая оценка качества промта: процент, подпись и подсказки,
 * что ещё стоит добавить.
 */
@Composable
fun QualityCard(
    score: Int,
    hints: List<String>,
    modifier: Modifier = Modifier,
    title: String = "Качество промта",
    icon: ImageVector = Icons.Rounded.TrendingUp,
    trailingLabel: String = "live"
) {
    val cs = MaterialTheme.colorScheme
    val scoreColor = when {
        score >= 75 -> cs.primary
        score >= 45 -> cs.tertiary
        else -> cs.error
    }
    val label = when {
        score >= 85 -> "Готов к работе"
        score >= 60 -> "Хороший черновик"
        score >= 35 -> "Есть что усилить"
        else -> "Начните с задачи"
    }

    SectionCard(modifier = modifier, title = title, icon = icon) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$score%",
                style = MaterialTheme.typography.headlineSmall,
                color = scoreColor
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            InfoPill(text = trailingLabel, icon = Icons.Rounded.Bolt)
        }

        ScoreBar(score = score)

        if (hints.isEmpty()) {
            Text(
                text = "Все ключевые блоки заполнены — можно отправлять модели.",
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant
            )
        } else {
            hints.take(3).forEach { hint ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(cs.primary.copy(alpha = 0.65f))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreBar(score: Int) {
    val fraction by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(550),
        label = "scoreFraction"
    )
    val cs = MaterialTheme.colorScheme
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

/** Квадратная кнопка с иконкой для второстепенных действий. */
@Composable
fun CircleActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 54
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(cs.surfaceContainerHigh)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = cs.primary,
            modifier = Modifier.size((size * 0.4f).dp)
        )
    }
}

/** Градиентная круглая кнопка (генерация идеи). */
@Composable
fun GradientIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 48
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(cs.primary, cs.tertiary)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.size((size * 0.48f).dp)
        )
    }
}

