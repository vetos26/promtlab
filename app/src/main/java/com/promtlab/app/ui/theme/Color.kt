package com.promtlab.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Акцентные палитры приложения. Пользователь выбирает цвет в настройках,
 * а светлая и тёмная схемы собираются автоматически.
 */
enum class AccentPalette(
    val label: String,
    val primaryLight: Color,
    val primaryDark: Color,
    val secondaryLight: Color,
    val secondaryDark: Color
) {
    VIOLET(
        label = "Фиолетовый",
        primaryLight = Color(0xFF6C4DFF),
        primaryDark = Color(0xFFB4A1FF),
        secondaryLight = Color(0xFF00A99D),
        secondaryDark = Color(0xFF4FE0D0)
    ),
    INDIGO(
        label = "Индиго",
        primaryLight = Color(0xFF3D5AFE),
        primaryDark = Color(0xFF9BAAFF),
        secondaryLight = Color(0xFF7A5AF8),
        secondaryDark = Color(0xFFC3B4FF)
    ),
    OCEAN(
        label = "Океан",
        primaryLight = Color(0xFF0B8FB8),
        primaryDark = Color(0xFF6FD3F2),
        secondaryLight = Color(0xFF2E6BE6),
        secondaryDark = Color(0xFF9CC0FF)
    ),
    EMERALD(
        label = "Изумруд",
        primaryLight = Color(0xFF00875A),
        primaryDark = Color(0xFF5BE0B0),
        secondaryLight = Color(0xFF38A169),
        secondaryDark = Color(0xFF9AE6C4)
    ),
    AMBER(
        label = "Янтарь",
        primaryLight = Color(0xFFB05E00),
        primaryDark = Color(0xFFFFC46B),
        secondaryLight = Color(0xFFC2410C),
        secondaryDark = Color(0xFFFFB59A)
    ),
    ROSE(
        label = "Роза",
        primaryLight = Color(0xFFD6336C),
        primaryDark = Color(0xFFFF9EC0),
        secondaryLight = Color(0xFF9D3FE0),
        secondaryDark = Color(0xFFD4A8FF)
    );

    val previewColor: Color get() = primaryLight
}
