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
import org.json.JSONObject


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AnyChart3DPieView(
    entries: List<ChartEntry>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current


    val dataJson = remember(entries) {
        val data = entries.map { mapOf("x" to it.category, "value" to it.value) }
        Gson().toJson(data)
    }


    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    AndroidView(
        factory = {
            WebView(context).apply {
                setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {

                    override fun onPageFinished(view: WebView?, url: String?) {
                        val script = "setData(${JSONObject.quote(dataJson)});"
                        Log.d("ChartDebug", "WebView loaded, executing script: $script")
                        view?.evaluateJavascript(script, null)
                    }
                }

                loadUrl("file:///android_asset/chart.html")
                webViewRef.value = this
            }
        },
        modifier = modifier
    )
}


