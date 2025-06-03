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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import kotlin.math.*

@Composable
fun DonutChartRenderer(
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
                detectTapGestures { offset ->
                    val width = boxSize.width
                    val height = boxSize.height
                    val center = Offset(width / 2f, height / 2f)
                    val dx = offset.x - center.x
                    val dy = offset.y - center.y
                    val distance = sqrt(dx * dx + dy * dy)

                    val outerRadius = min(width, height) / 2.4f
                    val innerRadius = outerRadius * 0.55f

                    if (distance !in innerRadius..outerRadius) return@detectTapGestures

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
            val sizeMin = min(size.width, size.height)
            val outerRadius = sizeMin / 2.4f
            val innerRadius = outerRadius * 0.55f
            val center = Offset(size.width / 2, size.height / 2)
            val total = data.entries.sumOf { it.value }
            val proportions = data.entries.map { (it.value / total).toFloat() }
            val colors = data.entries.map { style.categoryColors[it.category] ?: Color.Gray }

            var startAngle = -90f

            val labelPaint = Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 30f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            }

            proportions.forEachIndexed { index, proportion ->
                val sweepAngle = proportion * 360f
                val angleRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val offsetAmount = if (index == selectedIndex) 10.dp.toPx() else 0f
                val dx = cos(angleRad).toFloat() * offsetAmount
                val dy = sin(angleRad).toFloat() * offsetAmount
                val color = colors[index]

                // Draw path ring sector
                val path = Path().apply {
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            center.x - outerRadius + dx,
                            center.y - outerRadius + dy,
                            center.x + outerRadius + dx,
                            center.y + outerRadius + dy
                        ),
                        startAngleDegrees = startAngle,
                        sweepAngleDegrees = sweepAngle,
                        forceMoveTo = false
                    )
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            center.x - innerRadius + dx,
                            center.y - innerRadius + dy,
                            center.x + innerRadius + dx,
                            center.y + innerRadius + dy
                        ),
                        startAngleDegrees = startAngle + sweepAngle,
                        sweepAngleDegrees = -sweepAngle,
                        forceMoveTo = false
                    )
                    close()
                }

                drawPath(path, color)

                val value = data.entries[index].value
                if (value > 0) {
                    val labelOffset = outerRadius * 1.25f
                    val labelX = center.x + cos(angleRad).toFloat() * labelOffset
                    val labelY = center.y + sin(angleRad).toFloat() * labelOffset

                    val percent = (proportion * 100).roundToInt()
                    val label = "${value.toInt()}${style.unit} (${percent}%) ${data.entries[index].category}"

                    drawContext.canvas.nativeCanvas.drawText(label, labelX, labelY, labelPaint)
                }

                startAngle += sweepAngle
            }
        }
    }
}
