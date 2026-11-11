package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.model.markdownAnimations
import com.mikepenz.markdown.model.markdownPadding
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleAuthor
import com.shihcheeng.sspaiyou.ui.compoents.ReaderImage
import com.shihcheeng.sspaiyou.ui.compoents.ReaderMarkdownImage
import com.shihcheeng.sspaiyou.ui.theme.sspaiReaderTypo
import com.shihcheeng.sspaiyou.ui.theme.toMarkDownColors
import com.shihcheeng.sspaiyou.utils.LocalComposeSetting
import com.shihcheeng.sspaiyou.utils.SSPaiUrlHandler
import com.shihcheeng.sspaiyou.utils.settingdata.SettingData


@Composable
fun DetailScreenContent(
    modifier: Modifier = Modifier,
    state: LazyListState,
    title: String,
    author: ArticleAuthor,
    publishedDate: String,
    tags: List<String>,
    blocks: List<DetailContent>,
    extendsBodys: List<DetailBodyExtends>,
    bottomPadding: Dp = 0.dp,
    onAvatarClick: (String) -> Unit = {},
    onTagClick: (String) -> Unit = {},
    onImageClick: ((String) -> Unit)? = null
) {

    val markdownColors = MaterialTheme.colorScheme.toMarkDownColors()
    val markdownPadding = markdownPadding(
        block = 6.dp,
        blockQuoteText = PaddingValues(all = 8.dp)
    )
    val markdownAnimations = markdownAnimations()

    val markdownComponents = markdownComponents(
        image = {
            ReaderMarkdownImage(
                content = it.content,
                node = it.node,
                onImageClick = onImageClick
            )
        },
        paragraph = {
            DetailMarkdownParagraph(
                content = it.content,
                node = it.node,
                style = it.typography.paragraph
            )
        },
        table = {
            DetailArticleTable(
                content = it.content,
                node = it.node,
                style = it.typography.table
            )
        }
    )
    val imageTransformer = remember { DetailImageTransform() }
    val setting = LocalComposeSetting.current
    val readerFontScale by setting.observe(SettingData.readerScale)
        .collectAsStateWithLifecycle(initialValue = 1.15f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 32.dp + bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = state
    ) {
        item(
            key = DetailScreenKey.HEADER,
            contentType = DetailScreenKey.HEADER
        ) {
            DetailArticleTitle(
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                title = title,
                publishedDate = publishedDate,
                tags = tags,
                onTagClick = onTagClick
            )
        }
        item(
            key = DetailScreenKey.AUTHOR,
            contentType = DetailScreenKey.AUTHOR
        ) {
            DetailPostUserRow(
                nickname = author.nickname,
                avatar = author.avatar,
                bio = author.bio,
                modifier = Modifier.clickable(enabled = author.slug.isNotBlank()) {
                    onAvatarClick(author.slug)
                }
            )
        }
        if (extendsBodys.isNotEmpty()) {
            extendsBodys.forEachIndexed { indexa, x ->
                item(
                    key = x.id.toString() + "_header_" + indexa,
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
                        x.id.toString() + "_body_" + indexa + "_" + index
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
                            imageTransformer = imageTransformer,
                            colors = markdownColors,
                            typography = sspaiReaderTypo(fontScale = readerFontScale),
                            padding = markdownPadding,
                            components = markdownComponents,
                            animations = markdownAnimations
                        )

                        is DetailContent.Image -> DetailContentImage(item, onImageClick)
                    }
                }
            }

        }
        //with AI.
        itemsIndexed(
            items = blocks,
            key = { index, _ -> "content_$index" },
            contentType = { _, item ->
                when (item) {
                    is DetailContent.Image -> DetailScreenKey.IMAGE
                    is DetailContent.Markdown -> DetailScreenKey.CONTENT
                }
            }
        ) { _, item ->
            when (item) {
                is DetailContent.Markdown -> Markdown(
                    content = item.text,
                    imageTransformer = imageTransformer,
                    colors = markdownColors,
                    typography = sspaiReaderTypo(fontScale = readerFontScale),
                    padding = markdownPadding,
                    components = markdownComponents,
                    animations = markdownAnimations
                )

                is DetailContent.Image -> DetailContentImage(item, onImageClick)
            }
        }
    }
}

@Composable
private fun DetailContentImage(image: DetailContent.Image, onImageClick: ((String) -> Unit)?) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ReaderImage(
            url = image.url,
            contentDescription = image.caption,
            aspectRatio = image.aspectRatio,
            onImageClick = onImageClick
        )
        if (!image.caption.isNullOrBlank()) {
            Text(
                text = image.caption,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
fun customUriHandler(
    urlIsInnerOpen: Boolean,
    onInnerOpen: (String) -> Unit
): UriHandler {
    val context = LocalContext.current
    return remember(context, urlIsInnerOpen, onInnerOpen) {
        SSPaiUrlHandler(context, urlIsInnerOpen, onInnerOpen)
    }
}

@Preview(showBackground = true, device = "id:pixel_9_pro_xl")
@Composable
fun DetailScreenContentPreview() {
    val state = rememberLazyListState()
    val blocks = remember {
        listOf(
            DetailContent.Markdown(
                "除了首页时间流和侧栏的精选展位，少数派 Matrix 社区还有很多优秀内容因条件所限无法得到有效曝光，因此我们决定重启 Matrix 周报。"
            ),
            DetailContent.Image(
                url = "https://cdnfile.sspai.com/2024/10/15/example.png",
                caption = "示例图片说明"
            ),
            DetailContent.Markdown(
                "## 🔥一周热评\n\n来自文章《“iPad mini 换新记：一场消费与理性的博弈”》：\n\n> **知道分子吴先先** (+3) 原来大家在以旧换新的时候，都喜欢用这种明知道不对，但显得花钱少的计算方法哈哈哈：）"
            )
        )
    }
    DetailScreenContent(
        state = state,
        title = "社区速递 068 | 你没见过的社区文章、一周最热评、派友的控制中心",
        author = ArticleAuthor(nickname = "Matrix机器人"),
        publishedDate = "06月16日",
        tags = listOf("Matrix", "社区速递"),
        blocks = blocks,
        extendsBodys = emptyList()
    )
}
