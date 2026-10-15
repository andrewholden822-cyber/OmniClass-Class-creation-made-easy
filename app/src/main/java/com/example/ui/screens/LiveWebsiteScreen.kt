package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import java.io.File

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LiveWebsiteScreen(
    htmlContent: String?,
    courseTitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var pageProgress by remember { mutableIntStateOf(100) }

    BackHandler {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onBack()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Website Browser Toolbar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("webview_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to App")
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "https://omniclass.portal/${courseTitle.lowercase().replace(" ", "-")}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { webViewInstance?.reload() },
                        modifier = Modifier.testTag("webview_refresh_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload")
                    }

                    IconButton(
                        onClick = {
                            openInExternalBrowser(context, htmlContent, courseTitle)
                        },
                        modifier = Modifier.testTag("webview_open_external_button")
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in Chrome/Safari")
                    }

                    IconButton(
                        onClick = {
                            shareWebsiteFile(context, htmlContent, courseTitle)
                        },
                        modifier = Modifier.testTag("webview_share_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Website")
                    }
                }
            }
        }

        // Live WebView
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                pageProgress = 100
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                pageProgress = newProgress
                            }
                        }
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            allowFileAccess = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }

                        if (!htmlContent.isNullOrBlank()) {
                            loadDataWithBaseURL("https://omniclass.portal/", htmlContent, "text/html", "UTF-8", null)
                        } else {
                            loadUrl("file:///android_asset/web/index.html")
                        }
                        webViewInstance = this
                    }
                },
                update = { wv ->
                    webViewInstance = wv
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("live_webview_container")
            )

            if (pageProgress < 100) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun openInExternalBrowser(context: Context, htmlContent: String?, courseTitle: String) {
    try {
        val fileName = "omniclass_${courseTitle.lowercase().replace(Regex("[^a-z0-9]"), "_")}.html"
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) exportDir.mkdirs()
        val file = File(exportDir, fileName)
        file.writeText(htmlContent ?: "<html><body><h1>OmniClass Portal</h1></body></html>")

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/html")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Open Course Website in Browser"))
    } catch (_: Exception) {
        // Fallback
    }
}

private fun shareWebsiteFile(context: Context, htmlContent: String?, courseTitle: String) {
    try {
        val fileName = "omniclass_${courseTitle.lowercase().replace(Regex("[^a-z0-9]"), "_")}.html"
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) exportDir.mkdirs()
        val file = File(exportDir, fileName)
        file.writeText(htmlContent ?: "")

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/html"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "$courseTitle - Online Course Website")
            putExtra(Intent.EXTRA_TEXT, "Open this interactive online course website in any web browser (Safari on iOS, Chrome, Mac, Windows):")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Website"))
    } catch (_: Exception) {}
}
