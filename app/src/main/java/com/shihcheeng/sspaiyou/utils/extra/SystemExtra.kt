package com.shihcheeng.sspaiyou.utils.extra

import com.shihcheeng.sspaiyou.BuildConfig
import java.time.Instant

fun getSystemSecond(): Long {
    return Instant.now().epochSecond
}

fun createAtFor(offset: Int): Long = if (offset == 0) 0 else getSystemSecond()

fun Exception.printStackOnlyDebug() {
    if (BuildConfig.DEBUG) {
        printStackTrace()
    }
}