package com.shihcheeng.sspaiyou.ui.screen.login

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.just.agentweb.AgentWeb
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.SSPaiUrl

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebLoginScreen(
    viewModel: WebLoginViewModel = hiltViewModel(),
    onNavUp: () -> Unit,
) {

    val activity = LocalView.current
    val saveAccomplish by viewModel.saveAccomplish.collectAsStateWithLifecycle()

    LaunchedEffect(saveAccomplish){
        if (saveAccomplish) {
            onNavUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.login_summary))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(R.string.back_to_up)
                        )
                    }
                }
            )
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            factory = {
                LinearLayout(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = {
                AgentWeb.with(activity.context as Activity)
                    .setAgentWebParent(
                        it,
                        LinearLayout.LayoutParams(-1, -1)
                    )
                    .useDefaultIndicator()
                    .setWebViewClient(
                        LoginClient { map ->
                            map.forEach { (key, value) ->
                                if (key.contains(Constants.SSPAI_AWT_JSON)) {
                                    viewModel.saveToken(value)
                                }
                            }
                        }
                    )
                    .createAgentWeb()
                    .ready()
                    .go(SSPaiUrl.LOGIN_URL).apply {
                        agentWebSettings.webSettings.javaScriptEnabled = true
                        agentWebSettings.webSettings.builtInZoomControls = true
                        agentWebSettings.webSettings.displayZoomControls = false
                        agentWebSettings.webSettings.useWideViewPort = true
                    }
            }
        )
    }
}