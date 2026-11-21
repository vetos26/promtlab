package com.promtlab.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.promtlab.app.Graph
import com.promtlab.app.data.ImagePromptGenerator
import com.promtlab.app.data.PromptGenerator
import com.promtlab.app.data.model.GeneratedImagePrompt
import com.promtlab.app.data.model.GeneratedPrompt
import com.promtlab.app.data.model.ImagePromptForm
import com.promtlab.app.data.model.PromptCategory
import com.promtlab.app.data.model.PromptForm
import com.promtlab.app.data.model.PromptTemplate
import com.promtlab.app.data.model.SavedPrompt
import com.promtlab.app.data.settings.AppSettings
import com.promtlab.app.data.settings.ThemeMode
import com.promtlab.app.ui.theme.AccentPalette
import com.promtlab.app.util.titleFromPrompt
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Единый ViewModel приложения: конструктор, история, настройки.
 * Состояние конструктора живёт на уровне Activity, поэтому не теряется
 * при переключении вкладок нижнего меню.
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Graph.promptRepository(application)
    private val settingsRepository = Graph.settingsRepository(application)

    /* ----------------------------- Конструктор ---------------------------- */

    private val _form = MutableStateFlow(PromptForm())
    val form: StateFlow<PromptForm> = _form.asStateFlow()

    /** Счётчики для меню: сколько сохранено и сколько в избранном. */
    val generated: StateFlow<GeneratedPrompt> = _form
        .map { PromptGenerator.build(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PromptGenerator.build(_form.value)
        )

    fun updateForm(transform: (PromptForm) -> PromptForm) {
        _form.value = transform(_form.value)
    }

    fun applyTemplate(template: PromptTemplate) {
        _generatorMode.value = GeneratorMode.TEXT
        _form.value = template.preset
        showMessage("Шаблон «${template.title}» применён")
    }

    fun randomize() {
        _form.value = PromptGenerator.randomForm()
        showMessage("Идея подставлена — доработайте под себя")
    }

    fun resetForm() {
        _form.value = PromptForm()
        showMessage("Конструктор очищен")
    }

    /* -------------------- Режим: текст / изображение --------------------- */

    private val _generatorMode = MutableStateFlow(GeneratorMode.TEXT)
    val generatorMode: StateFlow<GeneratorMode> = _generatorMode.asStateFlow()

    fun setGeneratorMode(mode: GeneratorMode) {
        if (_generatorMode.value != mode) {
            _generatorMode.value = mode
        }
    }

    /* ---------------------- Генератор изображений ----------------------- */

    private val _imageForm = MutableStateFlow(ImagePromptForm())
    val imageForm: StateFlow<ImagePromptForm> = _imageForm.asStateFlow()

    val generatedImage: StateFlow<GeneratedImagePrompt> = _imageForm
        .map { ImagePromptGenerator.build(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ImagePromptGenerator.build(_imageForm.value)
        )

    fun updateImageForm(transform: (ImagePromptForm) -> ImagePromptForm) {
        _imageForm.value = transform(_imageForm.value)
    }

    fun randomizeImage() {
        _imageForm.value = ImagePromptGenerator.randomForm()
        showMessage("Идея для изображения подставлена")
    }

    fun resetImageForm() {
        _imageForm.value = ImagePromptForm()
        showMessage("Конструктор изображений очищен")
    }

    fun saveImagePrompt(title: String) {
        val generated = generatedImage.value
        val target = _imageForm.value.target
        viewModelScope.launch {
            repository.save(
                SavedPrompt(
                    title = title.trim().ifBlank { titleFromPrompt(generated.english) },
                    body = generated.fullText,
                    category = PromptCategory.DESIGN,
                    tone = "Визуальный",
                    format = target.shortLabel,
                    createdAt = System.currentTimeMillis()
                )
            )
            showMessage("Промт для изображения сохранён")
        }
    }

    /* ------------------------------- История ------------------------------ */

    val savedPrompts: StateFlow<List<SavedPrompt>> = repository.observeAll()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Счётчики для меню: сколько сохранено и сколько в избранном. */
    val counts: StateFlow<Pair<Int, Int>> = savedPrompts
        .map { list -> list.size to list.count { it.isFavorite } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0 to 0)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _favoritesOnly = MutableStateFlow(false)
    val favoritesOnly: StateFlow<Boolean> = _favoritesOnly.asStateFlow()

    val filteredPrompts: StateFlow<List<SavedPrompt>> =
        combine(savedPrompts, _searchQuery, _favoritesOnly) { prompts, query, favorites ->
            val q = query.trim()
            prompts.filter { prompt ->
                val matchesFilter = !favorites || prompt.isFavorite
                val matchesQuery = q.isBlank() ||
                    prompt.title.contains(q, ignoreCase = true) ||
                    prompt.body.contains(q, ignoreCase = true) ||
                    prompt.category.label.contains(q, ignoreCase = true)
                matchesFilter && matchesQuery
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFavoritesOnly(value: Boolean) {
        _favoritesOnly.value = value
    }

    fun saveCurrent(title: String, category: PromptCategory) {
        val body = generated.value.text
        val form = _form.value
        viewModelScope.launch {
            repository.save(
                SavedPrompt(
                    title = title.trim().ifBlank { titleFromPrompt(body) },
                    body = body,
                    category = category,
                    tone = form.tone.label,
                    format = form.format.label,
                    createdAt = System.currentTimeMillis()
                )
            )
            showMessage("Промт сохранён в историю")
        }
    }

    fun toggleFavorite(prompt: SavedPrompt) {
        viewModelScope.launch {
            repository.setFavorite(prompt.id, !prompt.isFavorite)
            showMessage(if (prompt.isFavorite) "Убрано из избранного" else "Добавлено в избранное")
        }
    }

    fun updatePrompt(prompt: SavedPrompt, title: String, body: String) {
        viewModelScope.launch {
            repository.updateContent(
                id = prompt.id,
                title = title.trim().ifBlank { "Без названия" },
                body = body.trim()
            )
            showMessage("Изменения сохранены")
        }
    }

    fun deletePrompt(prompt: SavedPrompt) {
        viewModelScope.launch {
            repository.delete(prompt.id)
            showMessage("Промт удалён")
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clear()
            showMessage("История очищена")
        }
    }

    fun exportAll(): String = savedPrompts.value.joinToString(separator = "\n\n———\n\n") { prompt ->
        "# ${prompt.title}\n\n${prompt.body}"
    }

    /* ------------------------------ Настройки ----------------------------- */

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .catch { emit(AppSettings()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppSettings()
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setAccent(accent: AccentPalette) {
        viewModelScope.launch { settingsRepository.setAccent(accent) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDynamicColor(enabled) }
    }

    fun setShowTokenCounter(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setShowTokenCounter(enabled) }
    }

    fun saveTextPrompt(title: String, category: PromptCategory) {
        val generated = generated.value
        val form = _form.value
        viewModelScope.launch {
            repository.save(
                SavedPrompt(
                    title = title.trim().ifBlank { titleFromPrompt(generated.text) },
                    body = generated.text,
                    category = category,
                    tone = form.tone.label,
                    format = form.format.label,
                    createdAt = System.currentTimeMillis()
                )
            )
            showMessage("Промт сохранён в избранное")
        }
    }

    fun toggleLessonCompleted(id: String) {
        viewModelScope.launch { settingsRepository.toggleLessonCompleted(id) }
    }

    /* ------------------------------- События ------------------------------ */

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 16)
    val messages = _messages.asSharedFlow()

    fun showMessage(message: String) {
        _messages.tryEmit(message)
    }
}
