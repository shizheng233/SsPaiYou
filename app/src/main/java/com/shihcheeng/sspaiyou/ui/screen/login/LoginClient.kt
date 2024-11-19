package com.shihcheeng.sspaiyou.ui.screen.login

import android.webkit.CookieManager
import android.webkit.WebView
import com.just.agentweb.WebViewClient

class LoginClient(
    private val needSave: (Map<String, String>) -> Unit
) : WebViewClient() {

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        val manager = CookieManager.getInstance().getCookie(url)
        val temp = manager.split(";")
        val map = mutableMapOf<String, String>()
        temp.forEach {
            val keyValue = it.split("=")
            map[keyValue[0]] = keyValue[1]
        }
        needSave.invoke(map)
    }

    companion object {
        const val TAG_LOGIN_CLIENT = "KEY_LoginClient"
    }

}