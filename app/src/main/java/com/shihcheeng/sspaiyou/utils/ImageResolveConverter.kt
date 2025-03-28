package com.shihcheeng.sspaiyou.utils

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.util.data.MutableDataHolder

object ImageResolveConverter : FlexmarkHtmlConverter.HtmlConverterExtension {


    override fun rendererOptions(options: MutableDataHolder) {

    }

    override fun extend(builder: FlexmarkHtmlConverter.Builder) {
        builder.htmlNodeRendererFactory(ImageNodeResolve.Companion.Factory)
    }
}