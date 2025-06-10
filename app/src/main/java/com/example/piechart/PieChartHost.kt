package com.example.piechart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PieChartHost(
    data: ChartData,
    style: ChartStyle,
    chartType: ChartType,
    modifier: Modifier = Modifier
) {

    var currentType by remember { mutableStateOf(chartType) }
    var selectedIndex by remember { mutableStateOf(-1) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = data.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))


        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            OutlinedTextField(
                value = currentType.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Chart Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ChartType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            currentType = type
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
           Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (currentType) {
                ChartType.Pie -> PieChartRenderer(
                    data = data, style = style, selectedIndex = selectedIndex,
                    onSectorSelected = { selectedIndex = it },
                    modifier = Modifier.fillMaxSize()
                )
                ChartType.Donut -> DonutChartRenderer(
                    data = data, style = style, selectedIndex = selectedIndex,
                    onSectorSelected = { selectedIndex = it },
                    modifier = Modifier.fillMaxSize()
                )
//                ChartType.Pie3D -> Pie3DChartRenderer(
//                    data = data, style = style, selectedIndex = selectedIndex,
//                    onSectorSelected = { selectedIndex = it },
//
//                    modifier = Modifier.fillMaxSize()
//                )
                ChartType.Pie3DWeb -> AnyChart3DPieView(
                    entries = data.entries,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }



        if (currentType != ChartType.Pie3DWeb) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Total: ${String.format(Locale.US, "%.2f", data.totalAmount)}${style.unit}",
                fontSize = 16.sp, color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.Start
            ) {
                data.entries.forEachIndexed { index, entry ->
                    val color = style.categoryColors[entry.category] ?: Color.Gray
                    val percentage = if (data.totalAmount > 0) (entry.value / data.totalAmount * 100).toInt() else 0
                    val label = "${entry.value.toInt()}${style.unit} ($percentage%) ${entry.category}"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Box(modifier = Modifier.size(12.dp).background(color))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, fontSize = 14.sp, color = Color(0xFF333333))
                    }
                }
            }
        }
    }
}