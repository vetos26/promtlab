package com.promtlab.app.data

import com.promtlab.app.data.model.GeneratedImagePrompt
import com.promtlab.app.data.model.ImagePromptForm
import com.promtlab.app.data.model.ImagePreset
import com.promtlab.app.data.model.ImageStyle
import com.promtlab.app.data.model.ImageTarget
import com.promtlab.app.data.model.ImageLighting
import com.promtlab.app.data.model.CameraShot
import com.promtlab.app.data.model.ColorPalette
import com.promtlab.app.data.model.ImageMood
import com.promtlab.app.data.model.ImageAspect

/**
 * Собирает промт для нейросетей-генераторов изображений.
 * Основной текст — на английском (модели понимают его точнее),
 * плюс отдельные блоки негативного промта и параметров модели.
 */
object ImagePromptGenerator {

    fun build(form: ImagePromptForm): GeneratedImagePrompt {
        val parts = buildList {
            add(form.subject.trim().ifBlank { "a striking hero subject" })
            if (form.details.isNotBlank()) add(form.details.trim())
            add(form.style.descriptor)
            add(form.lighting.descriptor)
            add(form.shot.descriptor)
            add(form.palette.descriptor)
            add(form.mood.descriptor)
            if (form.highQuality) {
                add("ultra detailed, 8k, sharp focus, professional color grading")
            }
        }.filter { it.isNotBlank() }

        val english = parts.joinToString(", ")
        val parameters = buildParameters(form)
        val negative = form.negatives.trim()
        val russian = buildRussianDescription(form)

        val negativeBlock = when {
            negative.isBlank() -> "— не задан"
            !form.target.supportsNegative ->
                "$negative\n\n(учтите: ${form.target.label} не понимает negative prompt — " +
                    "нежелательные детали лучше описать словами в основном промте)"
            else -> negative
        }

        val fullText = buildString {
            appendLine("# PROMPT (EN)")
            appendLine(english)
            appendLine()
            appendLine("# NEGATIVE PROMPT")
            appendLine(negativeBlock)
            appendLine()
            appendLine("# ПАРАМЕТРЫ · ${form.target.label}")
            appendLine(parameters)
            appendLine()
            appendLine("# КАК ИСПОЛЬЗОВАТЬ")
            appendLine("- Скопируйте блок PROMPT в ${form.target.shortLabel}.")
            if (form.target.supportsNegative && negative.isNotBlank()) {
                appendLine("- Блок NEGATIVE PROMPT вставьте в поле negative prompt / negative.")
            }
            appendLine("- Соотношение сторон: ${form.aspect.label} (${form.aspect.ar}).")
            appendLine("- Сгенерируйте 4 варианта и уточняйте детали новыми запросами.")
            appendLine()
            appendLine("# ОПИСАНИЕ")
            append(russian)
        }.trim()

        return GeneratedImagePrompt(
            english = english,
            negative = negative,
            parameters = parameters,
            russian = russian,
            fullText = fullText,
            score = score(form),
            hints = buildHints(form),
            tokens = PromptGenerator.estimateTokens(fullText)
        )
    }

    private fun buildParameters(form: ImagePromptForm): String = when (form.target) {
        ImageTarget.MIDJOURNEY ->
            "${form.target.parameters} --ar ${form.aspect.ar}"
        ImageTarget.DALLE -> "${form.target.parameters} · aspect ${form.aspect.ar}"
        else -> "${form.target.parameters} · ${form.aspect.ar}"
    }

    private fun buildRussianDescription(form: ImagePromptForm): String = buildString {
        append("Сюжет: ")
        append(form.subject.trim().ifBlank { "не задан" })
        if (form.details.isNotBlank()) {
            append(". Детали: ${form.details.trim()}")
        }
        append(". Стиль: ${form.style.label.lowercase()}")
        append(", свет: ${form.lighting.label.lowercase()}")
        append(", кадр: ${form.shot.label.lowercase()}")
        append(", палитра: ${form.palette.label.lowercase()}")
        append(", настроение: ${form.mood.label.lowercase()}")
        append(", формат ${form.aspect.label}.")
    }

    fun score(form: ImagePromptForm): Int {
        var total = 15
        if (form.subject.isNotBlank()) total += 35
        if (form.details.isNotBlank()) total += 15
        total += 10 // стиль выбран всегда
        total += 8 // свет
        total += 7 // кадр
        total += 5 // палитра
        total += 5 // настроение
        return total.coerceIn(10, 100)
    }

    fun buildHints(form: ImagePromptForm): List<String> = buildList {
        if (form.subject.isBlank()) add("Опишите главный объект кадра — без него промт размытый")
        if (form.details.isBlank()) add("Добавьте детали: материалы, окружение, время суток")
        if (form.negatives.isBlank()) add("Заполните negative prompt — уберёт артефакты и текст")
        val words = form.subject.trim().split(" ").count { it.isNotBlank() }
        if (words > 30) add("Сюжет слишком длинный: оставьте 1 главный объект")
        if (form.subject.length + form.details.length > 420) {
            add("Промт длинный: сократите второстепенные детали")
        }
    }

    val presets: List<ImagePreset> = listOf(
        ImagePreset(
            title = "Киберпанк-город",
            form = ImagePromptForm(
                subject = "rainy cyberpunk street market at night, a lone courier with a glowing umbrella",
                details = "holographic signs in Japanese and English, steam from vents, wet asphalt reflections",
                style = ImageStyle.CYBERPUNK,
                lighting = ImageLighting.NEON,
                shot = CameraShot.WIDE,
                palette = ColorPalette.NEON,
                mood = ImageMood.MYSTERIOUS,
                aspect = ImageAspect.CINEMA
            )
        ),
        ImagePreset(
            title = "Портрет в золотой час",
            form = ImagePromptForm(
                subject = "portrait of a young ceramic artist in her studio",
                details = "clay-dusted apron, freckles, wind-blown hair, handcrafted pottery in the background",
                style = ImageStyle.PHOTO,
                lighting = ImageLighting.GOLDEN_HOUR,
                shot = CameraShot.CLOSE_UP,
                palette = ColorPalette.WARM,
                mood = ImageMood.CALM,
                aspect = ImageAspect.PORTRAIT
            )
        ),
        ImagePreset(
            title = "3D-иконка приложения",
            form = ImagePromptForm(
                subject = "3D icon of a glowing chat bubble with a spark",
                details = "floating on a soft gradient background, rounded geometry, subtle drop shadow",
                style = ImageStyle.RENDER_3D,
                lighting = ImageLighting.STUDIO,
                shot = CameraShot.MACRO,
                palette = ColorPalette.PASTEL,
                mood = ImageMood.DREAMY,
                aspect = ImageAspect.SQUARE
            )
        ),
        ImagePreset(
            title = "Акварельный пейзаж",
            form = ImagePromptForm(
                subject = "fishing village on a Norwegian fjord at dawn",
                details = "wooden boats, low clouds, tiny windows glowing warm",
                style = ImageStyle.WATERCOLOR,
                lighting = ImageLighting.OVERCAST,
                shot = CameraShot.WIDE,
                palette = ColorPalette.COOL,
                mood = ImageMood.CALM,
                aspect = ImageAspect.WIDE
            )
        ),
        ImagePreset(
            title = "Постер фестиваля",
            form = ImagePromptForm(
                subject = "retro poster for an electronic music festival",
                details = "bold geometric shapes, halftone texture, space for a headline",
                style = ImageStyle.RETRO,
                lighting = ImageLighting.STUDIO,
                shot = CameraShot.TOP_DOWN,
                palette = ColorPalette.VIBRANT,
                mood = ImageMood.ENERGETIC,
                aspect = ImageAspect.PORTRAIT
            )
        ),
        ImagePreset(
            title = "Продукт для маркетплейса",
            form = ImagePromptForm(
                subject = "matte black wireless headphones on a stone pedestal",
                details = "minimal set design, single soft shadow, premium look",
                style = ImageStyle.MINIMAL,
                lighting = ImageLighting.STUDIO,
                shot = CameraShot.MEDIUM,
                palette = ColorPalette.MONO,
                mood = ImageMood.CALM,
                aspect = ImageAspect.SQUARE
            )
        ),
        ImagePreset(
            title = "Эпический пейзаж",
            form = ImagePromptForm(
                subject = "lone traveler facing a colossal ancient gate in a desert canyon",
                details = "sand drifting in the wind, carved runes, tiny figure for scale",
                style = ImageStyle.DIGITAL_ART,
                lighting = ImageLighting.BACKLIGHT,
                shot = CameraShot.LOW_ANGLE,
                palette = ColorPalette.EARTH,
                mood = ImageMood.EPIC,
                aspect = ImageAspect.CINEMA
            )
        )
    )

    fun randomForm(): ImagePromptForm = presets.random().form
}
