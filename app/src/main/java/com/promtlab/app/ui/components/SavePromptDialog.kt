package com.promtlab.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promtlab.app.data.model.PromptCategory

/** Общий диалог сохранения промта в историю. */
@Composable
fun SavePromptDialog(
    defaultTitle: String,
    defaultCategory: PromptCategory = PromptCategory.WRITING,
    onDismiss: () -> Unit,
    onConfirm: (String, PromptCategory) -> Unit
) {
    var title by remember { mutableStateOf(defaultTitle) }
    var category by remember { mutableStateOf(defaultCategory) }
    val cs = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(26.dp),
        containerColor = cs.surfaceContainerHigh,
        title = { Text("Сохранить промт") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "КАТЕГОРИЯ",
                    style = MaterialTheme.typography.labelSmall,
                    color = cs.onSurfaceVariant,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                ChoiceChips(
                    options = PromptCategory.entries,
                    selected = category,
                    label = { it.label },
                    onSelect = { category = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title, category) }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
