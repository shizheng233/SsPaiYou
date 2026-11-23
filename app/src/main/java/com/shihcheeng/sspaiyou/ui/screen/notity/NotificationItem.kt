package com.shihcheeng.sspaiyou.ui.screen.notity

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.imageUrlForNullable
import org.jsoup.Jsoup
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NotificationItem(
    icon: String,
    avatar: String,
    nickname: String,
    title: String,
    body: String,
    target: String,
    targetImage: String,
    createdAt: Long,
    isRead: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val containerColor by animateColorAsState(
        targetValue = if (isRead) colors.surfaceContainerLow else colors.surfaceContainer,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "notificationReadState"
    )
    val readState =
        stringResource(if (isRead) R.string.notification_read else R.string.notification_unread)
    val plainTitle = remember(title) { Jsoup.parseBodyFragment(title).text() }
    val plainBody = remember(body) { Jsoup.parseBodyFragment(body).text() }
    val plainTarget = remember(target) { Jsoup.parseBodyFragment(target).text() }
    val nameStyle = SpanStyle(
        color = colors.primary,
        fontWeight = MaterialTheme.typography.titleMedium.fontWeight
    )
    val heading = remember(plainTitle, nickname, nameStyle) {
        buildAnnotatedString {
            append(plainTitle)
            val nameStart = if (nickname.isNotBlank()) plainTitle.indexOf(nickname) else -1
            if (nameStart >= 0) addStyle(nameStyle, nameStart, nameStart + nickname.length)
        }
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .semantics { stateDescription = readState },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = colors.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NotificationIcon(icon = icon, modifier = Modifier.padding(top = 6.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // The header contains only the avatar; the name belongs to the action below.
                Box(Modifier.size(40.dp)) {
                    NotificationAvatar(avatar = avatar, modifier = Modifier.fillMaxSize())
                    if (!isRead) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(8.dp),
                            shape = CircleShape,
                            color = colors.primary,
                            content = {}
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (plainTitle.isNotBlank()) {
                        Text(
                            text = heading,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onSurface
                        )
                    }
                    if (plainBody.isNotBlank()) {
                        Text(
                            text = plainBody,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
                if (plainTarget.isNotBlank()) {
                    NotificationTarget(
                        title = plainTarget,
                        image = targetImage,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (createdAt > 0) {
                    val today = LocalDate.now()
                    val zone = ZoneId.systemDefault()
                    val date = remember(createdAt, today, zone) {
                        val time = Instant.ofEpochSecond(createdAt).atZone(zone)
                        val pattern = when {
                            time.toLocalDate() == today -> "HH:mm"
                            time.year == today.year -> "MM-dd HH:mm"
                            else -> "yyyy-MM-dd HH:mm"
                        }
                        time.format(DateTimeFormatter.ofPattern(pattern))
                    }
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationAvatar(avatar: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.baseline_person_outline_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            AsyncImage(
                model = imageUrlForNullable(avatar.takeIf(String::isNotBlank)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun NotificationIcon(icon: String, modifier: Modifier = Modifier) {
    val value = remember(icon) { Jsoup.parseBodyFragment(icon).text().trim() }
    val isImage =
        value.startsWith("https://") || value.startsWith("http://") || value.startsWith("/") ||
                value.substringBefore('?').endsWith(".png", ignoreCase = true) ||
                value.substringBefore('?').endsWith(".webp", ignoreCase = true)
    Box(
        modifier = modifier
            .sizeIn(minWidth = 24.dp, minHeight = 24.dp)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center
    ) {
        if (isImage) {
            AsyncImage(
                model = when {
                    value.startsWith("//") -> "https:$value"
                    value.startsWith("http://") -> value.replaceFirst("http://", "https://")
                    else -> imageUrlForNullable(value.removePrefix("/"))
                },
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )
        } else if (value.isNotBlank()) {
            Text(text = value, style = MaterialTheme.typography.titleLarge, maxLines = 1)
        }
    }
}

@Composable
private fun NotificationTarget(title: String, image: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = colors.surfaceContainerHighest,
        contentColor = colors.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            if (image.isNotBlank()) {
                AsyncImage(
                    model = imageUrlForNullable(image),
                    contentDescription = null,
                    placeholder = ColorPainter(colors.secondaryContainer),
                    error = ColorPainter(colors.secondaryContainer),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 64.dp, height = 48.dp)
                        .clip(MaterialTheme.shapes.small)
                )
            }
        }
    }
}

@Preview(name = "Light", widthDp = 412)
@Preview(name = "Dark", widthDp = 412, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large text", widthDp = 320, fontScale = 1.5f)
@Composable
private fun NotificationItemPreview() {
    AppTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NotificationItem(
                    icon = "🤩",
                    avatar = "",
                    nickname = "墨明棋妙",
                    title = "<a>墨明棋妙</a> 回应了你的评论",
                    body = "我也喜欢初春，Windows 上除了 Warp 还有啥其他的终端没。",
                    target = "你还需要 Terminal 吗？我为什么选择 Kaku？",
                    targetImage = "preview",
                    createdAt = LocalDate.now().atTime(15, 56).atZone(ZoneId.systemDefault())
                        .toEpochSecond(),
                    isRead = false,
                    onClick = {}
                )
                NotificationItem(
                    icon = "💬",
                    avatar = "",
                    nickname = "少数派",
                    title = "少数派 回复了你的评论",
                    body = "谢谢分享，我也来试试看。",
                    target = "让工具更好地服务于生活",
                    targetImage = "",
                    createdAt = 0,
                    isRead = true,
                    onClick = {}
                )
            }
        }
    }
}
