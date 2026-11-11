package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.LocalMarkdownDimens
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.LocalTableRowIndex
import com.mikepenz.markdown.compose.elements.MarkdownTable
import com.mikepenz.markdown.compose.elements.MarkdownTableHeader
import com.mikepenz.markdown.compose.elements.MarkdownTableRow
import com.mikepenz.markdown.model.MarkdownDimens
import com.shihcheeng.sspaiyou.ui.theme.sspaiReaderTypo
import com.shihcheeng.sspaiyou.ui.theme.toMarkDownColors
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

/** Uses the renderer's table layout, inline formatting, scrolling and collection semantics. */
@Composable
fun DetailArticleTable(
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.table,
) {
    val shape = MaterialTheme.shapes.small
    val dimensions = LocalMarkdownDimens.current
    val tableDimensions = remember(dimensions) {
        object : MarkdownDimens by dimensions {
            override val tableCellPadding = 12.dp
        }
    }
    val rowCount = remember(node) { node.children.count { it.type == GFMElementTypes.ROW } }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
    ) {
        CompositionLocalProvider(LocalMarkdownDimens provides tableDimensions) {
            MarkdownTable(
                content = content,
                node = node,
                style = style,
                headerBlock = { source, header, width, textStyle ->
                    // Headerless HTML tables receive an empty GFM header during conversion.
                    val hasHeader = remember(source, header) {
                        header.children.any {
                            it.type == GFMTokenTypes.CELL &&
                                source.substring(it.startOffset, it.endOffset).isNotBlank()
                        }
                    }
                    if (hasHeader) {
                        Box(Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
                            MarkdownTableHeader(
                                content = source,
                                header = header,
                                tableWidth = width,
                                style = textStyle,
                                verticalAlignment = Alignment.Top,
                                maxLines = Int.MAX_VALUE,
                                overflow = TextOverflow.Clip
                            )
                        }
                    }
                },
                rowBlock = { source, row, width, textStyle ->
                    Column {
                        MarkdownTableRow(
                            content = source,
                            header = row,
                            tableWidth = width,
                            style = textStyle,
                            verticalAlignment = Alignment.Top,
                            maxLines = Int.MAX_VALUE,
                            overflow = TextOverflow.Clip
                        )
                        if (LocalTableRowIndex.current < rowCount) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun DetailArticleTablePreview() {
    MaterialTheme {
        Markdown(
            content = """
                | 遥控器按键 | 发送的快捷键 | 实际用途 |
                | --- | --- | --- |
                | **方向键** | `Ctrl + Alt + Left` | 切换到上一个终端窗口，继续查看运行结果。 |
                | **中心 OK 键** | `Enter` | 确认执行 |
            """.trimIndent(),
            colors = MaterialTheme.colorScheme.toMarkDownColors(),
            typography = sspaiReaderTypo(),
            components = markdownComponents(table = {
                DetailArticleTable(content = it.content, node = it.node, style = it.typography.table)
            })
        )
    }
}
