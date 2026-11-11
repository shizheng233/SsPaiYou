package com.shihcheeng.sspaiyou.ui.compoents

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mikepenz.markdown.utils.getUnescapedTextInNode
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType

@Composable
fun ReaderMarkdownImage(
    content: String,
    node: ASTNode,
    onImageClick: ((String) -> Unit)? = null
) {

    val link = remember(content, node) {
        node.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)
            ?.getUnescapedTextInNode(content)
    }

    ReaderImage(url = link, onImageClick = onImageClick)
}
