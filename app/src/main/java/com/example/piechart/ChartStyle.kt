package com.example.piechart

import androidx.compose.ui.graphics.Color

// Defines the visual style for the chart
data class ChartStyle(
    val categoryColors: Map<String, Color>,  // Colors assigned to each category
    val showPercentage: Boolean,              // Whether to display percentage labels
    val unit: String
)
