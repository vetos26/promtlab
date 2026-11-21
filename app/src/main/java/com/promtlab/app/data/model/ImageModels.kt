package com.promtlab.app.data.model

/** Художественные стили для генерации изображений. */
enum class ImageStyle(val label: String, val emoji: String, val descriptor: String) {
    PHOTO(
        "Фотореализм", "📷",
        "photorealistic, 50mm photograph, natural skin texture, true-to-life detail"
    ),
    CINEMATIC(
        "Кинематограф", "🎬",
        "cinematic film still, anamorphic lens, film grain, color graded"
    ),
    ANIME(
        "Аниме", "🌸",
        "anime style, clean lineart, vibrant cel shading, expressive eyes"
    ),
    RENDER_3D(
        "3D-рендер", "🧊",
        "octane render, subsurface scattering, soft studio lighting, glossy materials"
    ),
    DIGITAL_ART(
        "Цифровая живопись", "🖌",
        "digital painting, visible brushstrokes, concept art, dramatic composition"
    ),
    WATERCOLOR(
        "Акварель", "🎨",
        "watercolor painting, soft washes, paper texture, bleeding edges"
    ),
    OIL(
        "Масло", "🖼",
        "oil painting, impasto texture, classical composition, rich varnish tones"
    ),
    PIXEL(
        "Пиксель-арт", "🕹",
        "pixel art, 16-bit, limited palette, crisp sprite outlines"
    ),
    CYBERPUNK(
        "Киберпанк", "🌆",
        "cyberpunk, neon signage, rain-slicked streets, holographic reflections"
    ),
    MINIMAL(
        "Минимализм", "◻️",
        "minimalist composition, flat design, generous negative space, single accent color"
    ),
    RETRO(
        "Ретро-постер", "📰",
        "retro poster, halftone print, limited vintage palette, bold typography space"
    ),
    LOWPOLY(
        "Low-poly", "🔻",
        "low poly 3D, geometric facets, stylized shading, clean topology"
    )
}

/** Освещение сцены. */
enum class ImageLighting(val label: String, val descriptor: String) {
    STUDIO("Студийный", "soft studio lighting, softbox key light, gentle shadows"),
    GOLDEN_HOUR("Золотой час", "golden hour sunlight, warm rim light, long shadows"),
    NEON("Неон", "neon lighting, magenta and cyan glow, reflective surfaces"),
    BACKLIGHT("Контражур", "dramatic backlight, glowing contours, volumetric haze"),
    OVERCAST("Мягкий дневной", "overcast daylight, diffused light, neutral shadows"),
    MOONLIGHT("Лунный свет", "cool moonlight, deep shadows, subtle blue cast"),
    CANDLE("Тёплый", "warm practical light, intimate falloff, soft highlights")
}

/** Кадр и оптика. */
enum class CameraShot(val label: String, val descriptor: String) {
    CLOSE_UP("Крупный план", "close-up, shallow depth of field, 85mm lens"),
    MEDIUM("Средний план", "medium shot, 50mm lens, natural perspective"),
    WIDE("Общий план", "wide establishing shot, 24mm lens, deep focus"),
    TOP_DOWN("Сверху", "top-down flat lay, symmetrical composition"),
    LOW_ANGLE("Нижний ракурс", "low angle shot, heroic perspective, dramatic scale"),
    MACRO("Макро", "macro photography, extreme detail, tiny depth of field"),
    ISOMETRIC("Изометрия", "isometric view, 45-degree angle, orthographic feel")
}

/** Цветовая палитра. */
enum class ColorPalette(val label: String, val descriptor: String) {
    WARM("Тёплая", "warm palette, amber and terracotta tones"),
    COOL("Холодная", "cool palette, teal and steel blue tones"),
    PASTEL("Пастель", "pastel palette, soft muted colors"),
    MONO("Монохром", "monochrome palette, black and white, high contrast"),
    NEON("Неон", "neon palette, saturated magenta, cyan and lime"),
    EARTH("Землистая", "earthy palette, olive, sand and clay"),
    VIBRANT("Яркая", "vibrant palette, bold complementary colors")
}

/** Настроение изображения. */
enum class ImageMood(val label: String, val descriptor: String) {
    CALM("Спокойное", "calm and serene atmosphere"),
    EPIC("Эпичное", "epic atmosphere, strong sense of scale"),
    DARK("Мрачное", "dark moody atmosphere, subtle tension"),
    DREAMY("Мечтательное", "dreamy ethereal atmosphere, soft glow"),
    ENERGETIC("Энергичное", "dynamic atmosphere, motion energy"),
    MYSTERIOUS("Загадочное", "mysterious atmosphere, fog and hidden details")
}

/** Соотношение сторон. */
enum class ImageAspect(val label: String, val ar: String) {
    SQUARE("1:1", "1:1"),
    PORTRAIT("4:5", "4:5"),
    STORY("9:16", "9:16"),
    LANDSCAPE("3:2", "3:2"),
    WIDE("16:9", "16:9"),
    CINEMA("21:9", "21:9")
}

/** Нейросеть, под которую собирается промт. */
enum class ImageTarget(
    val label: String,
    val shortLabel: String,
    val parameters: String,
    val supportsNegative: Boolean
) {
    MIDJOURNEY(
        "Midjourney v6", "Midjourney",
        "--v 6.1 --style raw --q 2", false
    ),
    SDXL(
        "Stable Diffusion XL", "SDXL",
        "Steps: 30 · CFG: 7 · Sampler: DPM++ 2M Karras", true
    ),
    DALLE(
        "DALL·E 3", "DALL·E 3",
        "quality: hd · detail: high", false
    ),
    FLUX(
        "Flux.1", "Flux",
        "guidance: 3.5 · steps: 28", true
    )
}

/** Состояние конструктора изображения. */
data class ImagePromptForm(
    val subject: String = "",
    val details: String = "",
    val style: ImageStyle = ImageStyle.PHOTO,
    val lighting: ImageLighting = ImageLighting.STUDIO,
    val shot: CameraShot = CameraShot.MEDIUM,
    val palette: ColorPalette = ColorPalette.WARM,
    val mood: ImageMood = ImageMood.CALM,
    val aspect: ImageAspect = ImageAspect.LANDSCAPE,
    val target: ImageTarget = ImageTarget.MIDJOURNEY,
    val highQuality: Boolean = true,
    val negatives: String = DEFAULT_NEGATIVES
) {
    companion object {
        const val DEFAULT_NEGATIVES =
            "blurry, low quality, jpeg artifacts, deformed hands, extra fingers, " +
                "watermark, text, oversaturated colors"
    }
}

/** Результат генерации промта для изображения. */
data class GeneratedImagePrompt(
    val english: String,
    val negative: String,
    val parameters: String,
    val russian: String,
    val fullText: String,
    val score: Int,
    val hints: List<String>,
    val tokens: Int
)

/** Готовая идея для генератора изображений. */
data class ImagePreset(
    val title: String,
    val form: ImagePromptForm
)
