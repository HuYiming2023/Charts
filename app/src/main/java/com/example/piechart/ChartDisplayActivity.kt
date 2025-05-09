package com.example.piechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class ChartDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sampleData = ChartData(
            title = "Monthly Expenses",
            totalAmount = 348.60f,
            entries = listOf(
                ChartEntry("Polttoaineet", 181.27f),
                ChartEntry("Ruoka", 94.12f),
                ChartEntry("Kosmetiikka ja hygienia", 48.80f),
                ChartEntry("Koti", 17.43f),
                ChartEntry("Muut", 6.98f)
            )
        )

        val sampleStyle = ChartStyle(
            categoryColors = mapOf(
                "Polttoaineet" to Color(0xFF1B5E20),
                "Ruoka" to Color(0xFF388E3C),
                "Kosmetiikka ja hygienia" to Color(0xFF66BB6A),
                "Koti" to Color(0xFFFFF59D),
                "Muut" to Color(0xFFBDBDBD)
            ),
            showPercentage = true
        )

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                PieChartHost(
                    data = sampleData,
                    style = sampleStyle,
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(16.dp)
                )
            }
        }
    }
}
