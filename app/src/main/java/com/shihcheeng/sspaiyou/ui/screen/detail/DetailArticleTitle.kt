package com.shihcheeng.sspaiyou.ui.screen.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shihcheeng.sspaiyou.ui.theme.AppTheme

@Composable
fun DetailArticleTitle(
    modifier: Modifier = Modifier,
    title: String,
    publishedDate: String = "",
    tags: List<String> = emptyList(),
    onTagClick: (String) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes
    val visibleTags = remember(tags) {
        tags.map(String::trim).filter(String::isNotBlank).distinct()
    }
    val accentedTitle = remember(title, colors.primary) {
        buildAnnotatedString {
            append(title)
            title.forEachIndexed { index, character ->
                if (character == '，' || character == ',' || character == '、') {
                    addStyle(SpanStyle(color = colors.primary), index, index + 1)
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (visibleTags.isNotEmpty()) {
            Surface(
                color = colors.surfaceContainerLow,
                contentColor = colors.onSurfaceVariant,
                shape = shapes.large.copy(
                    bottomStart = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
            ) {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    visibleTags.forEach { tag ->
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        color = colors.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("# ")
                                }
                                append(tag)
                            },
                            modifier = Modifier.clickable { onTagClick(tag) },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
        Surface(
            color = colors.surfaceContainerLow,
            contentColor = colors.onSurface,
            shape = shapes.large.copy(
                topStart = if (visibleTags.isEmpty()) shapes.large.topStart else CornerSize(0.dp)
            )
        ) {
            Text(
                text = accentedTitle,
                modifier = Modifier
                    .padding(16.dp)
                    .semantics { heading() },
                style = MaterialTheme.typography.headlineLargeEmphasized.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    fontWeight = FontWeight.Bold
                )
            )
        }
        if (publishedDate.isNotBlank()) {
            Surface(
                modifier = Modifier.padding(top = 6.dp),
                color = colors.inverseSurface,
                contentColor = colors.inverseOnSurface,
                shape = shapes.small
            ) {
                Text(
                    text = publishedDate,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true, widthDp = 412)
@Preview(
    name = "Dark",
    showBackground = true,
    widthDp = 412,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(name = "Large font", showBackground = true, widthDp = 320, fontScale = 1.5f)
@Composable
fun DetailArticleTitlePreview() {
    AppTheme(dynamicColor = false) {
        Surface {
            DetailArticleTitle(
                modifier = Modifier.padding(16.dp),
                title = "今年双十一，这些鼠标值得一看",
                publishedDate = "06月16日",
                tags = listOf("购物", "鼠标")
            )
        }
    }
}

@Preview(name = "No tags", showBackground = true, widthDp = 360)
@Composable
private fun DetailArticleTitleWithoutTagsPreview() {
    AppTheme(dynamicColor = false) {
        Surface {
            DetailArticleTitle(
                modifier = Modifier.padding(16.dp),
                title = "社区速递 068 | 你没见过的社区文章、一周最热评、派友的控制中心",
                publishedDate = "06月16日"
            )
        }
    }
}
