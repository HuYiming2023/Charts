package com.example.piechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.piechart.ui.theme.PieChartTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.graphics.Color

// Activity that displays the pie chart with parsed data from intent
class ChartDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Get data passed from input screen
        val chartTitle = intent.getStringExtra("chart_title") ?: "Chart"
        val chartUnit = intent.getStringExtra("chart_unit") ?: ""
        val chartTotal = intent.getDoubleExtra("chart_total", 100.0)
        val entryStrings = intent.getStringArrayListExtra("entries") ?: arrayListOf()

        // Parse string into list of ChartEntry
        val entries = entryStrings.mapNotNull {
            val parts = it.split(":")
            if (parts.size == 2) {
                val category = parts[0]
                val value = parts[1].toDoubleOrNull()
                if (value != null) ChartEntry(category, value) else null
            } else null
        }

        // Assign unique color to each category
        val availableColors = listOf(
            Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF42A5F5),
            Color(0xFF26A69A), Color(0xFFFFCA28), Color(0xFFFF7043),
            Color(0xFFA1887F), Color(0xFF90A4AE), Color(0xFF66BB6A),
            Color(0xFF7E57C2), Color(0xFFFFB300), Color(0xFF5C6BC0),
            Color(0xFF009688)
        ).shuffled()

        val categoryColors = entries.mapIndexed { index, entry ->
            entry.category to availableColors[index % availableColors.size]
        }.toMap()

        val style = ChartStyle(
            categoryColors = categoryColors,
            unit = chartUnit,
            showPercentage = true
        )

        val chartData = ChartData(
            title = chartTitle,
            totalAmount = chartTotal,
            entries = entries
        )

        // Render pie chart with theme
        setContent {
            PieChartTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.systemBars.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PieChartHost(data = chartData, style = style)
                }
            }
        }
    }
}
