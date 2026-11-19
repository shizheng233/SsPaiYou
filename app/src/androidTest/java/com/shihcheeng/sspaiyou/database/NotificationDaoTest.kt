package com.shihcheeng.sspaiyou.database

import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shihcheeng.sspaiyou.database.base.NotificationDatabase
import com.shihcheeng.sspaiyou.database.datamodel.NotificationEntity
import com.shihcheeng.sspaiyou.database.datamodel.NotificationReadState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationDaoTest {
    private lateinit var db: NotificationDatabase

    @Before fun setUp() {
        db = Room.inMemoryDatabaseBuilder<NotificationDatabase>(InstrumentationRegistry.getInstrumentation().targetContext)
            .setDriver(AndroidSQLiteDriver()).build()
    }

    @After fun tearDown() = db.close()

    @Test fun localReadsSurviveRefreshAndRemainAccountScoped() = runBlocking {
        val dao = db.notificationDao()
        val message = NotificationEntity("a", "online", "1", 0, "{}")
        val state = NotificationReadState("a", "online", "1")
        dao.saveSummary(listOf(NotificationReadState("a", "summary", "online", unreadCount = 2)))
        dao.savePage("a", "online", true, listOf(message), listOf(state))
        dao.savePage("b", "online", true, listOf(message.copy(account = "b")), listOf(state.copy(account = "b")))
        assertEquals(2, dao.observeUnreadCount("a").first())
        assertEquals(1, dao.observeUnreadCount("b").first())

        dao.markRead("a", "online", "1")
        dao.markRead("a", "online", "1")
        assertEquals(1, dao.observeUnreadCount("a").first())
        assertEquals(1, dao.observeUnreadCount("b").first())

        dao.savePage("a", "online", true, listOf(message), listOf(state))
        assertEquals(1, dao.observeUnreadCount("a").first())
        dao.savePage("a", "online", true, listOf(message), listOf(state.copy(isRead = true)))
        dao.saveSummary(listOf(NotificationReadState("a", "summary", "online", unreadCount = 1)))
        assertEquals(1, dao.observeUnreadCount("a").first())
    }

    @Test fun readInOneCategoryDoesNotHideOtherCategories() = runBlocking {
        val dao = db.notificationDao()
        dao.saveSummary(listOf(
            NotificationReadState("a", "summary", "online", unreadCount = 1),
            NotificationReadState("a", "summary", "other", unreadCount = 2)
        ))
        dao.savePage("a", "online", true,
            listOf(NotificationEntity("a", "online", "1", 0, "{}")),
            listOf(NotificationReadState("a", "online", "1")))
        dao.markRead("a", "online", "1")
        assertEquals(2, dao.observeUnreadCount("a").first())
    }
}
