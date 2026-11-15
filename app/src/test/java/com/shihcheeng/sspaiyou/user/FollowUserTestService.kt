package com.shihcheeng.sspaiyou.user

import com.shihcheeng.sspaiyou.api.data.user.UserinfoDataModel
import com.shihcheeng.sspaiyou.api.data.user.activity.UserActivityDataModel
import com.shihcheeng.sspaiyou.api.data.user.favourite.UserFavouriteDataModel
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowMessageModel
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowUserRequest
import com.shihcheeng.sspaiyou.api.data.user.follow.UserFollowedDataModel
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserDataModel
import com.shihcheeng.sspaiyou.api.service.UserService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

internal class FollowUserTestService : UserService {
    var users = UserFollowedDataModel(error = 0)
    var followResponse = FollowMessageModel(0)
    var followGate: CompletableDeferred<Unit>? = null
    var failure: Exception? = null
    val listRequests = mutableListOf<Triple<Int, Int, String>>()
    val followRequests = mutableListOf<FollowUserRequest>()
    val detailRequests = Channel<CompletableDeferred<ThirdUserDataModel>>(Channel.UNLIMITED)

    override suspend fun followUser(request: FollowUserRequest): FollowMessageModel {
        followRequests += request
        followGate?.await()
        failure?.let { throw it }
        return followResponse
    }

    override suspend fun getUserList(limit: Int, offset: Int, slug: String): UserFollowedDataModel {
        listRequests += Triple(limit, offset, slug)
        failure?.let { throw it }
        return users
    }

    override suspend fun getUserInfo(slug: String): ThirdUserDataModel {
        val response = CompletableDeferred<ThirdUserDataModel>()
        detailRequests.send(response)
        return response.await()
    }

    override suspend fun getUserInfo(): UserinfoDataModel = error("unused")

    override suspend fun getUserActivity(
        limit: Int, offset: Int, createdAt: Long, slug: String
    ): UserActivityDataModel = error("unused")

    override suspend fun getUserFavourite(
        limit: Int, offset: Int, createdAt: Long, slug: String, type: String
    ): UserFavouriteDataModel = error("unused")
}
