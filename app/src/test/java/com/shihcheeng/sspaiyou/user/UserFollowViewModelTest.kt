package com.shihcheeng.sspaiyou.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowMessageModel
import com.shihcheeng.sspaiyou.api.data.user.follow.UserFollowedDataModel
import com.shihcheeng.sspaiyou.api.data.user.third.Data
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserDataModel
import com.shihcheeng.sspaiyou.repository.UserRepository
import com.shihcheeng.sspaiyou.ui.screen.user.UserViewModel
import com.shihcheeng.sspaiyou.ui.screen.userfollowed.UserFollowedViewModel
import com.shihcheeng.sspaiyou.utils.NetState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserFollowViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val service = FollowUserTestService()
    private val token = MutableStateFlow<String?>("session")
    private val ownSlug = MutableStateFlow<String?>("me")
    private val repository = UserRepository(service) { token.value }

    @Before
    fun setUp() { Dispatchers.setMain(dispatcher) }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun confirmedFollowUpdatesStatusAndFanCountWhilePendingAndFailuresKeepOldState() = runTest {
        val viewModel = createViewModel()
        service.detailRequests.receive().complete(profile())
        viewModel.dataOfUser.first { it is NetState.Success }
        service.followGate = CompletableDeferred()
        viewModel.setFollowed(true)
        runCurrent()
        assertEquals(setOf("target"), viewModel.followPending.value)
        assertFalse(viewModel.profile().followed)
        viewModel.setFollowed(true)
        assertEquals(1, service.followRequests.size)
        service.followGate!!.complete(Unit)
        runCurrent()
        assertTrue(viewModel.profile().followed)
        assertEquals(11, viewModel.profile().followedCount)

        service.followResponse = FollowMessageModel(403, "denied")
        viewModel.setFollowed(false)
        runCurrent()
        assertTrue(viewModel.followError.value)
        assertTrue(viewModel.profile().followed)
        assertEquals(11, viewModel.profile().followedCount)
        assertTrue(viewModel.followPending.value.isEmpty())
        viewModel.clearFollowError()
        service.followResponse = FollowMessageModel(0)
        viewModel.setFollowed(false)
        runCurrent()
        assertFalse(viewModel.profile().followed)
        assertEquals(10, viewModel.profile().followedCount)
        assertFalse(viewModel.followError.value)
    }

    @Test
    fun anonymousAndOwnProfileCannotSendFollowRequests() = runTest {
        token.value = null
        val anonymous = createViewModel()
        anonymous.setFollowed(true)
        token.value = "session"
        val ownProfile = createViewModel("me")
        ownProfile.setFollowed(true)
        runCurrent()
        assertTrue(service.followRequests.isEmpty())
    }

    @Test
    fun accountSwitchReloadsDetailAndDiscardsPendingFollowResult() = runTest {
        val viewModel = createViewModel()
        service.detailRequests.receive().complete(profile())
        viewModel.dataOfUser.first { it is NetState.Success }
        service.followGate = CompletableDeferred()
        viewModel.setFollowed(true)
        runCurrent()
        token.value = "another-session"
        runCurrent()
        service.detailRequests.receive().complete(profile())
        viewModel.dataOfUser.first { it is NetState.Success }
        service.followGate!!.complete(Unit)
        runCurrent()
        assertFalse(viewModel.profile().followed)
        assertEquals(10, viewModel.profile().followedCount)
        assertFalse(viewModel.followError.value)
    }

    @Test
    fun staleDetailResponseCannotOverwriteAConfirmedFollowChange() = runTest {
        val viewModel = createViewModel()
        val detail = service.detailRequests.receive()
        // Another open profile can change this user's follow state during detail loading.
        repository.setFollowed("target", true)
        runCurrent()
        detail.complete(profile())
        viewModel.dataOfUser.first { it is NetState.Success }
        assertTrue(viewModel.profile().followed)
        assertEquals(11, viewModel.profile().followedCount)
    }

    @Test
    fun successfulUnfollowReloadsAnAlreadyOpenFollowedList() = runTest {
        service.users = UserFollowedDataModel(listOf(ColumnUser(slug = "target")), 0, total = 1)
        val viewModel = UserFollowedViewModel(repository, SavedStateHandle(mapOf("user_slug" to "me")))
        store.put("list", viewModel)
        val presenter = object : PagingDataPresenter<ColumnUser>(dispatcher) {
            override suspend fun presentPagingDataEvent(event: PagingDataEvent<ColumnUser>) = Unit
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.users.collectLatest { presenter.collectFrom(it) }
        }
        runCurrent()
        assertEquals(listOf("target"), presenter.snapshot().items.map { it.slug })
        service.followResponse = FollowMessageModel(403)
        try { repository.setFollowed("target", false) } catch (_: IllegalStateException) { }
        runCurrent()
        assertEquals(1, service.listRequests.size)
        assertEquals(1, presenter.snapshot().size)

        service.followResponse = FollowMessageModel(0)
        service.users = UserFollowedDataModel(error = 0, total = 0)
        repository.setFollowed("target", false)
        runCurrent()
        assertEquals(2, service.listRequests.size)
        assertTrue(service.listRequests.all { it.second == 0 && it.third == "me" })
        assertTrue(presenter.snapshot().isEmpty())
    }

    private fun createViewModel(slug: String = "target") = UserViewModel(
        repository, SavedStateHandle(mapOf("slug" to slug)), { token }, { ownSlug }
    ).also { store.put(slug, it) }

    private fun profile() = ThirdUserDataModel(data = Data(slug = "target", followedCount = 10))

    private fun UserViewModel.profile() = (dataOfUser.value as NetState.Success).content.data
}
