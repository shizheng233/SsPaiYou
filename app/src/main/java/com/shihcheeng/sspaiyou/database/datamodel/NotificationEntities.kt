package com.shihcheeng.sspaiyou.database.datamodel

import androidx.room.Entity

@Entity(tableName = "notifications", primaryKeys = ["account", "kind", "messageId"])
data class NotificationEntity(
    val account: String,
    val kind: String,
    val messageId: String,
    val position: Int,
    val payload: String
)

// The reserved summary row stores the latest server unread count for this account.
@Entity(tableName = "notification_read_states", primaryKeys = ["account", "kind", "messageId"])
data class NotificationReadState(
    val account: String,
    val kind: String,
    val messageId: String,
    val isRead: Boolean = false,
    val pendingLocalRead: Boolean = false,
    val unreadCount: Int = 0
)
