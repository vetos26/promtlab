package com.promtlab.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun PromtLabBottomBar(
    currentRoute: String?,
    onSelect: (Dest) -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = cs.surfaceContainerHigh.copy(alpha = 0.98f),
        tonalElevation = 4.dp,
        shadowElevation = 18.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Dest.tabs.forEach { dest ->
                BottomBarItem(
                    dest = dest,
                    selected = currentRoute == dest.route,
                    onClick = { onSelect(dest) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    dest: Dest,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val iconColor by animateColorAsState(
        targetValue = if (selected) cs.onPrimaryContainer else cs.onSurfaceVariant,
        label = "iconColor"
    )
    val pillColor by animateColorAsState(
        targetValue = if (selected) cs.primaryContainer.copy(alpha = 0.9f) else Color.Transparent,
        label = "pillColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.9f,
        label = "scale"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(pillColor)
                .padding(horizontal = 18.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = dest.icon,
                contentDescription = dest.label,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text = dest.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) cs.primary else cs.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
