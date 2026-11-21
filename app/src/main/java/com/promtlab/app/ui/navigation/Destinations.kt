package com.promtlab.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Dest(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Generate : Dest("generate", "Создать", Icons.Rounded.AutoAwesome)
    data object Templates : Dest("templates", "Шаблоны", Icons.Rounded.GridView)
    data object History : Dest("history", "История", Icons.Rounded.History)
    data object Settings : Dest("settings", "Настройки", Icons.Rounded.Tune)

    companion object {
        val tabs: List<Dest> = listOf(Generate, Templates, History, Settings)
        val tabRoutes: List<String> = tabs.map { it.route }

        const val PROMPT_DETAIL_ROUTE = "prompt/{promptId}"
        fun promptDetail(id: Long): String = "prompt/$id"
    }
}
