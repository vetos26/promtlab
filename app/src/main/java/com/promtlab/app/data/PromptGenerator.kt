package com.promtlab.app.data

import com.promtlab.app.data.model.GeneratedPrompt
import com.promtlab.app.data.model.PromptForm
import kotlin.math.roundToInt

/**
 * Собирает финальный промт из полей конструктора.
 *
 * Генератор детерминированный: один и тот же [PromptForm] всегда даёт
 * одинаковый текст, поэтому предпросмотр можно обновлять на каждое нажатие.
 */
object PromptGenerator {

    private const val FALLBACK_ROLE = "опытный эксперт в теме запроса"
    private const val FALLBACK_TASK = "Опишите задачу, чтобы получить готовый промт"

    fun build(form: PromptForm): GeneratedPrompt {
        val text = buildString {
            appendLine("# РОЛЬ")
            appendLine(roleLine(form))
            appendLine()

            appendLine("# ЗАДАЧА")
            appendLine(taskLine(form))
            appendLine()

            if (form.context.isNotBlank()) {
                appendLine("# КОНТЕКСТ")
                appendLine(form.context.trim())
                appendLine()
            }

            if (form.audience.isNotBlank()) {
                appendLine("# АУДИТОРИЯ")
                appendLine(
                    "${form.audience.trim()}. Подстрой лексику и глубину под этот уровень " +
                        "подготовки."
                )
                appendLine()
            }

            if (form.constraints.isNotBlank()) {
                appendLine("# ОГРАНИЧЕНИЯ")
                append(form.constraints.trim().prependIndentIfNeeded())
                appendLine()
                appendLine()
            }

            appendLine("# ФОРМАТ ОТВЕТА")
            appendLine("- ${form.format.instruction}")
            appendLine("- Объём: ${form.length.instruction}.")
            form.language.instruction?.let { appendLine("- $it") }
            appendLine()

            if (form.techniques.isNotEmpty()) {
                appendLine("# КАК РАБОТАТЬ")
                form.techniques.forEach { technique ->
                    appendLine("- ${technique.instruction}")
                }
                appendLine()
            }

            appendLine("# КРИТЕРИИ КАЧЕСТВА")
            appendLine("- Ответ конкретный: факты, цифры и примеры вместо общих слов.")
            appendLine("- Никаких выдуманных данных: если не уверен — прямо скажи об этом.")
            appendLine("- Соблюдай заданный формат и объём.")
        }.trim()

        val hints = buildHints(form)
        return GeneratedPrompt(
            text = text,
            score = score(form),
            hints = hints,
            chars = text.length,
            words = text.split(Regex("\\s+")).count { it.isNotBlank() },
            tokens = estimateTokens(text)
        )
    }

    private fun roleLine(form: PromptForm): String {
        val role = form.role.trim().ifBlank { FALLBACK_ROLE }
        val tone = form.tone.instruction
        return "Ты — $role. Тон общения: $tone. Объясняй понятно и не бойся " +
            "уточнять детали, если данных недостаточно."
    }

    private fun taskLine(form: PromptForm): String {
        val task = form.task.trim().ifBlank { FALLBACK_TASK }
        return task
    }

    private fun String.prependIndentIfNeeded(): String =
        if (lines().size > 1) lines().joinToString("\n") { "- ${it.trim()}" } else "- $this"

    /** Грубая оценка токенов: ~3.2 символа на токен для смешанного RU/EN текста. */
    fun estimateTokens(text: String): Int =
        if (text.isBlank()) 0 else (text.length / 3.2).roundToInt().coerceAtLeast(1)

    fun score(form: PromptForm): Int {
        var total = 0
        if (form.role.isNotBlank()) total += 18
        if (form.task.isNotBlank()) total += 30
        if (form.context.isNotBlank()) total += 14
        if (form.audience.isNotBlank()) total += 10
        if (form.constraints.isNotBlank()) total += 10
        if (form.techniques.isNotEmpty()) total += 10
        if (form.format != com.promtlab.app.data.model.OutputFormat.MARKDOWN) total += 4
        if (form.length == com.promtlab.app.data.model.AnswerLength.DETAILED) total += 4
        return total.coerceIn(5, 100)
    }

    fun buildHints(form: PromptForm): List<String> = buildList {
        if (form.task.isBlank()) add("Опишите задачу — это ядро промта")
        if (form.role.isBlank()) add("Добавьте роль: «Ты — senior-копирайтер»")
        if (form.context.isBlank()) add("Добавьте контекст: продукт, данные, ограничения")
        if (form.audience.isBlank()) add("Укажите аудиторию, чтобы модель выбрала глубину")
        if (form.techniques.isEmpty()) add("Включите хотя бы один приём промт-инжиниринга")
        if (form.constraints.isBlank()) add("Опишите, чего делать нельзя — это снижает шум")
    }

    /** Случайные идеи для кнопки «вдохновение» на главном экране. */
    val inspiration: List<PromptForm> = listOf(
        PromptForm(
            role = "senior-копирайтер с 10-летним опытом в B2B-маркетинге",
            task = "Напиши 5 вариантов заголовка для лендинга SaaS-сервиса аналитики",
            context = "Сервис помогает продуктовым командам находить узкие места в воронке за 10 минут.",
            audience = "Продуктовые менеджеры и аналитики в IT-компаниях",
            tone = com.promtlab.app.data.model.Tone.EXPERT,
            format = com.promtlab.app.data.model.OutputFormat.MARKDOWN,
            length = com.promtlab.app.data.model.AnswerLength.MEDIUM,
            constraints = "Без кликбейта и восклицательных знаков. Длина заголовка до 60 символов.",
            techniques = setOf(
                com.promtlab.app.data.model.Technique.FEW_SHOT,
                com.promtlab.app.data.model.Technique.NO_FLUFF
            )
        ),
        PromptForm(
            role = "ведущий Android-разработчик, эксперт по Kotlin и Compose",
            task = "Отревьюи фрагмент кода и предложи рефакторинг с объяснением решений",
            context = "Проект на Compose, MVI, корутины. Код работает, но есть проблемы с читаемостью.",
            audience = "Middle-разработчики, знакомые с Kotlin",
            tone = com.promtlab.app.data.model.Tone.PROFESSIONAL,
            format = com.promtlab.app.data.model.OutputFormat.CODE,
            length = com.promtlab.app.data.model.AnswerLength.DETAILED,
            constraints = "Не меняй публичный API. Сначала перечисли проблемы, затем дай код.",
            techniques = setOf(
                com.promtlab.app.data.model.Technique.STEP_BY_STEP,
                com.promtlab.app.data.model.Technique.SELF_CRITIQUE
            )
        ),
        PromptForm(
            role = "продуктовый аналитик",
            task = "Составь план A/B-теста для новой онбординг-воронки и рассчитай размер выборки",
            context = "Текущая конверсия в первый заказ 12%, трафик 40 000 пользователей в месяц.",
            audience = "Продуктовая команда и стейкхолдеры",
            tone = com.promtlab.app.data.model.Tone.ACADEMIC,
            format = com.promtlab.app.data.model.OutputFormat.TABLE,
            length = com.promtlab.app.data.model.AnswerLength.DETAILED,
            constraints = "Укажи гипотезу, метрики, длительность, риски и способ проверки значимости.",
            techniques = setOf(
                com.promtlab.app.data.model.Technique.CHAIN_OF_THOUGHT,
                com.promtlab.app.data.model.Technique.STRUCTURED
            )
        ),
        PromptForm(
            role = "наставник по публичным выступлениям",
            task = "Помоги переписать мой доклад в структуру «проблема — решение — результат»",
            context = "Выступление на 10 минут для конференции о внутренних инструментах.",
            audience = "Инженеры и тимлиды",
            tone = com.promtlab.app.data.model.Tone.INSPIRING,
            format = com.promtlab.app.data.model.OutputFormat.STEP_BY_STEP,
            length = com.promtlab.app.data.model.AnswerLength.MEDIUM,
            constraints = "Дай план слайдов и тайминг, не более 12 слайдов.",
            techniques = setOf(
                com.promtlab.app.data.model.Technique.STRUCTURED,
                com.promtlab.app.data.model.Technique.ASK_QUESTIONS
            )
        ),
        PromptForm(
            role = "редактор научно-популярного издания",
            task = "Объясни сложную тему простым языком с аналогиями и примерами",
            context = "Тема: как работают векторные базы данных и эмбеддинги.",
            audience = "Люди без технического бэкграунда, но с любопытством",
            tone = com.promtlab.app.data.model.Tone.FRIENDLY,
            format = com.promtlab.app.data.model.OutputFormat.ESSAY,
            length = com.promtlab.app.data.model.AnswerLength.MEDIUM,
            constraints = "Минимум терминов. Каждый термин объясняй в скобках при первом упоминании.",
            techniques = setOf(
                com.promtlab.app.data.model.Technique.FEW_SHOT,
                com.promtlab.app.data.model.Technique.NO_FLUFF
            )
        )
    )

    fun randomForm(): PromptForm = inspiration.random()
}
