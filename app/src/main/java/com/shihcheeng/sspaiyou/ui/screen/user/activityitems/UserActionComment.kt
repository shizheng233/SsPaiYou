package com.shihcheeng.sspaiyou.ui.screen.user.activityitems

import androidx.compose.foundation.layout.Column
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
fun UserActionComment(
    modifier: Modifier = Modifier,
    avatarUrl: String,
    actionString: String,
    commentString: String,
    title: String,
    banner: String?,
    onCardClick: () -> Unit
) {
    val comment = remember(commentString) {
        HtmlCompat.fromHtml(commentString, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim()
    }
    UserActivityItem(
        imageUrl = avatarUrl,
        actionString = actionString,
        modifier = modifier
    ) {
        Column {
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
            UserActivityNormalItem(
                title = title,
                banner = banner,
                modifier = Modifier.padding(top = 8.dp),
                onCardClick = onCardClick
            )
        }
    }
}
