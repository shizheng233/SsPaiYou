package com.shihcheeng.sspaiyou.ui.compoents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.mikepenz.markdown.utils.getUnescapedTextInNode
import com.shihcheeng.sspaiyou.utils.imageUrlForNullable
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType

@Composable
fun ReaderMarkdownImage(
    content: String,
    node: ASTNode
) {

    val link = remember {
        node.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)
            ?.getUnescapedTextInNode(content)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = imageUrlForNullable(link),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(MaterialTheme.shapes.medium),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceContainer)
        )
        Text(text = "图")
    }
}