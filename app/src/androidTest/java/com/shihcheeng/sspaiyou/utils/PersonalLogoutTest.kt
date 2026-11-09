package com.shihcheeng.sspaiyou.utils

import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import com.shihcheeng.sspaiyou.api.data.user.Data
import com.shihcheeng.sspaiyou.api.data.user.UserinfoDataModel
import com.shihcheeng.sspaiyou.api.data.user.activity.UserActivityDataModel
import com.shihcheeng.sspaiyou.api.data.user.favourite.UserFavouriteDataModel
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserDataModel
import com.shihcheeng.sspaiyou.api.service.UserService
import com.shihcheeng.sspaiyou.repository.UserRepository
import com.shihcheeng.sspaiyou.ui.screen.main.personal.PersonalViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.UUID

class PersonalLogoutTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val preferencesName = "personal-logout-test-${UUID.randomUUID()}"
    private val cipher = CookieCipher("personal-logout-test-${UUID.randomUUID()}")
    private val store = ViewModelStore()
    private val service = PendingUserService()
    private lateinit var manager: LocalTokenManager
    private lateinit var viewModel: PersonalViewModel

    @Before
    fun setUp() = runBlocking {
        manager = LocalTokenManager(
            context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE), cipher
        )
        manager.saveToken("old-session")
        withContext(Dispatchers.Main) {
            viewModel = PersonalViewModel(manager, UserRepository(service))
            store.put("personal", viewModel)
        }
    }

    @After
    fun tearDown() = runBlocking {
        withContext(Dispatchers.Main) { store.clear() }
        context.deleteSharedPreferences(preferencesName)
        cipher.deleteKey()
    }

    @Test
    fun logoutClearsLoadedProfileAndNextLoginLoadsTheNewUser() = runBlocking {
        withTimeout(5_000) {
            service.requests.receive().response.complete(profile("old-user"))
            viewModel.info.first { it is NetState.Success }
            assertEquals("old-user", manager.getSlug())

            manager.logout()

            viewModel.token.first { it == null }
            viewModel.info.first { it == NetState.Loading }
            assertNull(manager.getSlug())

            manager.saveToken("new-session")
            service.requests.receive().response.complete(profile("new-user"))
            val info = viewModel.info.first {
                it is NetState.Success && it.content.data.slug == "new-user"
            }
            assertEquals(NetState.Success(profile("new-user")), info)
            assertEquals("new-user", manager.getSlug())
        }
    }

    @Test
    fun logoutCancelsPendingUserInfoAndLateResponseCannotRestoreProfile() = runBlocking {
        withTimeout(5_000) {
            val oldRequest = service.requests.receive()

            manager.logout()

            oldRequest.finished.await()
            viewModel.token.first { it == null }
            // Finishing a response after cancellation must not repopulate the profile or slug.
            oldRequest.response.complete(profile("old-user"))
            withContext(Dispatchers.Main) {
                assertEquals(NetState.Loading, viewModel.info.value)
            }
            assertNull(manager.getSlug())

            manager.saveToken("new-session")
            service.requests.receive().response.complete(profile("new-user"))
            viewModel.info.first {
                it is NetState.Success && it.content.data.slug == "new-user"
            }
            assertEquals("new-user", manager.getSlug())
        }
    }

    private fun profile(slug: String) = UserinfoDataModel(data = Data(slug = slug))

    private class PendingUserService : UserService {
        val requests = Channel<Request>(Channel.UNLIMITED)

        override suspend fun getUserInfo(): UserinfoDataModel {
            val request = Request()
            requests.send(request)
            return try {
                request.response.await()
            } finally {
                request.finished.complete(Unit)
            }
        }

        override suspend fun getUserInfo(slug: String): ThirdUserDataModel = error("unused")

        override suspend fun getUserActivity(
            limit: Int, offset: Int, createdAt: Long, slug: String
        ): UserActivityDataModel = error("unused")

        override suspend fun getUserFavourite(
            limit: Int, offset: Int, createdAt: Long, slug: String, type: String
        ): UserFavouriteDataModel = error("unused")
    }

    private class Request {
        val response = CompletableDeferred<UserinfoDataModel>()
        val finished = CompletableDeferred<Unit>()
    }
}
