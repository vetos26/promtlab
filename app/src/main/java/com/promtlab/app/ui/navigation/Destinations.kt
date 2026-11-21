package com.promtlab.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Dest(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val description: String
) {
    data object Generate : Dest(
        route = "generate",
        label = "Создать промт",
        icon = Icons.Rounded.AutoAwesome,
        description = "Конструктор текстовых промтов"
    )

    /**
     * Тот же экран генератора, но сразу в режиме «Изображение»:
     * генерация промтов для картинок живёт внутри конструктора.
     */
    data object Image : Dest(
        route = "generate",
        label = "Изображения",
        icon = Icons.Rounded.Image,
        description = "Промты для нейросетей-художников"
    )

    data object Templates : Dest(
        route = "templates",
        label = "Шаблоны",
        icon = Icons.Rounded.GridView,
        description = "Готовые промты в один тап"
    )

    data object Favorites : Dest(
        route = "favorites",
        label = "Избранное",
        icon = Icons.Rounded.Star,
        description = "Сохранённые и лучшие промты"
    )

    data object Learn : Dest(
        route = "learn",
        label = "Обучающие материалы",
        icon = Icons.Rounded.School,
        description = "Короткие уроки по промт-инжинирингу"
    )

    data object Settings : Dest(
        route = "settings",
        label = "Настройки",
        icon = Icons.Rounded.Tune,
        description = "Тема, акцент, данные"
    )

    data object About : Dest(
        route = "about",
        label = "О программе",
        icon = Icons.Rounded.Info,
        description = "Версия, стек, лицензия"
    )

    companion object {
        /**
         * Пункты выдвижного меню по порядку.
         *
         * Важно: список ленивый. Если обращаться к синглтонам внутри инициализации
         * класса [Dest], их статические поля ещё null — получается
         * NullPointerException в <clinit> и мгновенное падение при запуске.
         */
        val items: List<Dest>
            get() = listOf(Generate, Image, Templates, Favorites, Learn, Settings, About)

        /** Уникальные маршруты (Generate и Image делят один экран). */
        val routes: List<String>
            get() = items.map { it.route }.distinct()

        const val PROMPT_DETAIL_ROUTE = "prompt/{promptId}"

        fun promptDetail(id: Long): String = "prompt/$id"
    }
}
