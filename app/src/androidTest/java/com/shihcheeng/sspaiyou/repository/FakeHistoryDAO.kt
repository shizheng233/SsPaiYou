package com.shihcheeng.sspaiyou.repository

import com.shihcheeng.sspaiyou.database.dao.HistoryDAO
import com.shihcheeng.sspaiyou.database.datamodel.LocalRecordDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * 内存版 [HistoryDAO]，用于不依赖 Room 数据库的 ViewModel 测试。
 *
 * 所有读写都在同一把锁内完成，避免详情页在 IO 线程保存进度时与断言线程竞争。
 * [observeHistory] 返回的是调用时刻的快照，这些测试不需要观察后续变更。
 */
class FakeHistoryDAO : HistoryDAO {
    private val records = linkedMapOf<Pair<String, Int>, LocalRecordDataModel>()

    /** 已写入的记录，方便测试断言历史是否被持久化。 */
    val savedRecords: List<LocalRecordDataModel>
        get() = synchronized(records) { records.values.toList() }

    override fun observeHistory(ownerKey: String): Flow<List<LocalRecordDataModel>> =
        flowOf(historyFor(ownerKey))

    override suspend fun getHistory(ownerKey: String): List<LocalRecordDataModel> =
        historyFor(ownerKey)

    override suspend fun getHistoryByPostId(ownerKey: String, postId: Int): LocalRecordDataModel? =
        synchronized(records) { records[ownerKey to postId] }

    override suspend fun getHistoryBySlug(ownerKey: String, slug: String): LocalRecordDataModel? =
        synchronized(records) {
            records.values.firstOrNull { it.ownerKey == ownerKey && it.slug == slug }
        }

    override suspend fun upsertHistory(model: LocalRecordDataModel) {
        synchronized(records) { records[model.ownerKey to model.postId] = model }
    }

    override suspend fun deleteHistory(ownerKey: String, postId: Int): Int =
        synchronized(records) { if (records.remove(ownerKey to postId) != null) 1 else 0 }

    override suspend fun clearHistory(ownerKey: String): Int =
        synchronized(records) {
            val keys = records.keys.filter { it.first == ownerKey }
            keys.forEach(records::remove)
            keys.size
        }

    private fun historyFor(ownerKey: String): List<LocalRecordDataModel> =
        synchronized(records) {
            records.values
                .filter { it.ownerKey == ownerKey }
                .sortedWith(
                    compareByDescending<LocalRecordDataModel> { it.lastReadAtEpochMillis }
                        .thenByDescending { it.postId }
                )
        }
}
