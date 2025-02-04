package com.shihcheeng.sspaiyou.utils.extra

import java.util.Locale

/**
 * 转换单位，鉴于少数派大多数为华人用户，故不做i18n处理。
 */
fun Long.formatWithUnit(): String {
    val units = arrayOf("", "千", "万", "百万", "亿", "十亿", "百亿", "千亿", "万亿")
    var value = this.toDouble()
    var unitIndex = 0

    while (value >= 1000 && unitIndex < units.size - 1) {
        value /= 1000
        unitIndex++
    }

    return String.format(Locale.CHINESE, "%.1f%s", value, units[unitIndex])
}