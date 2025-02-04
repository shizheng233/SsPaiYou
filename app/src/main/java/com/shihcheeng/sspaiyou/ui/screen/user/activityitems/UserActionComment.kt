package com.shihcheeng.sspaiyou.ui.screen.user.activityitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    UserActivityItem(
        imageUrl = avatarUrl,
        actionString = actionString,
        modifier = modifier
    ) {
        Column {
            Text(
                text = commentString,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            UserActivityNormalItem(
                title = title,
                banner = banner,
                modifier = Modifier.padding(top = 8.dp),
                onCardClick = onCardClick
            )
        }
    }
}
