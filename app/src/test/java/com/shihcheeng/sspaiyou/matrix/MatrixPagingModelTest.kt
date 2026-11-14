package com.shihcheeng.sspaiyou.matrix

import com.shihcheeng.sspaiyou.api.data.matrix.host.MatrixHostDataModel
import com.shihcheeng.sspaiyou.api.data.matrix.normal.MatrixNormalDataModel
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MatrixPagingModelTest {
    @Test
    fun absentAndNullTotalsStayUnknownForBothFeeds() {
        for (payload in listOf(
            """{"error":0,"data":[{"id":1}]}""",
            """{"error":0,"total":null,"data":[{"id":1}]}"""
        )) {
            val normal = Json.decodeFromString<MatrixNormalDataModel>(payload)
            val hot = Json.decodeFromString<MatrixHostDataModel>(payload)
            assertNull(normal.total)
            assertNull(hot.total)
            assertEquals(listOf(1), normal.data.map { it.id })
            assertEquals(listOf(1), hot.data.map { it.id })
        }
        val zero = """{"error":0,"total":0,"data":[]}"""
        assertEquals(0, Json.decodeFromString<MatrixNormalDataModel>(zero).total)
        assertEquals(0, Json.decodeFromString<MatrixHostDataModel>(zero).total)
    }

    @Test
    fun errorEnvelopesRetainCodeAndMessageWithoutRequiringItemsOrTotal() {
        val payload = """{"error":403,"msg":"Access denied"}"""
        val normal = Json.decodeFromString<MatrixNormalDataModel>(payload)
        val hot = Json.decodeFromString<MatrixHostDataModel>(payload)
        assertEquals(403, normal.error)
        assertEquals(403, hot.error)
        assertEquals("Access denied", normal.msg)
        assertEquals("Access denied", hot.msg)
        assertTrue(normal.data.isEmpty())
        assertTrue(hot.data.isEmpty())
        assertNull(normal.total)
        assertNull(hot.total)
    }
}
