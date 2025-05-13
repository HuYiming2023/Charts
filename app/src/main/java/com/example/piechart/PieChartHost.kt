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
import java.util.Locale

// Hosts the title, chart canvas, legend, and total amount
@Composable
fun PieChartHost(
    data: ChartData,
    style: ChartStyle,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf(-1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Chart title
        Text(text = data.title, fontSize = 20.sp, color = Color(0xFF444444))
        Spacer(modifier = Modifier.height(12.dp))

        // Pie chart rendering
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            PieChartRenderer(
                data = data,
                style = style,
                selectedIndex = selectedIndex,
                onSectorSelected = { selectedIndex = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Total amount display
        Text(
            text = "Total Value: ${String.format(Locale.US, "%.2f", data.totalAmount)}${style.unit}",
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Legend showing categories with colors and values
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            data.entries.forEachIndexed { index, entry ->
                val color = style.categoryColors[entry.category] ?: Color.Gray
                val percentage = (entry.value / data.totalAmount * 100).roundToInt()
                val isSelected = index == selectedIndex

                val displayText = "${entry.category} (${percentage}%) ${entry.value.toInt()}${style.unit}" +
                        if (isSelected) " – Selected" else ""

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.size(12.dp).background(color))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = displayText, fontSize = 14.sp, color = Color(0xFF333333))
                }
            }
        }
    }
}
