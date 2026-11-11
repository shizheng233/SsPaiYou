package com.shihcheeng.sspaiyou.utils

object ImageCacheConfig {
    // Shared by article thumbnails, comment images and the full-screen viewer.
    const val DEFAULT_MAX_SIZE_BYTES: Long = 256L * 1024 * 1024
    const val DIRECTORY_NAME = "image_cache"
}
