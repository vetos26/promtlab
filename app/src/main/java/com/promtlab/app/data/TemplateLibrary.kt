package com.promtlab.app.data

import com.promtlab.app.data.model.AnswerLength
import com.promtlab.app.data.model.OutputFormat
import com.promtlab.app.data.model.PromptCategory
import com.promtlab.app.data.model.PromptForm
import com.promtlab.app.data.model.PromptTemplate
import com.promtlab.app.data.model.Technique
import com.promtlab.app.data.model.Tone

/**
 * Встроенная библиотека проверенных промтов.
 * Каждый шаблон — это готовый пресет конструктора.
 */
object TemplateLibrary {

    val templates: List<PromptTemplate> = listOf(
        PromptTemplate(
            id = "copy_landing",
            title = "Лендинг, который продаёт",
            description = "Структура и тексты для посадочной страницы под конкретный продукт.",
            category = PromptCategory.MARKETING,
            preset = PromptForm(
                role = "senior-копирайтер с опытом в B2B и SaaS",
                task = "Напиши текст для лендинга: заголовок, подзаголовок, 3 блока выгод, " +
                    "социальное доказательство и призыв к действию",
                context = "Продукт: [опишите продукт]. Главная ценность: [что получает клиент].",
                audience = "Владельцы малого бизнеса, которые сравнивают 3–5 решений",
                tone = Tone.EXPERT,
                format = OutputFormat.MARKDOWN,
                length = AnswerLength.MEDIUM,
                constraints = "Без штампов вроде «инновационный» и «лидер рынка». До 900 символов.",
                techniques = setOf(Technique.FEW_SHOT, Technique.NO_FLUFF, Technique.STRUCTURED)
            )
        ),
        PromptTemplate(
            id = "code_review",
            title = "Ревью кода",
            description = "Найдёт баги, проблемы читаемости и предложит рефакторинг.",
            category = PromptCategory.CODING,
            preset = PromptForm(
                role = "ведущий инженер и ревьюер с 12-летним опытом",
                task = "Сделай ревью кода: баги, безопасность, читаемость и архитектура. " +
                    "Предложи исправленный вариант",
                context = "Стек: [язык/фреймворк], версия. Код:\n```\n[вставьте код]\n```",
                audience = "Разработчики уровня middle",
                tone = Tone.PROFESSIONAL,
                format = OutputFormat.CODE,
                length = AnswerLength.DETAILED,
                constraints = "Сначала список проблем с приоритетом, затем итоговый код. Без изменения API.",
                techniques = setOf(
                    Technique.STEP_BY_STEP,
                    Technique.SELF_CRITIQUE,
                    Technique.STRUCTURED
                )
            )
        ),
        PromptTemplate(
            id = "bug_hunt",
            title = "Поиск бага",
            description = "Гипотезы причин, минимальный репро-кейс и план исправления.",
            category = PromptCategory.CODING,
            preset = PromptForm(
                role = "инженер по отладке, мастер бинарного поиска причин",
                task = "Найди вероятную причину бага и предложи план исправления",
                context = "Ожидаемое поведение: [так]. Фактическое: [так]. " +
                    "Логи/стектрейс:\n```\n[вставьте]\n```",
                audience = "Разработчик, который может быстро проверить гипотезы",
                tone = Tone.EXPERT,
                format = OutputFormat.STEP_BY_STEP,
                length = AnswerLength.MEDIUM,
                constraints = "Сначала 3 гипотезы по убыванию вероятности. Не предлагай " +
                    "переписать проект.",
                techniques = setOf(Technique.CHAIN_OF_THOUGHT, Technique.ASK_QUESTIONS)
            )
        ),
        PromptTemplate(
            id = "seo_article",
            title = "SEO-статья",
            description = "Статья с планом, ключевыми словами и мета-описанием.",
            category = PromptCategory.MARKETING,
            preset = PromptForm(
                role = "SEO-редактор с опытом вывода статей в топ-10",
                task = "Напиши SEO-статью и мета-описание для поисковой выдачи",
                context = "Тема: [тема]. Ключевые запросы: [список]. Конкуренты уже раскрыли: [что].",
                audience = "Пользователи, которые впервые ищут решение этой задачи",
                tone = Tone.PROFESSIONAL,
                format = OutputFormat.MARKDOWN,
                length = AnswerLength.DETAILED,
                constraints = "Ключ не более 3% плотности. H2/H3, лид до 300 символов, FAQ из 4 вопросов.",
                techniques = setOf(Technique.STRUCTURED, Technique.CITATIONS)
            )
        ),
        PromptTemplate(
            id = "product_spec",
            title = "Продуктовая фича",
            description = "PRD: цель, user story, сценарии, метрики и риски.",
            category = PromptCategory.BUSINESS,
            preset = PromptForm(
                role = "продакт-менеджер, который запускал фичи в B2B SaaS",
                task = "Составь короткое PRD для новой фичи",
                context = "Фича: [описание]. Проблема пользователя: [боль]. " +
                    "Текущие метрики: [данные].",
                audience = "Команда разработки и стейкхолдеры",
                tone = Tone.PROFESSIONAL,
                format = OutputFormat.MARKDOWN,
                length = AnswerLength.MEDIUM,
                constraints = "Укажи non-goals, метрики успеха и риски. Уложись в 1 страницу.",
                techniques = setOf(Technique.STRUCTURED, Technique.ASK_QUESTIONS)
            )
        ),
        PromptTemplate(
            id = "data_insight",
            title = "Анализ данных",
            description = "Гипотезы, проверка значимости и выводы для бизнеса.",
            category = PromptCategory.ANALYTICS,
            preset = PromptForm(
                role = "продуктовый аналитик",
                task = "Проанализируй данные и сформулируй выводы с рекомендациями",
                context = "Данные:\n[вставьте таблицу/цифры]. Период: [даты]. " +
                    "Источник: [система].",
                audience = "Продуктовая команда без глубокой статистической подготовки",
                tone = Tone.ACADEMIC,
                format = OutputFormat.TABLE,
                length = AnswerLength.DETAILED,
                constraints = "Отделяй факты от гипотез. Укажи ограничения выборки.",
                techniques = setOf(Technique.CHAIN_OF_THOUGHT, Technique.CITATIONS)
            )
        ),
        PromptTemplate(
            id = "design_brief",
            title = "Дизайн-бриф",
            description = "Мудборд словами, сетка, типографика и состояние экрана.",
            category = PromptCategory.DESIGN,
            preset = PromptForm(
                role = "арт-директор digital-студии",
                task = "Составь дизайн-бриф и опиши концепцию экрана",
                context = "Продукт: [описание]. Референсы: [ссылки]. " +
                    "Ограничения: [платформа, бренд].",
                audience = "Дизайнеры и разработчики, которые будут реализовывать",
                tone = Tone.INSPIRING,
                format = OutputFormat.MARKDOWN,
                length = AnswerLength.MEDIUM,
                constraints = "Опиши сетку, шрифты, палитру, состояния и мобильную адаптацию.",
                techniques = setOf(Technique.STRUCTURED, Technique.FEW_SHOT)
            )
        ),
        PromptTemplate(
            id = "learning_plan",
            title = "План обучения",
            description = "Персональная программа на N недель с практикой и проверкой.",
            category = PromptCategory.EDUCATION,
            preset = PromptForm(
                role = "наставник и методист",
                task = "Составь план обучения с практическими заданиями и критериями прогресса",
                context = "Тема: [тема]. Стартовый уровень: [уровень]. " +
                    "Сколько часов в неделю: [часы].",
                audience = "Взрослый специалист, который учится после работы",
                tone = Tone.FRIENDLY,
                format = OutputFormat.STEP_BY_STEP,
                length = AnswerLength.DETAILED,
                constraints = "Разбей на недели. Каждая неделя: цель, материалы, практика, чек-лист.",
                techniques = setOf(Technique.STRUCTURED, Technique.ASK_QUESTIONS)
            )
        ),
        PromptTemplate(
            id = "explain_simple",
            title = "Объясни простыми словами",
            description = "Сложная тема через аналогии, примеры и уровни сложности.",
            category = PromptCategory.EDUCATION,
            preset = PromptForm(
                role = "популяризатор науки",
                task = "Объясни тему на трёх уровнях: ребёнку, новичку и эксперту",
                context = "Тема: [тема]. Что уже знает читатель: [минимум].",
                audience = "Любопытный человек без специальной подготовки",
                tone = Tone.FRIENDLY,
                format = OutputFormat.ESSAY,
                length = AnswerLength.MEDIUM,
                constraints = "Одна аналогия на раздел. Термины расшифровывай при первом употреблении.",
                techniques = setOf(Technique.FEW_SHOT, Technique.NO_FLUFF)
            )
        ),
        PromptTemplate(
            id = "socratic",
            title = "Сократовский диалог",
            description = "Не даёт ответ сразу — ведёт к решению вопросами.",
            category = PromptCategory.ROLEPLAY,
            preset = PromptForm(
                role = "сократовский наставник",
                task = "Веди меня к решению проблемы, задавая вопросы по одному",
                context = "Моя проблема: [описание]. Что я уже пробовал: [список].",
                audience = "Я, автор запроса",
                tone = Tone.NEUTRAL,
                format = OutputFormat.BULLETS,
                length = AnswerLength.SHORT,
                constraints = "Один вопрос за сообщение. Не давай готовых ответов до 5-го вопроса.",
                techniques = setOf(Technique.ASK_QUESTIONS, Technique.CHAIN_OF_THOUGHT)
            )
        ),
        PromptTemplate(
            id = "interview_prep",
            title = "Подготовка к интервью",
            description = "Вопросы по уровню, эталонные ответы и пробное собеседование.",
            category = PromptCategory.ROLEPLAY,
            preset = PromptForm(
                role = "технический интервьюер в продуктовой компании",
                task = "Проведи пробное собеседование и дай развёрнутую обратную связь",
                context = "Вакансия: [должность]. Уровень: [junior/middle/senior]. " +
                    "Моё резюме: [кратко].",
                audience = "Кандидат, который готовится к реальному интервью",
                tone = Tone.PROFESSIONAL,
                format = OutputFormat.MARKDOWN,
                length = AnswerLength.DETAILED,
                constraints = "Задавай вопросы по одному. После каждого ответа — оценка по 3 критериям.",
                techniques = setOf(Technique.ASK_QUESTIONS, Technique.SELF_CRITIQUE)
            )
        ),
        PromptTemplate(
            id = "summary",
            title = "Выжимка из текста",
            description = "Краткое резюме, тезисы и нерешённые вопросы.",
            category = PromptCategory.WRITING,
            preset = PromptForm(
                role = "редактор-аналитик",
                task = "Сделай выжимку из текста: 5 тезисов, выводы и открытые вопросы",
                context = "Текст:\n[вставьте текст или ссылку]",
                audience = "Занятый руководитель, у которого 2 минуты",
                tone = Tone.PROFESSIONAL,
                format = OutputFormat.BULLETS,
                length = AnswerLength.SHORT,
                constraints = "Не добавляй факты, которых нет в тексте. Сохрани цифры.",
                techniques = setOf(Technique.NO_FLUFF, Technique.STRUCTURED)
            )
        ),
        PromptTemplate(
            id = "email_pitch",
            title = "Холодное письмо",
            description = "Короткий персонализированный питч с оффером и CTA.",
            category = PromptCategory.MARKETING,
            preset = PromptForm(
                role = "эксперт по outbound-продажам",
                task = "Напиши холодное письмо на 120 слов и 2 follow-up",
                context = "Мой продукт: [описание]. Кому пишу: [компания, роль]. Повод: [триггер].",
                audience = "Занятый руководитель, который получает 50 писем в день",
                tone = Tone.FRIENDLY,
                format = OutputFormat.MARKDOWN,
                length = AnswerLength.SHORT,
                constraints = "Одна мысль на письмо. Без вложений и ссылок в первом касании.",
                techniques = setOf(Technique.FEW_SHOT, Technique.NO_FLUFF)
            )
        ),
        PromptTemplate(
            id = "brainstorm",
            title = "Брейншторм 20 идей",
            description = "Много идей с фильтрацией по потенциалу и сложности.",
            category = PromptCategory.BUSINESS,
            preset = PromptForm(
                role = "фасилитатор брейнштормов",
                task = "Сгенерируй 20 идей и отбери 5 самых перспективных",
                context = "Задача: [описание]. Ресурсы: [команда, бюджет, срок].",
                audience = "Команда, которой нужен план на ближайший месяц",
                tone = Tone.INSPIRING,
                format = OutputFormat.TABLE,
                length = AnswerLength.DETAILED,
                constraints = "Колонки: идея, эффект, сложность, срок. Оценивай по 5-балльной шкале.",
                techniques = setOf(Technique.CHAIN_OF_THOUGHT, Technique.STRUCTURED)
            )
        ),
        PromptTemplate(
            id = "translation",
            title = "Локализация текста",
            description = "Перевод с сохранением смысла, тона и терминологии.",
            category = PromptCategory.WRITING,
            preset = PromptForm(
                role = "профессиональный переводчик и локализатор",
                task = "Переведи текст и адаптируй его под культуру целевого рынка",
                context = "Текст:\n[вставьте]. Направление: [RU → EN].",
                audience = "Носители языка, для которых переводим",
                tone = Tone.NEUTRAL,
                format = OutputFormat.MARKDOWN,
                length = AnswerLength.MEDIUM,
                constraints = "Сохрани форматирование. Спорные места отметь сносками.",
                techniques = setOf(Technique.FEW_SHOT, Technique.SELF_CRITIQUE)
            )
        ),
        PromptTemplate(
            id = "json_api",
            title = "Генератор JSON",
            description = "Структурированный ответ по схеме — без лишних слов.",
            category = PromptCategory.CODING,
            preset = PromptForm(
                role = "инженер по интеграциям",
                task = "Сгенерируй данные строго по JSON-схеме",
                context = "Схема:\n```json\n{\n  \"name\": \"string\",\n  \"items\": []\n}\n```\n" +
                    "Входные данные: [описание].",
                audience = "Сервис, который парсит ответ автоматически",
                tone = Tone.NEUTRAL,
                format = OutputFormat.JSON,
                length = AnswerLength.SHORT,
                constraints = "Только валидный JSON. Без комментариев и текста вокруг.",
                techniques = setOf(Technique.STRUCTURED, Technique.NO_FLUFF)
            )
        )
    )

    fun byId(id: String): PromptTemplate? = templates.firstOrNull { it.id == id }

    fun popular(): List<PromptTemplate> = templates.take(4)
}
