package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shihcheeng.sspaiyou.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleCommentPushEditor(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    comment: String,
    onCommentEdit: (String) -> Unit,
    isSending: Boolean = false,
    onSendClick: (message: String) -> Unit
) {

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.send_the_comment),
                    style = MaterialTheme.typography.headlineSmall
                )
                if (isSending) {
                    val sendingDescription = stringResource(R.string.article_comment_sending)
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .semantics { contentDescription = sendingDescription },
                        strokeWidth = 2.dp
                    )
                }
            }
            BasicTextField(
                value = comment,
                onValueChange = onCommentEdit,
                readOnly = isSending,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textStyle = MaterialTheme.typography.bodyLarge
            )
            FilledTonalButton(
                onClick = { onSendClick(comment) },
                modifier = Modifier.align(Alignment.End),
                enabled = !isSending && comment.isNotBlank()
            ) {
                Text(
                    text = stringResource(R.string.send)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ArticleCommentPushEditorPreivew() {
    var string by remember {
        mutableStateOf("aaa")
    }
    Scaffold { padding ->
        Box(modifier = Modifier.padding(padding)) {
            ArticleCommentPushEditor(
                comment = string,
                onDismissRequest = {},
                onCommentEdit = { x -> string = x }
            ) {

            }
        }
    }
}
