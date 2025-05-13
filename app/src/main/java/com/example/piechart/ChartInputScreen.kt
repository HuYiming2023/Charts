package com.example.piechart

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

// Enum for input modes: raw value or percentage
enum class InputMode {
    Value, Percentage
}

// Screen for inputting chart title, categories, and values
@Composable
fun ChartInputScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(TextFieldValue("")) }
    var unit by remember { mutableStateOf(TextFieldValue("€")) }
    var inputMode by remember { mutableStateOf(InputMode.Value) }
    var totalOverride by remember { mutableStateOf(TextFieldValue("")) }

    // Dynamic list of entries for user input
    val entries = remember {
        mutableStateListOf(
            mutableStateOf(Pair(TextFieldValue("Category 1"), TextFieldValue("")))
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title and unit input fields
        OutlinedTextField(title, { title = it }, label = { Text("Chart Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(unit, { unit = it }, label = { Text("Unit (optional)") }, modifier = Modifier.fillMaxWidth())

        // Select value or percentage mode
        Text("Input Type:")
        Row {
            RadioButton(inputMode == InputMode.Value, { inputMode = InputMode.Value })
            Text("Value")
            Spacer(Modifier.width(16.dp))
            RadioButton(inputMode == InputMode.Percentage, { inputMode = InputMode.Percentage })
            Text("Percentage")
        }

        // Optional override for total in percentage mode
        if (inputMode == InputMode.Percentage) {
            OutlinedTextField(
                value = totalOverride,
                onValueChange = { totalOverride = it },
                label = { Text("Total Amount (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Entry fields: category and value/percentage
        entries.forEachIndexed { index, pairState ->
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = pairState.value.first,
                    onValueChange = { pairState.value = pairState.value.copy(first = it) },
                    label = { Text("Category") },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = pairState.value.second,
                    onValueChange = { pairState.value = pairState.value.copy(second = it) },
                    label = { Text(if (inputMode == InputMode.Percentage) "Percentage (%)" else "Value") },
                    placeholder = { Text(if (inputMode == InputMode.Percentage) "e.g. 50" else "e.g. 200") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { if (entries.size > 1) entries.removeAt(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }

        // Button to add new category entry
        Button(onClick = {
            val nextIndex = entries.size + 1
            val defaultName = TextFieldValue("Category $nextIndex")
            entries.add(mutableStateOf(Pair(defaultName, TextFieldValue(""))))
        }) { Text("Add Category") }

        // Generate chart and launch display activity
        Button(
            onClick = {
                val titleText = title.text
                val unitText = unit.text

                // Parse and validate input entries
                val parsedEntries = entries.mapNotNull {
                    val name = it.value.first.text.trim()
                    val valueText = it.value.second.text.trim().replace("%", "")
                    val rawValue = valueText.toDoubleOrNull()
                    if (name.isNotEmpty() && rawValue != null) ChartEntry(name, rawValue) else null
                }

                if (parsedEntries.isEmpty()) {
                    Toast.makeText(context, "Please enter at least one valid entry", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Ensure percentages sum to 100 if in percentage mode
                if (inputMode == InputMode.Percentage) {
                    val total = parsedEntries.sumOf { it.value }
                    if (total != 100.0) {
                        Toast.makeText(context, "Percentages must sum to 100%", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                }

                val finalEntries = when (inputMode) {
                    InputMode.Value -> parsedEntries
                    InputMode.Percentage -> {
                        val total = parsedEntries.sumOf { it.value }
                        parsedEntries.map { ChartEntry(it.category, (it.value / total) * 100.0) }
                    }
                }

                val totalAmount = when (inputMode) {
                    InputMode.Value -> finalEntries.sumOf { it.value }
                    InputMode.Percentage -> totalOverride.text.toDoubleOrNull() ?: 100.0
                }

                // Pass data to ChartDisplayActivity
                val intent = Intent(context, ChartDisplayActivity::class.java).apply {
                    putExtra("chart_title", titleText)
                    putExtra("chart_unit", unitText)
                    putExtra("chart_total", totalAmount)
                    putStringArrayListExtra("entries", ArrayList(finalEntries.map { "${it.category}:${it.value}" }))
                }

                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) { Text("Generate Chart") }
    }
}

