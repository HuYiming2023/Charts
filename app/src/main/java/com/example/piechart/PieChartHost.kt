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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PieChartHost(
    data: ChartData,
    style: ChartStyle,
    chartType: ChartType,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf(-1) }
    var currentType by remember { mutableStateOf(chartType) }
    val chartTypes = ChartType.values()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                readOnly = true,
                value = currentType.name,
                onValueChange = {},
                label = { Text("Chart Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(0.8f)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                chartTypes.forEach { type ->
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

        Text(text = data.title, fontSize = 18.sp, color = Color(0xFF222222))
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (currentType) {
                ChartType.Pie -> PieChartRenderer(
                    data = data,
                    style = style,
                    selectedIndex = selectedIndex,
                    onSectorSelected = { selectedIndex = it },
                    modifier = Modifier.fillMaxSize()
                )

                ChartType.Donut -> DonutChartRenderer(
                    data = data,
                    style = style,
                    selectedIndex = selectedIndex,
                    onSectorSelected = { selectedIndex = it },
                    modifier = Modifier.fillMaxSize()
                )

                ChartType.Pie3D -> Pie3DChartRenderer(
                    data = data,
                    style = style,
                    selectedIndex = selectedIndex,
                    onSectorSelected = { selectedIndex = it },
                    modifier = Modifier.fillMaxSize()
                )

//                ChartType.Pie3DWeb -> {
//                    val context = LocalContext.current
//                    LaunchedEffect(Unit) {
//                        launch3DPieChartViaBrowser(context, data.entries)
//                    }
//                }

                ChartType.Pie3DWeb -> {
                    Text(
                        text = "3D Web chart opened in browser.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total Value: ${String.format(Locale.US, "%.2f", data.totalAmount)}${style.unit}",
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            data.entries.forEachIndexed { index, entry ->
                val color = style.categoryColors[entry.category] ?: Color.Gray
                val percentage = (entry.value / data.totalAmount * 100).toInt()
                val isSelected = index == selectedIndex

                val displayText = "${entry.value.toInt()}${style.unit} ($percentage%) ${entry.category}" +
                        if (isSelected && currentType != ChartType.Pie3DWeb) " – Selected" else ""

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = displayText, fontSize = 14.sp, color = Color(0xFF333333))
                }
            }
        }
    }
}
