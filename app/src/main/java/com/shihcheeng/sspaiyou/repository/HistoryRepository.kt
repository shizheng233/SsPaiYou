package com.shihcheeng.sspaiyou.repository

import com.shihcheeng.sspaiyou.database.dao.HistoryDAO
import com.shihcheeng.sspaiyou.database.datamodel.LocalRecordDataModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDAO: HistoryDAO
) {

    fun observeHistory(ownerKey: String): Flow<List<LocalRecordDataModel>> {
        requireOwnerKey(ownerKey)
        return historyDAO.observeHistory(ownerKey)
    }

    suspend fun getHistory(ownerKey: String): List<LocalRecordDataModel> {
        requireOwnerKey(ownerKey)
        return historyDAO.getHistory(ownerKey)
    }

    suspend fun getHistoryByPostId(
        ownerKey: String,
        postId: Int
    ): LocalRecordDataModel? {
        requireOwnerKey(ownerKey)
        requirePostId(postId)
        return historyDAO.getHistoryByPostId(ownerKey, postId)
    }

    suspend fun getHistoryBySlug(
        ownerKey: String,
        slug: String
    ): LocalRecordDataModel? {
        requireOwnerKey(ownerKey)
        require(slug.isNotBlank()) { "Article slug must not be blank" }
        return historyDAO.getHistoryBySlug(ownerKey, slug)
    }

    suspend fun upsertHistory(model: LocalRecordDataModel) {
        requireOwnerKey(model.ownerKey)
        requirePostId(model.postId)
        require(model.readProgress in 0f..1f) {
            "Reading progress must be between 0 and 1"
        }
        require(model.lastReadAtEpochMillis >= 0L) {
            "Last-read timestamp must not be negative"
        }
        historyDAO.upsertHistory(model)
    }

    suspend fun deleteHistory(ownerKey: String, postId: Int): Boolean {
        requireOwnerKey(ownerKey)
        requirePostId(postId)
        return historyDAO.deleteHistory(ownerKey, postId) > 0
    }

    suspend fun clearHistory(ownerKey: String): Int {
        requireOwnerKey(ownerKey)
        return historyDAO.clearHistory(ownerKey)
    }

    private fun requireOwnerKey(ownerKey: String) {
        require(ownerKey.isNotBlank()) { "Owner key must not be blank" }
    }

    private fun requirePostId(postId: Int) {
        require(postId > 0) { "Post ID must be greater than 0" }
    }

}
