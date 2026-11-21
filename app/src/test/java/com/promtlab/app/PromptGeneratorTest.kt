package com.promtlab.app

import com.promtlab.app.data.PromptGenerator
import com.promtlab.app.data.TemplateLibrary
import com.promtlab.app.data.model.PromptForm
import com.promtlab.app.data.model.Technique
import com.promtlab.app.data.model.Tone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptGeneratorTest {

    @Test
    fun `empty form still produces a valid prompt`() {
        val result = PromptGenerator.build(PromptForm())

        assertTrue(result.text.isNotBlank())
        assertTrue(result.text.contains("# РОЛЬ"))
        assertTrue(result.text.contains("# ЗАДАЧА"))
        assertTrue(result.chars > 0)
        assertTrue(result.tokens > 0)
        assertTrue(result.score in 5..100)
    }

    @Test
    fun `score grows when fields are filled`() {
        val empty = PromptGenerator.build(PromptForm()).score
        val filled = PromptGenerator.build(
            PromptForm(
                role = "senior-копирайтер",
                task = "Напиши 5 заголовков для лендинга",
                context = "SaaS-сервис аналитики для продуктовых команд",
                audience = "Продуктовые менеджеры",
                constraints = "Без кликбейта",
                techniques = setOf(Technique.NO_FLUFF, Technique.STRUCTURED)
            )
        ).score

        assertTrue(filled > empty)
        assertTrue(filled >= 90)
    }

    @Test
    fun `role tone and techniques appear in the prompt`() {
        val result = PromptGenerator.build(
            PromptForm(
                role = "ведущий Android-разработчик",
                task = "Отревьюи код",
                tone = Tone.EXPERT,
                techniques = setOf(Technique.CHAIN_OF_THOUGHT, Technique.NO_FLUFF)
            )
        )

        assertTrue(result.text.contains("ведущий Android-разработчик"))
        assertTrue(result.text.contains(Tone.EXPERT.instruction))
        assertTrue(result.text.contains(Technique.CHAIN_OF_THOUGHT.instruction))
        assertTrue(result.text.contains("# КАК РАБОТАТЬ"))
    }

    @Test
    fun `hints point at missing blocks`() {
        val hints = PromptGenerator.buildHints(PromptForm())
        assertTrue(hints.any { it.contains("задачу", ignoreCase = true) })
        assertTrue(hints.any { it.contains("роль", ignoreCase = true) })
    }

    @Test
    fun `template library is consistent`() {
        assertTrue(TemplateLibrary.templates.size >= 10)
        assertEquals(
            TemplateLibrary.templates.size,
            TemplateLibrary.templates.map { it.id }.distinct().size
        )
        TemplateLibrary.templates.forEach { template ->
            assertTrue(template.title.isNotBlank())
            assertTrue(template.preset.task.isNotBlank())
            assertTrue(template.preset.role.isNotBlank())
        }
    }

    @Test
    fun `generator is deterministic`() {
        val form = PromptForm(role = "аналитик", task = "Построй прогноз")
        assertEquals(
            PromptGenerator.build(form).text,
            PromptGenerator.build(form).text
        )
    }
}
