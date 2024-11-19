package com.shihcheeng.sspaiyou.utils.extra

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.shihcheeng.sspaiyou.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Long.timestampToChineseDate(): String {
    val format = SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE)
    format.timeZone = TimeZone.getTimeZone("GMT+8")
    val instant = Instant.ofEpochSecond(this)
    val date = Date(instant.toEpochMilli())
    val reformatedData = format.format(date).split("/").map { x -> x.toInt() }
    val yearChinese = reformatedData[0].numberToChineseNumeric()
    val month = reformatedData[1].numberToChineseNumericAlt()
    val day = reformatedData[2].numberToChineseNumericAlt()
    return buildString {
        append(yearChinese)
        append("年")
        append(month)
        append("月")
        append(day)
        append("日")
    }
}

fun Long.timestampToChinese(): String {
    val format = SimpleDateFormat("HH:mm", Locale.CHINESE)
    format.timeZone = TimeZone.getTimeZone("GMT+8")
    val instant = Instant.ofEpochSecond(this)
    val date = Date(instant.toEpochMilli())
    val reformatedData = format.format(date)
    return reformatedData
}

@Composable
fun Long.calculateDay(): String {
    val instant = Instant.ofEpochSecond(this).toEpochMilli()
    val now = System.currentTimeMillis()
    return when (((now - instant) * 1.0f / (24 * 60 * 60 * 1000)).roundToInt()) {
        1 -> {
            stringResource(R.string.yesterday)
        }

        0 -> {
            stringResource(R.string.today)
        }

        2 -> {
            stringResource(R.string.the_day_before_yesterday)
        }

        else -> {
            val format = SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE)
            format.timeZone = TimeZone.getTimeZone("GMT+8")
            val date = Date(instant)
            format.format(date)
        }
    }
}

fun Int.numberToChineseNumeric(): String {
    val chineseNumerics = arrayOf("〇", "一", "二", "三", "四", "五", "六", "七", "八", "九")
    val sb = StringBuilder()
    this.toString().forEach { digitChar ->
        val digit = digitChar.toString().toInt()
        sb.append(chineseNumerics[digit])
    }
    return sb.toString()
}

fun Int.numberToChineseNumericAlt(): String {
    val chineseNumerics = arrayOf("〇", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十")
    val chineseNumericsII =
        arrayOf("零", "十", "二十", "三十", "四十", "五十", "六十", "七十", "八十", "九十")
    val sb = StringBuilder()
    if (this < 10) {
        sb.append(chineseNumerics[this])
    } else {
        sb.append(chineseNumericsII[this / 10])
        sb.append(chineseNumerics[this % 10])
    }
    return sb.toString()
}

fun Long.untilToDay(): Int {
    val instant = Instant.ofEpochSecond(this).toEpochMilli()
    val timed = System.currentTimeMillis() - instant
    return timed.toDuration(DurationUnit.MILLISECONDS).toInt(DurationUnit.DAYS)
}