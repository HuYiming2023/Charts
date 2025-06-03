package com.example.piechart

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import kotlin.math.*

@Composable
fun Pie3DChartRenderer(
    data: ChartData,
    style: ChartStyle,
    selectedIndex: Int,
    onSectorSelected: (Int) -> Unit,
    depth: Float = 30f,
    modifier: Modifier = Modifier
) {
    var boxSize = IntSize(0, 0)

    Box(
        modifier = modifier
            .onSizeChanged { boxSize = it }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val centerX = boxSize.width / 2
                    val centerY = boxSize.height / 2
                    val dx = offset.x - centerX
                    val dy = offset.y - centerY
                    val distance = sqrt(dx * dx + dy * dy)
                    val radius = min(centerX, centerY).toFloat() * 0.9f
                    if (distance > radius) return@detectTapGestures

                    val angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 360) % 360
                    val adjustedAngle = (angle - 270 + 360) % 360

                    val total = data.entries.sumOf { it.value }.toFloat()
                    var start = 0f
                    data.entries.map { (it.value / total).toFloat() }.forEachIndexed { index, proportion ->
                        val sweep = proportion * 360f
                        val angleInSector = (adjustedAngle - start + 360) % 360
                        if (angleInSector <= sweep) {
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

            val chartDepth = depth.dp.toPx()
            var startAngle = -90f

            val verticalEdges = mutableListOf<Pair<Offset, Offset>>()

            // Depth sides
            for (layer in chartDepth.toInt() downTo 1) {
                var layerStart = startAngle
                proportions.forEachIndexed { index, proportion ->
                    val sweep = proportion * 360f
                    val angleRad = Math.toRadians((layerStart + sweep / 2).toDouble())
                    val isSelected = index == selectedIndex
                    val dx = if (isSelected) cos(angleRad).toFloat() * 10.dp.toPx() else 0f
                    val dy = if (isSelected) sin(angleRad).toFloat() * 10.dp.toPx() else 0f

                    drawArc(
                        color = colors[index].copy(alpha = 0.6f),
                        startAngle = layerStart,
                        sweepAngle = sweep,
                        useCenter = true,
                        topLeft = Offset(center.x - radius + dx, center.y - radius + dy + layer),
                        size = Size(radius * 2, radius * 2)
                    )

                    val angleStartRad = Math.toRadians(layerStart.toDouble())
                    val angleEndRad = Math.toRadians((layerStart + sweep).toDouble())
                    val topStart = Offset(center.x + cos(angleStartRad).toFloat() * radius + dx, center.y + sin(angleStartRad).toFloat() * radius + dy)
                    val bottomStart = topStart.copy(y = topStart.y + chartDepth)
                    val topEnd = Offset(center.x + cos(angleEndRad).toFloat() * radius + dx, center.y + sin(angleEndRad).toFloat() * radius + dy)
                    val bottomEnd = topEnd.copy(y = topEnd.y + chartDepth)

                    verticalEdges.add(topStart to bottomStart)
                    verticalEdges.add(topEnd to bottomEnd)

                    layerStart += sweep
                }
            }

            // Top surface
            startAngle = -90f
            proportions.forEachIndexed { index, proportion ->
                val sweep = proportion * 360f
                val angleRad = Math.toRadians((startAngle + sweep / 2).toDouble())
                val offset = if (index == selectedIndex) 10.dp.toPx() else 0f
                val dx = cos(angleRad).toFloat() * offset
                val dy = sin(angleRad).toFloat() * offset

                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(center.x - radius + dx, center.y - radius + dy),
                    size = Size(radius * 2, radius * 2)
                )

                // White border arc
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(center.x - radius + dx, center.y - radius + dy),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 3f)
                )

                startAngle += sweep
            }

            // Outer ring
            drawArc(
                color = Color.White,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 3f)
            )

            verticalEdges.forEach { (start, end) ->
                drawLine(
                    color = Color.White,
                    start = start,
                    end = end,
                    strokeWidth = 3f
                )
            }

            // Labels
            startAngle = -90f
            val labelPaint = Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 28f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            }

            proportions.forEachIndexed { index, proportion ->
                val sweep = proportion * 360f
                val angleRad = Math.toRadians((startAngle + sweep / 2).toDouble())
                val offset = if (index == selectedIndex) 10.dp.toPx() else 0f
                val labelRadius = radius * 1.15f
                val labelX = center.x + cos(angleRad).toFloat() * labelRadius + cos(angleRad).toFloat() * offset
                val labelY = center.y + sin(angleRad).toFloat() * labelRadius + sin(angleRad).toFloat() * offset - chartDepth / 2

                val value = data.entries[index].value
                val percent = (proportion * 100).roundToInt()
                val category = data.entries[index].category
                val label = "${value.toInt()}${style.unit} (${percent}%) $category"

                drawContext.canvas.nativeCanvas.drawText(label, labelX, labelY, labelPaint)

                startAngle += sweep
            }
        }
    }
}