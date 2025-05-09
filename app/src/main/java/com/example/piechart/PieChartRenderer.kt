package com.example.piechart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun PieChartRenderer(
    data: ChartData,
    style: ChartStyle,
    selectedIndex: Int,
    onSectorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val total = data.entries.sumOf { it.value.toDouble() }.toFloat()
    val proportions = data.entries.map { it.value / total }
    val colors = data.entries.map { style.categoryColors[it.category] ?: Color.Gray }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val dx = offset.x - centerX
                    val dy = offset.y - centerY
                    val distance = sqrt(dx * dx + dy * dy)
                    val radius = min(size.width, size.height) / 2
                    if (distance > radius) return@detectTapGestures

                    val angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 360) % 360
                    val adjustedAngle = (angle - 270 + 360) % 360

                    var start = 0f
                    proportions.forEachIndexed { index, p ->
                        val sweep = p * 360
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
            val canvasSize = min(size.width, size.height)
            val radius = canvasSize / 2
            val center = Offset(size.width / 2f, size.height / 2f)
            var startAngle = -90f

            proportions.forEachIndexed { index, proportion ->
                val sweepAngle = proportion * 360f
                val angleRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())

                val isSelected = index == selectedIndex
                val offsetDistance = if (isSelected) 10.dp.toPx() else 0f
                val dx = (cos(angleRad) * offsetDistance).toFloat()
                val dy = (sin(angleRad) * offsetDistance).toFloat()

                val color = if (isSelected)
                    colors[index].copy(alpha = 0.5f)
                else
                    colors[index]

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(
                        center.x - radius + dx,
                        center.y - radius + dy
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = radius * 2,
                        height = radius * 2
                    )
                )

                startAngle += sweepAngle
            }
        }
    }
}
