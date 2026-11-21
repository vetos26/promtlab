package com.promtlab.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormat = SimpleDateFormat("d MMM, HH:mm", Locale("ru"))
private val fullDateFormat = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("ru"))

fun Context.copyToClipboard(text: String, label: String = "Промт") {
    val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    manager.setPrimaryClip(ClipData.newPlainText(label, text))
}

fun Context.shareText(text: String, subject: String = "Промт из PromtLab") {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, "Поделиться промтом"))
}

fun formatDate(millis: Long): String = dateFormat.format(Date(millis))

fun formatFullDate(millis: Long): String = fullDateFormat.format(Date(millis))

fun shortSnippet(text: String, max: Int = 160): String {
    val clean = text.replace(Regex("\\s+"), " ").trim()
    return if (clean.length <= max) clean else clean.take(max).trimEnd() + "…"
}

fun titleFromPrompt(text: String): String {
    val firstTask = text.substringAfter("# ЗАДАЧА", "").substringBefore("#").trim()
    val source = firstTask.ifBlank { text }
    val firstLine = source.lineSequence().map { it.trim() }.firstOrNull { it.isNotBlank() }
        ?: "Новый промт"
    return shortSnippet(firstLine, 48)
}
