package com.shihcheeng.sspaiyou.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shihcheeng.sspaiyou.database.datamodel.LocalSearchRecordDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchKeywordDAO {

    @Query("SELECT * FROM local_search_record ORDER BY time DESC")
    fun getKeywords(): Flow<List<LocalSearchRecordDataModel>>

    @Upsert
    suspend fun updateKeywords(recordDataModel: LocalSearchRecordDataModel)
}
