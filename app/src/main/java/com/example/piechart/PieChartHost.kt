package com.example.piechart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun PieChartHost(
    data: ChartData,
    style: ChartStyle,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf(-1) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Box(modifier = Modifier.size(280.dp)) {
            PieChartRenderer(
                data = data,
                style = style,
                selectedIndex = selectedIndex,
                onSectorSelected = { index -> selectedIndex = index },
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = data.title,
            fontSize = 20.sp
        )

        val total = data.entries.sumOf { it.value.toDouble() }.toFloat()

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            data.entries.forEachIndexed { index, entry ->
                val percent = (entry.value / total * 100).roundToInt()
                val color = style.categoryColors[entry.category] ?: Color.Gray
                val selectedMark = if (index == selectedIndex) " ← Selected" else ""

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$percent% ${entry.category}$selectedMark")
                }
            }
        }
    }
}
