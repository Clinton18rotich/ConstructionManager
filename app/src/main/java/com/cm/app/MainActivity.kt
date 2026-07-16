package com.cm.app

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import android.webkit.WebChromeClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Color(0xFFFF9F0A),
                background = Color(0xFF0A0A0A),
                surface = Color(0xFF1C1C1E),
            )) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AndroidView(factory = { context ->
                        WebView(context).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                allowFileAccess = true
                                allowContentAccess = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                cacheMode = WebSettings.LOAD_DEFAULT
                                allowUniversalAccessFromFileURLs = true
                                allowFileAccessFromFileURLs = true
                            }
                            webViewClient = WebViewClient()
                            webChromeClient = WebChromeClient()
                            loadUrl("file:///android_asset/index.html")
                        }
                    }, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
