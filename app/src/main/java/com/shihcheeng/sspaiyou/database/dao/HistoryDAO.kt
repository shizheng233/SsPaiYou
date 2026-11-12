package com.shihcheeng.sspaiyou.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shihcheeng.sspaiyou.database.datamodel.LocalRecordDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDAO {
    @Query(
        """
        SELECT * FROM reading_records
        WHERE ownerKey = :ownerKey
        ORDER BY lastReadAtEpochMillis DESC, postId DESC
        """
    )
    fun observeHistory(ownerKey: String): Flow<List<LocalRecordDataModel>>

    @Query(
        """
        SELECT * FROM reading_records
        WHERE ownerKey = :ownerKey
        ORDER BY lastReadAtEpochMillis DESC, postId DESC
        """
    )
    suspend fun getHistory(ownerKey: String): List<LocalRecordDataModel>

    @Query(
        """
        SELECT * FROM reading_records
        WHERE ownerKey = :ownerKey AND postId = :postId
        LIMIT 1
        """
    )
    suspend fun getHistoryByPostId(ownerKey: String, postId: Int): LocalRecordDataModel?

    @Query(
        """
        SELECT * FROM reading_records
        WHERE ownerKey = :ownerKey AND slug = :slug
        LIMIT 1
        """
    )
    suspend fun getHistoryBySlug(ownerKey: String, slug: String): LocalRecordDataModel?

    @Upsert
    suspend fun upsertHistory(model: LocalRecordDataModel)

    @Query(
        "DELETE FROM reading_records WHERE ownerKey = :ownerKey AND postId = :postId"
    )
    suspend fun deleteHistory(ownerKey: String, postId: Int): Int

    @Query("DELETE FROM reading_records WHERE ownerKey = :ownerKey")
    suspend fun clearHistory(ownerKey: String): Int
}
