package com.shihcheeng.sspaiyou.activities.detail

import android.content.Context
import com.google.android.material.color.MaterialColors
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.syntax.SyntaxHighlightNoOp
import com.google.android.material.R as MaterialR

class DetailMarkwonThemePlugin(
    private val context: Context
) : AbstractMarkwonPlugin() {

    override fun configureTheme(builder: MarkwonTheme.Builder) {
        builder.codeTextColor(
            MaterialColors.getColor(
                context,
                MaterialR.attr.colorOnSurface,
                "colorOnSurface"
            )
        )
        builder.codeBackgroundColor(
            MaterialColors.getColor(
                context,
                MaterialR.attr.colorSurfaceVariant,
                "colorSurfaceVariant"
            )
        )
        builder.codeBlockTextColor(
            MaterialColors.getColor(
                context,
                MaterialR.attr.colorOnSurfaceVariant,
                "colorOnSurfaceVariant"
            )
        )
        builder.codeBlockBackgroundColor(
            MaterialColors.getColor(
                context,
                MaterialR.attr.colorSurfaceVariant,
                "colorSurfaceVariant"
            )
        )
        builder.blockQuoteColor(
            MaterialColors.getColor(
                context,
                MaterialR.attr.colorSurfaceVariant,
                "colorSurfaceVariant"
            )
        )
        builder.headingBreakHeight(0)
    }

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.syntaxHighlight(SyntaxHighlightNoOp())
    }
}