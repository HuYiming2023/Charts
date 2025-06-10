//package com.example.piechart
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.core.view.WindowCompat
//import com.example.piechart.ui.theme.PieChartTheme
//import com.google.gson.Gson
//import java.io.File
//import kotlinx.coroutines.delay
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//
//
//
//
//class ChartDisplayActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        val chartTitle = intent.getStringExtra("chart_title") ?: "Chart"
//        val chartUnit = intent.getStringExtra("chart_unit") ?: ""
//        val chartTotal = intent.getDoubleExtra("chart_total", 100.0)
//        val chartType = intent.getStringExtra("chart_type")?.let { ChartType.valueOf(it) } ?: ChartType.Pie
//        val entryStrings = intent.getStringArrayListExtra("entries") ?: arrayListOf()
//
//        val entries = entryStrings.mapNotNull {
//            val parts = it.split(":")
//            if (parts.size == 2) {
//                val category = parts[0]
//                val value = parts[1].toDoubleOrNull()
//                if (value != null) ChartEntry(category, value) else null
//            } else null
//        }
//
//        val availableColors = listOf(
//            Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF42A5F5),
//            Color(0xFF26A69A), Color(0xFFFFCA28), Color(0xFFFF7043),
//            Color(0xFFA1887F), Color(0xFF90A4AE), Color(0xFF66BB6A),
//            Color(0xFF7E57C2), Color(0xFFFFB300), Color(0xFF5C6BC0),
//            Color(0xFF009688)
//        ).shuffled()
//
//        val categoryColors = entries.mapIndexed { index, entry ->
//            entry.category to availableColors[index % availableColors.size]
//        }.toMap()
//
//        val style = ChartStyle(
//            categoryColors = categoryColors,
//            unit = chartUnit,
//            showPercentage = true
//        )
//
//        val chartData = ChartData(
//            title = chartTitle,
//            totalAmount = chartTotal,
//            entries = entries
//        )
//
//
//        setContent {
//            PieChartTheme {
//
//                if (chartType == ChartType.Pie3DWeb) {
//                    var launched by remember { mutableStateOf(false) }
//
//                    LaunchedEffect(Unit) {
//                        if (!launched) {
//                            launched = true
//                            launch3DPieChartViaBrowser(this@ChartDisplayActivity, entries)
//                            delay(1500)
//                            finish()
//                        }
//                    }
//
//
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        androidx.compose.material3.Text(
//                            text = "3D Web chart opened in browser.",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = Color.Gray
//                        )
//                    }
//
//                } else {
//
//                    PieChartHost(
//                        data = chartData,
//                        style = style,
//                        chartType = chartType,
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//fun launch3DPieChartViaBrowser(context: Context, entries: List<ChartEntry>) {
//
////    Log.d("ChartDebug", "Launching HTML at: ${file.absolutePath}")
//    val dataJson = Gson().toJson(entries.map { mapOf("x" to it.category, "value" to it.value) })
//
//    val html = """
//        <!DOCTYPE html>
//        <html>
//        <head>
//            <meta charset="UTF-8">
//            <title>3D Pie Chart</title>
//            <script src="https://cdn.anychart.com/releases/v8/js/anychart-bundle.min.js"></script>
//            <style>
//                html, body { margin: 0; padding: 0; background: white; }
//                #container { width: 100%; height: 100vh; }
//            </style>
//        </head>
//        <body>
//        <div id="container"></div>
//        <script>
//            var data = $dataJson;
//
//            var chart = anychart.pie3d(data);
//            chart.innerRadius("0%");
//            chart.startAngle(0);
//            chart.labels()
//                .position("outside")
//                .format("{%x}: {%value} ({%percentValue}%)")
//                .fontSize(14)
//                .fontColor("#333");
//            chart.legend(true);
//            chart.height("100%");
//            chart.container("container");
//            chart.draw();
//        </script>
//        </body>
//        </html>
//    """.trimIndent()
//
//    android.util.Log.d("HTML_DEBUG", "Generated HTML:\n$html")
//    android.util.Log.d("HTML_DEBUG", "File saved at: ${context.cacheDir.absolutePath}/chart3d.html")
//
//    val file = File(context.cacheDir, "chart3d.html")
//    file.writeText(html)
//
//    val intent = Intent(Intent.ACTION_VIEW).apply {
//        setDataAndType(Uri.fromFile(file), "text/html")
//        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//    }
//
//    context.startActivity(Intent.createChooser(intent, "Open 3D Pie Chart"))
//}
//
package com.example.piechart

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.piechart.ui.theme.PieChartTheme
import com.google.gson.Gson
import java.io.File
import java.util.*
import androidx.core.content.FileProvider
import android.widget.Toast

class ChartDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val chartTitle = intent.getStringExtra("chart_title") ?: "Chart"
        val chartUnit = intent.getStringExtra("chart_unit") ?: ""
        val chartTotal = intent.getDoubleExtra("chart_total", 100.0)
        val chartType = intent.getStringExtra("chart_type")?.let { ChartType.valueOf(it) } ?: ChartType.Pie
        val entryStrings = intent.getStringArrayListExtra("entries") ?: arrayListOf()

        val entries = entryStrings.mapNotNull {
            val parts = it.split(":")
            if (parts.size == 2) {
                val category = parts[0]
                val value = parts[1].toDoubleOrNull()
                if (value != null) ChartEntry(category, value) else null
            } else null
        }

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

        setContent {
            PieChartTheme {
                if (chartType == ChartType.Pie3DWeb) {
                    var launched by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        if (!launched) {
                            launched = true
                            launch3DPieChartViaBrowser(this@ChartDisplayActivity, entries)
                            // Optionally close the activity after launch
                            // delay(1500)
                            finish()
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Opening 3D Chart in browser...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    PieChartHost(
                        data = chartData,
                        style = style,
                        chartType = chartType,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

fun launch3DPieChartViaBrowser(context: Context, entries: List<ChartEntry>) {
    val dataJson = Gson().toJson(entries.map { mapOf("x" to it.category, "value" to it.value) })

    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>3D Pie Chart</title>
            <script src="https://cdn.anychart.com/releases/v8/js/anychart-bundle.min.js"></script>
            <style>
                html, body { margin: 0; padding: 0; background: white; }
                #container { width: 100%; height: 100vh; }
            </style>
        </head>
        <body>
        <div id="container"></div>
        <script>
        anychart.onDocumentReady(function() {
            var data = $dataJson;

            var chart = anychart.pie3d(data);
            chart.innerRadius("0%");
            chart.startAngle(0);
            chart.labels()
                .position("outside")
                .format("{%x}: {%value} ({%percentValue}%)")
                .fontSize(14)
                .fontColor("#333");
            chart.legend(true);
            chart.height("100%");
            chart.container("container");
            chart.draw();
            });
        </script>
        </body>
        </html>
    """.trimIndent()
    try {

    val file = File(context.cacheDir, "chart3d.html")
    file.writeText(html)
        val authority = "${context.packageName}.provider"
        val fileUri = FileProvider.getUriForFile(context, authority, file)

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(fileUri, "text/html")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }


        if (intent.resolveActivity(context.packageManager) != null) {
            val chooserIntent = Intent.createChooser(intent, "Open 3D Pie Chart")
            context.startActivity(chooserIntent)

        } else {
            Toast.makeText(context, "No application found to open HTML files.", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error launching chart: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

