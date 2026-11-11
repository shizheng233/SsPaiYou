package com.shihcheeng.sspaiyou.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.ui.platform.UriHandler
import androidx.core.net.toUri

class SSPaiUrlHandler(
    private val context: Context,
    private val urlInnerOpen: Boolean,
    private val onInnerOpen: (String) -> Unit
) : UriHandler {

    /**
     * 开启 [urlInnerOpen] 时通过 [onInnerOpen] 打开 HTTP(S) 网页，其他情况交给系统处理。
     * 回调在调用线程同步执行；涉及导航或 UI 更新时，应从主线程调用。
     *
     * @throws IllegalArgumentException when given [uri] is invalid and/or can't be handled by the
     *   system
     */
    override fun openUri(uri: String) {
        val parsedUri = uri.toUri().normalizeScheme()
        require(!parsedUri.scheme.isNullOrBlank()) { "Can't open $uri: missing URI scheme." }
        val isWebUrl = parsedUri.scheme == "http" || parsedUri.scheme == "https"
        require(!isWebUrl || !parsedUri.host.isNullOrBlank()) { "Can't open $uri: missing host." }

        if (urlInnerOpen && isWebUrl) {
            onInnerOpen(parsedUri.toString())
            return
        }

        val intent = Intent(Intent.ACTION_VIEW, parsedUri)
        if (!context.hasActivity()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            throw IllegalArgumentException("Can't open $uri.", e)
        }
    }
}

private tailrec fun Context.hasActivity(): Boolean = when (this) {
    is Activity -> true
    is ContextWrapper -> baseContext.hasActivity()
    else -> false
}
