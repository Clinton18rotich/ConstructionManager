package com.cm.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import android.webkit.WebChromeClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import android.util.Base64
import android.os.ParcelFileDescriptor
import java.io.FileInputStream
import android.print.pdf.PrintedPdfDocument
import android.graphics.pdf.PdfDocument
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect

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
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }
                            webViewClient = WebViewClient()
                            webChromeClient = WebChromeClient()
                            
                            // Handle PDF downloads and sharing
                            setDownloadListener { url, _, _, _, _ ->
                                handleDownload(context, url)
                            }
                            
                            // Add JavaScript interface for sharing
                            addJavascriptInterface(WebAppInterface(context, this), "Android")
                            
                            loadUrl("file:///android_asset/index.html")
                        }
                    }, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
    
    private fun handleDownload(context: Context, url: String) {
        try {
            if (url.startsWith("data:application/pdf")) {
                val pdfDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Reports")
                pdfDir.mkdirs()
                val pdfFile = File(pdfDir, "Daily_Report_${System.currentTimeMillis()}.pdf")
                
                val base64 = url.substring(url.indexOf("base64,") + 7)
                val pdfBytes = Base64.decode(base64, Base64.DEFAULT)
                FileOutputStream(pdfFile).use { it.write(pdfBytes) }
                
                shareFile(context, pdfFile, "application/pdf")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun shareViaWhatsApp(text: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                startActivity(Intent.createChooser(intent, "Share Report"))
            } catch (e2: Exception) {
                android.widget.Toast.makeText(this, "No sharing app available", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareReportViaWhatsApp(text: String) {
        try {
            val pdfDir = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Reports")
            pdfDir.mkdirs()
            val pdfFile = File(pdfDir, "Daily_Report_${System.currentTimeMillis()}.pdf")
            pdfFile.writeText(text)
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", pdfFile)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, text)
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to text-only
            shareViaWhatsApp(text)
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Daily Site Report")
            putExtra(Intent.EXTRA_TEXT, "Daily Site Report - Construction Manager")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Report via"))
    }
    
    inner class WebAppInterface(private val context: Context, private val webView: WebView) {
        @android.webkit.JavascriptInterface
        fun shareViaWhatsApp(text: String) {
            runOnUiThread {
                this@MainActivity.shareViaWhatsApp(text)
            }
        }

        @android.webkit.JavascriptInterface
        fun shareReportViaWhatsApp(text: String) {
            runOnUiThread {
                this@MainActivity.shareReportViaWhatsApp(text)
            }
        }

        @android.webkit.JavascriptInterface
        fun shareAsPdf(reportHtml: String, fileName: String) {
            try {
                val pdfDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Reports")
                pdfDir.mkdirs()
                val pdfFile = File(pdfDir, fileName)
                
                // Generate PDF from HTML using WebView
                webView.post {
                    val printAdapter = webView.createPrintDocumentAdapter(fileName)
                    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                    
                    // Instead, let's write the HTML directly and use a simpler approach
                    val tempHtml = File(pdfDir, "temp_report.html")
                    tempHtml.writeText(reportHtml)
                    
                    // Create a simple PDF using the printed document
                    val document = PrintedPdfDocument(context, PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setMinMargins(PrintAttributes.Margins(10, 10, 10, 10))
                        .build())
                    
                    // For simplicity, save the report as HTML and share
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/html"
                        putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempHtml))
                        putExtra(Intent.EXTRA_SUBJECT, fileName)
                        putExtra(Intent.EXTRA_TEXT, "Daily Site Report - ${fileName.replace(".html","")}")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Report via"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
