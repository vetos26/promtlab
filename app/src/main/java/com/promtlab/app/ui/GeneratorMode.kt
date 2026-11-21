package com.promtlab.app.ui

/** Режим конструктора: текстовый промт или промт для изображения. */
enum class GeneratorMode(val label: String, val hint: String) {
    TEXT("Текст", "Роль, задача, контекст"),
    IMAGE("Изображение", "Сюжет, стиль, свет")
}
