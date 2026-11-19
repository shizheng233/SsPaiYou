package com.shihcheeng.sspaiyou.modula

import com.shihcheeng.sspaiyou.repository.NotificationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NotificationEntryPoint {
    fun notificationRepository(): NotificationRepository
}
