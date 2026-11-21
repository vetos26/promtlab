package com.promtlab.app

import com.promtlab.app.data.ImagePromptGenerator
import com.promtlab.app.data.model.ImageAspect
import com.promtlab.app.data.model.ImagePromptForm
import com.promtlab.app.data.model.ImageStyle
import com.promtlab.app.data.model.ImageTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImagePromptGeneratorTest {

    @Test
    fun `empty form still produces usable prompt`() {
        val result = ImagePromptGenerator.build(ImagePromptForm())

        assertTrue(result.english.isNotBlank())
        assertTrue(result.fullText.contains("# PROMPT (EN)"))
        assertTrue(result.tokens > 0)
    }

    @Test
    fun `prompt contains all selected blocks`() {
        val form = ImagePromptForm(
            subject = "a ceramic artist in her studio",
            details = "clay-dusted apron, golden light",
            style = ImageStyle.PHOTO,
            aspect = ImageAspect.PORTRAIT,
            target = ImageTarget.MIDJOURNEY
        )
        val result = ImagePromptGenerator.build(form)

        assertTrue(result.english.contains("ceramic artist"))
        assertTrue(result.english.contains("photorealistic"))
        assertTrue(result.parameters.contains("--ar 4:5"))
        assertTrue(result.negative.contains("blurry"))
    }

    @Test
    fun `midjourney does not support negative prompt and we warn about it`() {
        val result = ImagePromptGenerator.build(
            ImagePromptForm(target = ImageTarget.MIDJOURNEY)
        )
        assertTrue(result.fullText.contains("не понимает negative prompt"))
    }

    @Test
    fun `stable diffusion keeps negative prompt as is`() {
        val result = ImagePromptGenerator.build(
            ImagePromptForm(target = ImageTarget.SDXL, negatives = "text, logo")
        )
        assertEquals("text, logo", result.negative)
        assertTrue(!result.fullText.contains("не понимает negative prompt"))
    }

    @Test
    fun `score grows when subject and details are filled`() {
        val empty = ImagePromptGenerator.score(ImagePromptForm())
        val filled = ImagePromptGenerator.score(
            ImagePromptForm(subject = "lighthouse at dawn", details = "fog, waves")
        )
        assertTrue("score should grow: $empty -> $filled", filled > empty)
        assertTrue(filled >= 90)
    }

    @Test
    fun `hints point to missing subject`() {
        val hints = ImagePromptGenerator.buildHints(ImagePromptForm())
        assertTrue(hints.any { it.contains("объект", ignoreCase = true) })
    }

    @Test
    fun `every preset builds without errors and has subject`() {
        ImagePromptGenerator.presets.forEach { preset ->
            assertTrue(preset.title.isNotBlank())
            assertTrue("preset ${preset.title}", preset.form.subject.isNotBlank())
            val result = ImagePromptGenerator.build(preset.form)
            assertTrue("preset ${preset.title}", result.english.isNotBlank())
            assertTrue("preset ${preset.title}", result.score >= 90)
        }
    }

    @Test
    fun `generation is deterministic`() {
        val form = ImagePromptForm(subject = "red fox in snow")
        assertEquals(
            ImagePromptGenerator.build(form).fullText,
            ImagePromptGenerator.build(form).fullText
        )
    }
}
