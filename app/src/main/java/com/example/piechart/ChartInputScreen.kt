package com.example.piechart

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

enum class InputMode {
    Value, Percentage
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartInputScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(TextFieldValue("")) }
    var unit by remember { mutableStateOf(TextFieldValue("€")) }
    var inputMode by remember { mutableStateOf(InputMode.Value) }
    var totalOverride by remember { mutableStateOf(TextFieldValue("")) }
    var chartType by remember { mutableStateOf(ChartType.Pie) }
    val chartTypes = ChartType.values()
    var expanded by remember { mutableStateOf(false) }

    val entries = remember {
        mutableStateListOf(
            mutableStateOf(Pair(TextFieldValue("Category 1"), TextFieldValue("")))
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Chart Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = unit,
            onValueChange = { unit = it },
            label = { Text("Unit (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Input Type:")
        Row {
            RadioButton(selected = inputMode == InputMode.Value, onClick = { inputMode = InputMode.Value })
            Text("Value")
            Spacer(Modifier.width(16.dp))
            RadioButton(selected = inputMode == InputMode.Percentage, onClick = { inputMode = InputMode.Percentage })
            Text("Percentage")
        }

        if (inputMode == InputMode.Percentage) {
            OutlinedTextField(
                value = totalOverride,
                onValueChange = { totalOverride = it },
                label = { Text("Total Amount (optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        entries.forEachIndexed { index, pairState ->
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = pairState.value.first,
                    onValueChange = { pairState.value = pairState.value.copy(first = it) },
                    label = { Text("Category") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = pairState.value.second,
                    onValueChange = { pairState.value = pairState.value.copy(second = it) },
                    label = { Text(if (inputMode == InputMode.Percentage) "Percentage (%)" else "Value") },
                    placeholder = { Text(if (inputMode == InputMode.Percentage) "e.g. 50" else "e.g. 200") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { if (entries.size > 1) entries.removeAt(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }

        Button(onClick = {
            val nextIndex = entries.size + 1
            entries.add(mutableStateOf(Pair(TextFieldValue("Category $nextIndex"), TextFieldValue(""))))
        }) {
            Text("Add Category")
        }

        val sum = entries.sumOf { it.value.second.text.toDoubleOrNull() ?: 0.0 }
        OutlinedTextField(
            value = TextFieldValue(sum.toString()),
            onValueChange = {},
            enabled = false,
            label = {
                Text(if (inputMode == InputMode.Percentage) "Percentage Total (%)" else "Total Amount")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Chart Type:")
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = chartType.name,
                onValueChange = {},
                label = { Text("Select Chart Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                chartTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            chartType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                Toast.makeText(context, "Generate chart clicked", Toast.LENGTH_SHORT).show()
                Log.d("ChartDebug", "Generate chart clicked")

                val titleText = title.text
                val unitText = unit.text

                val parsedEntries = entries.mapNotNull {
                    val name = it.value.first.text.trim()
                    val valueText = it.value.second.text.trim().replace("%", "")
                    val rawValue = valueText.toDoubleOrNull()
                    if (name.isNotEmpty() && rawValue != null) ChartEntry(name, rawValue) else null
                }

                Log.d("ChartDebug", "Parsed entries = $parsedEntries")


                if (parsedEntries.isEmpty()) {
                    Toast.makeText(context, "Please enter at least one valid entry", Toast.LENGTH_SHORT).show()
                    return@Button
                }

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

                val intent = Intent(context, ChartDisplayActivity::class.java).apply {
                    putExtra("chart_title", titleText)
                    putExtra("chart_unit", unitText)
                    putExtra("chart_total", totalAmount)
                    putExtra("chart_type", chartType.name)
                    putStringArrayListExtra("entries", ArrayList(finalEntries.map { "${it.category}:${it.value}" }))
                }

                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Generate Chart")
        }
    }
}
