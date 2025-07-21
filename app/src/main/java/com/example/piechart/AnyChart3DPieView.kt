package com.example.piechart

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AnyChart3DPieView(
    entries: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current


    val dataJson by produceState<String?>(initialValue = null, entries) {
        value = withContext(Dispatchers.Default) {
            val data = entries.map { mapOf("x" to it.category, "value" to it.value) }
            Gson().toJson(data)
        }
    }

    AndroidView(
        factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                loadUrl("file:///android_asset/chart.html")
            }
        },
        update = { webView ->

            dataJson?.let { json ->
                val script = "updateData(${JSONObject.quote(json)});"
                Log.d("ChartDebug", "Data updated, executing script: $script")
                webView.evaluateJavascript(script, null)
            }
        },
        modifier = modifier
    )
}