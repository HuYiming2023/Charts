package com.example.piechart

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import kotlin.math.*

// Responsible for drawing the actual pie chart with interactive tap-to-select functionality
@Composable
fun PieChartRenderer(
    data: ChartData,
    style: ChartStyle,
    selectedIndex: Int,
    onSectorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var boxSize = IntSize(0, 0)

    Box(
        modifier = modifier
            .onSizeChanged { boxSize = it }
            .pointerInput(Unit) {
                // Detect user taps and determine which sector was touched
                detectTapGestures { offset ->
                    val centerX = boxSize.width / 2
                    val centerY = boxSize.height / 2
                    val dx = offset.x - centerX
                    val dy = offset.y - centerY
                    val distance = sqrt(dx * dx + dy * dy)
                    val radius = min(centerX, centerY).toFloat()
                    if (distance > radius) return@detectTapGestures

                    val angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 360) % 360
                    val adjustedAngle = (angle - 270 + 360) % 360

                    val total = data.entries.sumOf { it.value }.toFloat()
                    var start = 0f
                    data.entries.map { (it.value / total).toFloat()  }.forEachIndexed { index, proportion ->
                        val sweep = proportion * 360f
                        if (adjustedAngle >= start && adjustedAngle <= start + sweep) {
                            onSectorSelected(index)
                            return@detectTapGestures
                        }
                        start += sweep
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = min(size.width, size.height) / 2.4f
            val center = Offset(size.width / 2, size.height / 2)
            val total = data.entries.sumOf { it.value }
            val proportions = data.entries.map { (it.value / total).toFloat() }
            val colors = data.entries.map { style.categoryColors[it.category] ?: Color.Gray }
            var startAngle = -90f

            // Configure label text style
            val labelPaint = Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 30f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                typeface = Typeface.SANS_SERIF
            }

            proportions.forEachIndexed { index, proportion ->
                val sweepAngle = proportion * 360f
                val angleRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val offset = if (index == selectedIndex) 12.dp.toPx() else 0f
                val dx = cos(angleRad).toFloat() * offset
                val dy = sin(angleRad).toFloat() * offset
                val color = colors[index]

                // Draw the arc (sector) of the pie
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius + dx, center.y - radius + dy),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )

                // Draw label text outside the pie
                val value = data.entries[index].value
                if (value > 0) {
                    val labelOffset = radius * 1.1f
                    val labelX = center.x + cos(angleRad).toFloat() * labelOffset
                    val labelY = center.y + sin(angleRad).toFloat() * labelOffset

                    val percent = (proportion * 100).roundToInt()
                    val valueLine = "${value.toInt()}${style.unit} ($percent%)"
                    val category = data.entries[index].category

                    val wrappedCategory = wrapText(category)

                    // Draw value + percent
                    drawContext.canvas.nativeCanvas.drawText(
                        valueLine,
                        labelX,
                        labelY,
                        labelPaint
                    )

                    // Draw wrapped category name below the value
                    wrappedCategory.forEachIndexed { i, line ->
                        drawContext.canvas.nativeCanvas.drawText(
                            line,
                            labelX,
                            labelY + (i + 1) * 30f,
                            labelPaint
                        )
                    }
                }

                startAngle += sweepAngle
            }
        }
    }
}

// Splits a long string into multiple lines with a maximum length per line.
// The default maximum line length is 12 characters.
private fun wrapText(text: String, maxLen: Int = 12): List<String> {
    // If text is already short enough, return it as a single line
    if (text.length <= maxLen) return listOf(text)

    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var current = ""

    for (word in words) {
        // If adding the word still keeps us within maxLen, append it
        if ((current + word).length <= maxLen) {
            current += if (current.isEmpty()) word else " $word"
        } else {
            // Otherwise, push the current line and start a new one
            lines.add(current)
            current = word
        }
    }

    // Add any remaining content
    if (current.isNotEmpty()) lines.add(current)

    return lines
}
