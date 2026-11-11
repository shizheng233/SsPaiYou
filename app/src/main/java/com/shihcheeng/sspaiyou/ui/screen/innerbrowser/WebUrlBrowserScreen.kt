package com.shihcheeng.sspaiyou.ui.screen.innerbrowser

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.icons.ExternalLink
import com.shihcheeng.sspaiyou.utils.icons.SSPaiPack

@Composable
fun WebUrlBrowserScreen(
    url: String,
    onNavUp: () -> Unit
) {
    val activity = checkNotNull(LocalActivity.current)
    val lifecycleOwner = LocalLifecycleOwner.current
    var title by remember(url) { mutableStateOf<String?>(null) }
    var browser by remember { mutableStateOf<AgentBrowserView?>(null) }
    val onBack: () -> Unit = {
        if (browser?.goBack() != true) onNavUp()
    }
    val normalUrlHandler = LocalUriHandler.current
    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title ?: stringResource(R.string.web_page_loading),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                subtitle = {
                    Text(text = stringResource(R.string.inner_b))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(R.string.back_to_up)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            normalUrlHandler.openUri(url)
                        }
                    ) {
                        Icon(
                            imageVector = SSPaiPack.ExternalLink,
                            contentDescription = stringResource(R.string.external_browser_open)
                        )
                    }
                }
            )
        }
    ) { padding ->
        key(url, activity, lifecycleOwner) {
            AndroidView(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                factory = { context ->
                    AgentBrowserView(context, activity, lifecycleOwner.lifecycle, url) {
                        title = it
                    }.also { browser = it }
                },
                onRelease = { view ->
                    view.release()
                    if (browser === view) browser = null
                }
            )
        }
    }
}

/** 每个 AndroidView 实例拥有自己的 AgentWeb 和生命周期观察者。 */
@SuppressLint("SetJavaScriptEnabled")
private class AgentBrowserView(
    context: Context,
    activity: Activity,
    private val lifecycle: Lifecycle,
    url: String,
    onTitleChanged: (String?) -> Unit
) : LinearLayout(context) {
    private var released = false
    private var loading = true
    private val agentWeb = AgentWeb.with(activity)
        .setAgentWebParent(this, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        .useDefaultIndicator()
        // 使用 AgentWeb 的客户端类，并调用 super，保留库的回调代理链。
        .setWebChromeClient(object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String?) {
                super.onReceivedTitle(view, title)
                // 加载完成后仍接收 document.title 的动态更新。
                if (!released && !loading) {
                    onTitleChanged(title?.takeIf { it.isNotBlank() } ?: view.url ?: url)
                }
            }
        })
        .setWebViewClient(object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (!released) {
                    loading = true
                    onTitleChanged(null)
                }
            }

            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                if (!released && url == view.url) {
                    loading = false
                    onTitleChanged(view.title?.takeIf { it.isNotBlank() } ?: url)
                }
            }
        })
        .createAgentWeb()
        .ready()
        .get()

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            agentWeb.webLifeCycle.onResume()
        }

        override fun onPause(owner: LifecycleOwner) {
            agentWeb.webLifeCycle.onPause()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            release()
        }
    }

    init {
        layoutParams = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        // ready() 应用默认设置后、首次加载前再设置页面选项。
        agentWeb.agentWebSettings.webSettings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
            useWideViewPort = true
        }
        lifecycle.addObserver(lifecycleObserver)
        agentWeb.urlLoader.loadUrl(url)
    }

    fun goBack(): Boolean = !released && agentWeb.back()

    fun release() {
        if (released) return
        released = true
        lifecycle.removeObserver(lifecycleObserver)
        agentWeb.webLifeCycle.onDestroy()
    }
}
