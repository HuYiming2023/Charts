package com.example.piechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import com.example.piechart.ui.theme.PieChartTheme
import androidx.compose.ui.Modifier

// Entry point of the application. Hosts the input screen for chart data.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PieChartTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChartInputScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

