package com.shihcheeng.sspaiyou.ui.screen.user.activityitems

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat

@Composable
fun UserActionTopicComment(
    modifier: Modifier = Modifier,
    avatarUrl: String,
    actionString: String,
    commentString: String,
    topicTitle: String,
    replyToString: String = "",
    statusTitle: String = ""
) {
    val comment = remember(commentString) {
        HtmlCompat.fromHtml(commentString, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim()
    }
    val replyTo = remember(replyToString) {
        HtmlCompat.fromHtml(replyToString, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim()
    }
    UserActivityItem(
        modifier = modifier,
        imageUrl = avatarUrl,
        actionString = actionString
    ) {
        if (statusTitle.isNotBlank()) {
            Text(
                text = statusTitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (comment.isNotBlank()) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Text(
                    text = comment,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        if (replyTo.isNotBlank()) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text(
                    text = replyTo,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        if (topicTitle.isNotBlank()) {
            Text(
                text = topicTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
