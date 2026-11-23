package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.MarkdownComponents
import com.mikepenz.markdown.model.MarkdownAnimations
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownPadding
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.theme.sspaiReaderTypo
import com.shihcheeng.sspaiyou.utils.extra.click
import com.shihcheeng.sspaiyou.utils.imageUrlForNullable

/**
 * 向文章详情页的 [LazyListScope] 中渲染单个少数派扩展正文区块（常见于早报、推荐等官方文章的分段条目）。
 *
 * 会按顺序向列表添加以下内容：
 * 1. 区块标题（使用阅读器二级标题样式）
 * 2. 区块正文（支持 Markdown 文本与图片内容块）
 * 3. 区块内嵌评论预览列表（首尾项保留对应圆角、中间项无缝衔接）
 * 4. 区块底部的水平分割线
 *
 * @param innerCommentShape 内嵌评论卡片组的圆角形状，首项保留顶部圆角、末项保留底部圆角，中间项为直角。
 * @param readerFontScale 阅读器字体缩放比例，用于标题和 Markdown 正文排版。
 * @param detailImageTransform Markdown 内嵌图片转换器。
 * @param markdownColors Markdown 配色配置。
 * @param markdownPadding Markdown 间距配置。
 * @param markdownComponents Markdown 自定义组件配置。
 * @param markdownAnimations Markdown 动画配置。
 * @param indexa 当前扩展区块在列表中的索引，用于生成列表项的唯一 `key`。
 * @param x 当前扩展区块的数据模型，包含标题、正文内容块及内嵌评论预览。
 * @param onImageClick 点击正文图片时的回调，参数为图片地址；为 `null` 时不响应点击。
 * @param onCommentsClick 点击内嵌评论区时的回调，参数为当前区块 ID。
 */
fun LazyListScope.articleExtendsBodyBlock(
    innerCommentShape: CornerBasedShape,
    readerFontScale: Float,
    detailImageTransform: DetailImageTransform,
    markdownColors: MarkdownColors,
    markdownPadding: MarkdownPadding,
    markdownComponents: MarkdownComponents,
    markdownAnimations: MarkdownAnimations,
    indexa: Int,
    x: DetailBodyExtends,
    onImageClick: ((String) -> Unit)? = null,
    onCommentsClick: ((Int) -> Unit)? = null,
) {
    item(
        key = "${x.id}_header_$indexa",
        contentType = DetailScreenKey.CONTENT
    ) {
        Text(
            text = x.title,
            style = sspaiReaderTypo(fontScale = readerFontScale).h2
        )
    }
    itemsIndexed(
        items = x.body,
        key = { index: Int, _: DetailContent ->
            "${x.id}_body_${indexa}_$index"
        },
        contentType = { _: Int, item: DetailContent ->
            when (item) {
                is DetailContent.Image -> DetailScreenKey.IMAGE
                is DetailContent.Markdown -> DetailScreenKey.CONTENT
            }
        },
    ) { _: Int, item: DetailContent ->
        when (item) {
            is DetailContent.Markdown -> Markdown(
                content = item.text,
                imageTransformer = detailImageTransform,
                colors = markdownColors,
                typography = sspaiReaderTypo(fontScale = readerFontScale),
                padding = markdownPadding,
                components = markdownComponents,
                animations = markdownAnimations
            )

            is DetailContent.Image -> DetailContentImage(item, onImageClick)
        }
    }
    item(
        key = "${x.id}_COMMENT",
        contentType = DetailScreenKey.INNER_COMMENT
    ) {
        val canOpenComments = onCommentsClick != null && x.id > 0 &&
                (x.comments.isNotEmpty() || x.commentCount > 0)
        Column(
            modifier = Modifier
                .padding(all = 6.dp)
                .click(
                    enabled = canOpenComments,
                    onClick = { onCommentsClick?.invoke(x.id) },
                ),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            if (canOpenComments && x.comments.isEmpty()) {
                Text(
                    text = stringResource(R.string.article_comments_title, x.commentCount),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(all = 8.dp),
                )
            }
            x.comments.forEachIndexed { xIndex, zItem ->
                Surface(
                    shape = innerCommentShape
                        .copy(
                            bottomStart = if (xIndex == x.comments.lastIndex) {
                                innerCommentShape.bottomStart
                            } else {
                                CornerSize(0.dp)
                            },
                            bottomEnd = if (xIndex == x.comments.lastIndex) {
                                innerCommentShape.bottomEnd
                            } else {
                                CornerSize(0.dp)
                            },
                            topStart = if (xIndex == 0) innerCommentShape.topStart else CornerSize(0.dp),
                            topEnd = if (xIndex == 0) innerCommentShape.topEnd else CornerSize(0.dp)
                        ),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = imageUrlForNullable(zItem.user.avatar),
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .clip(CircleShape)
                                .size(16.dp),
                            contentDescription = zItem.user.nickname,
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = zItem.comment,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
    item(
        key = "${x.id}_INNER_DIVIDER",
        contentType = DetailScreenKey.INNER_DIVIDER
    ) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}
