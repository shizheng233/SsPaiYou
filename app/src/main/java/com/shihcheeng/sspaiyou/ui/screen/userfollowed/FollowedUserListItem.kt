package com.shihcheeng.sspaiyou.ui.screen.userfollowed

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
internal fun FollowedUserListItem(
    user: ColumnUser,
    first: Boolean,
    last: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outer = MaterialTheme.shapes.extraLarge
    val square = CornerSize(0.dp)
    val shape = RoundedCornerShape(
        topStart = if (first) outer.topStart else square,
        topEnd = if (first) outer.topEnd else square,
        bottomStart = if (last) outer.bottomStart else square,
        bottomEnd = if (last) outer.bottomEnd else square
    )
    ListItem(
        onClick = onClick,
        enabled = user.slug.isNotBlank(),
        modifier = modifier.fillMaxWidth(),
        shapes = ListItemDefaults.shapes(
            shape = shape,
            pressedShape = shape,
            focusedShape = shape,
            hoveredShape = shape
        ),
        supportingContent = {
            Text(
                text = user.bio.ifBlank { stringResource(R.string.article_author_no_bio) },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                val placeholder = painterResource(R.drawable.baseline_person_24)
                AsyncImage(
                    model = user.avatar.takeIf { it.isNotBlank() }?.let(::imageUrlFor),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = placeholder,
                    error = placeholder,
                    fallback = placeholder,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Text(
            text = user.nickname.ifBlank { user.slug },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FollowedUserListPreview() {
    AppTheme {
        Surface {
            Column(Modifier.padding(16.dp)) {
                listOf(
                    ColumnUser(slug = "first", nickname = "利兹与青鸟"),
                    ColumnUser(slug = "middle", nickname = "车不能快", bio = "分享键盘与生活中的小乐趣"),
                    ColumnUser(slug = "last", nickname = "SamWanng", bio = "集合啦！")
                ).forEachIndexed { index, user ->
                    FollowedUserListItem(
                        user = user,
                        first = index == 0,
                        last = index == 2,
                        onClick = {},
                        modifier = Modifier.padding(bottom = if (index == 2) 0.dp else 2.dp)
                    )
                }
            }
        }
    }
}
