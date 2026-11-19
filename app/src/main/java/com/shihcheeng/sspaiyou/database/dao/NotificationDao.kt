package com.shihcheeng.sspaiyou.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.shihcheeng.sspaiyou.database.datamodel.NotificationEntity
import com.shihcheeng.sspaiyou.database.datamodel.NotificationReadState
import kotlinx.coroutines.flow.Flow

data class StoredNotification(val messageId: String, val payload: String, val isRead: Boolean)

@Dao
abstract class NotificationDao {
    @Query("SELECT n.messageId, n.payload, COALESCE(s.isRead, 0) AS isRead FROM notifications n LEFT JOIN notification_read_states s ON n.account = s.account AND n.kind = s.kind AND n.messageId = s.messageId WHERE n.account = :account AND n.kind = :kind ORDER BY n.position ASC, n.messageId ASC")
    abstract fun pagingSource(account: String, kind: String): PagingSource<Int, StoredNotification>

    @Query("DELETE FROM notifications WHERE account = :account AND kind = :kind")
    protected abstract suspend fun clearMessages(account: String, kind: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertMessages(messages: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertStates(states: List<NotificationReadState>)

    @Query("UPDATE notification_read_states SET isRead = 1, pendingLocalRead = 0 WHERE account = :account AND kind = :kind AND messageId = :id")
    protected abstract suspend fun confirmRead(account: String, kind: String, id: String)

    @Transaction
    open suspend fun savePage(account: String, kind: String, refresh: Boolean, messages: List<NotificationEntity>, states: List<NotificationReadState>) {
        if (refresh) clearMessages(account, kind)
        insertMessages(messages)
        insertStates(states)
        states.filter { it.isRead }.forEach { confirmRead(account, kind, it.messageId) }
    }

    @Query("UPDATE notification_read_states SET isRead = 1, pendingLocalRead = 1 WHERE account = :account AND kind = :kind AND messageId = :id AND isRead = 0")
    abstract suspend fun markRead(account: String, kind: String, id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveSummary(summary: List<NotificationReadState>)

    @Query("""
        SELECT
          MAX(COALESCE((SELECT unreadCount FROM notification_read_states WHERE account = :account AND kind = 'summary' AND messageId = 'online'), 0)
              - (SELECT COUNT(*) FROM notification_read_states WHERE account = :account AND kind = 'online' AND pendingLocalRead = 1),
              (SELECT COUNT(*) FROM notifications n JOIN notification_read_states s ON n.account = s.account AND n.kind = s.kind AND n.messageId = s.messageId WHERE n.account = :account AND n.kind = 'online' AND s.isRead = 0))
          + MAX(COALESCE((SELECT unreadCount FROM notification_read_states WHERE account = :account AND kind = 'summary' AND messageId = 'comment'), 0)
              - (SELECT COUNT(*) FROM notification_read_states WHERE account = :account AND kind = 'comment' AND pendingLocalRead = 1),
              (SELECT COUNT(*) FROM notifications n JOIN notification_read_states s ON n.account = s.account AND n.kind = s.kind AND n.messageId = s.messageId WHERE n.account = :account AND n.kind = 'comment' AND s.isRead = 0))
          + COALESCE((SELECT unreadCount FROM notification_read_states WHERE account = :account AND kind = 'summary' AND messageId = 'other'), 0)
    """)
    abstract fun observeUnreadCount(account: String): Flow<Int>
}
