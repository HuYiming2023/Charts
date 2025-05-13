package com.example.piechart

// Contains the overall chart data including the title, total amount, and all entries
data class ChartData(
    val title: String,                 // Title of the chart
    val totalAmount: Double,            // Total value displayed in the center of the pie chart
    val entries: List<ChartEntry>      // List of category entries
)
