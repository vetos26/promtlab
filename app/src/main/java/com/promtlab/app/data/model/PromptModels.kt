package com.promtlab.app.data.model

/**
 * Категории промтов — используются и в библиотеке шаблонов, и в истории.
 */
enum class PromptCategory(val label: String, val emoji: String) {
    WRITING("Тексты", "✍️"),
    MARKETING("Маркетинг", "📣"),
    CODING("Код", "💻"),
    BUSINESS("Бизнес", "📊"),
    DESIGN("Дизайн", "🎨"),
    EDUCATION("Обучение", "🎓"),
    ANALYTICS("Аналитика", "📈"),
    ROLEPLAY("Ролевые", "🎭"),
    OTHER("Другое", "✨");

    companion object {
        fun fromId(id: String): PromptCategory = entries.firstOrNull { it.name == id } ?: OTHER
    }
}

/** Тональность ответа модели. */
enum class Tone(val label: String, val instruction: String) {
    PROFESSIONAL("Деловой", "деловой, точный и уважительный"),
    FRIENDLY("Дружелюбный", "дружелюбный и тёплый, обращение на «ты»"),
    EXPERT("Экспертный", "уверенный экспертный, как у senior-специалиста"),
    INSPIRING("Вдохновляющий", "вдохновляющий и энергичный"),
    PLAYFUL("Живой", "лёгкий, живой, с уместной иронией"),
    ACADEMIC("Академический", "академический, нейтральный научный"),
    NEUTRAL("Нейтральный", "нейтральный, без лишних эмоций")
}

/** Формат итогового ответа. */
enum class OutputFormat(val label: String, val instruction: String) {
    MARKDOWN(
        "Markdown",
        "Оформи ответ в Markdown: заголовки, списки и выделение ключевых мыслей."
    ),
    BULLETS("Список", "Дай ответ компактным маркированным списком без длинных абзацев."),
    TABLE(
        "Таблица",
        "Оформи результат как Markdown-таблицу с понятными названиями колонок."
    ),
    JSON("JSON", "Верни строго валидный JSON без пояснений и Markdown-обёрток."),
    STEP_BY_STEP(
        "Инструкция",
        "Оформи ответ как пошаговую инструкцию с нумерацией и краткими пояснениями."
    ),
    ESSAY("Эссе", "Напиши связный текст с абзацами и плавными переходами между мыслями."),
    CODE("Код", "Дай рабочий код с комментариями и коротким примером использования.")
}

/** Желаемая длина ответа. */
enum class AnswerLength(val label: String, val instruction: String) {
    SHORT("Кратко", "не более 150 слов — только самое важное"),
    MEDIUM("Средне", "300–500 слов с достаточным уровнем детализации"),
    DETAILED("Подробно", "от 800 слов: разбор, аргументы и примеры")
}

/** Язык ответа. */
enum class PromptLanguage(val label: String, val instruction: String?) {
    AUTO("Авто", null),
    RU("Русский", "Отвечай на русском языке."),
    EN("English", "Respond in English.")
}

/** Приёмы промт-инжиниринга, которые добавляются в промт. */
enum class Technique(val label: String, val instruction: String) {
    CHAIN_OF_THOUGHT(
        "Chain-of-Thought",
        "Сначала порассуждай шаг за шагом, затем выдай финальный ответ."
    ),
    STEP_BY_STEP("Пошагово", "Разбей решение на последовательные шаги с короткими пояснениями."),
    FEW_SHOT(
        "Few-shot",
        "Перед основным ответом покажи 1–2 коротких примера нужного формата."
    ),
    SELF_CRITIQUE(
        "Самокритика",
        "Проверь черновик ответа на ошибки и слабые места, исправь их и только потом отдай результат."
    ),
    ASK_QUESTIONS(
        "Уточняющие вопросы",
        "Если данных не хватает, задай до 3 уточняющих вопросов, прежде чем отвечать."
    ),
    NO_FLUFF(
        "Без воды",
        "Без вступлений, извинений и общих фраз — только суть по делу."
    ),
    STRUCTURED("Структура", "Используй явные разделы и подзаголовки для навигации по ответу."),
    CITATIONS(
        "Источники",
        "Указывай источники, а предположения явно помечай как гипотезы."
    )
}

/**
 * Состояние конструктора промта.
 */
data class PromptForm(
    val role: String = "",
    val task: String = "",
    val context: String = "",
    val audience: String = "",
    val tone: Tone = Tone.PROFESSIONAL,
    val format: OutputFormat = OutputFormat.MARKDOWN,
    val length: AnswerLength = AnswerLength.MEDIUM,
    val language: PromptLanguage = PromptLanguage.AUTO,
    val constraints: String = "",
    val techniques: Set<Technique> = setOf(Technique.NO_FLUFF, Technique.STRUCTURED)
)

/** Результат генерации: текст промта + метрики качества. */
data class GeneratedPrompt(
    val text: String,
    val score: Int,
    val hints: List<String>,
    val chars: Int,
    val words: Int,
    val tokens: Int
)

/** Сохранённый промт (модель домена). */
data class SavedPrompt(
    val id: Long = 0L,
    val title: String,
    val body: String,
    val category: PromptCategory = PromptCategory.OTHER,
    val tone: String = Tone.PROFESSIONAL.label,
    val format: String = OutputFormat.MARKDOWN.label,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

/** Шаблон из библиотеки. */
data class PromptTemplate(
    val id: String,
    val title: String,
    val description: String,
    val category: PromptCategory,
    val preset: PromptForm
)

/** Готовый набор полей, применяемый к конструктору одним тапом. */
typealias TemplatePreset = PromptForm
