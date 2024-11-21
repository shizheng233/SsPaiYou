package com.shihcheeng.sspaiyou.api.data.user


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Data(
    @SerialName("address")
    val address: Address = Address(),
    @SerialName("api_token_flag")
    val apiTokenFlag: Boolean = false,
    @SerialName("apply_audit_permissions")
    val applyAuditPermissions: Boolean = false,
    @SerialName("article_view_count")
    val articleViewCount: Int = 0,
    @SerialName("articles_count")
    val articlesCount: Int = 0,
    @SerialName("articles_word_count")
    val articlesWordCount: Int = 0,
    @SerialName("avatar")
    val avatar: String = "",
    @SerialName("avatar_id")
    val avatarId: Int = 0,
    @SerialName("banned_until")
    val bannedUntil: Int = 0,
    @SerialName("blocked")
    val blocked: Boolean = false,
    @SerialName("comment_convention_on")
    val commentConventionOn: Boolean = false,
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("email_day_collect_on")
    val emailDayCollectOn: Boolean = false,
    @SerialName("email_important_on")
    val emailImportantOn: Boolean = false,
    @SerialName("email_matrixs")
    val emailMatrixs: Boolean = false,
    @SerialName("email_messages")
    val emailMessages: Boolean = false,
    @SerialName("email_on")
    val emailOn: Boolean = false,
    @SerialName("exp_on")
    val expOn: Boolean = false,
    @SerialName("followed_count")
    val followedCount: Int = 0,
    @SerialName("following_count")
    val followingCount: Int = 0,
    @SerialName("franchised_writer")
    val franchisedWriter: Boolean = false,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("id_card_auth")
    val idCardAuth: Boolean = false,
    @SerialName("id_card_code")
    val idCardCode: String = "",
    @SerialName("id_hash")
    val idHash: String = "",
    @SerialName("in_mainland")
    val inMainland: Boolean = false,
    @SerialName("is_email_verified")
    val isEmailVerified: Boolean = false,
    @SerialName("is_series_author")
    val isSeriesAuthor: Boolean = false,
    @SerialName("key")
    val key: String = "",
    @SerialName("liked_count")
    val likedCount: Int = 0,
    @SerialName("matrix_article_count")
    val matrixArticleCount: Int = 0,
    @SerialName("matrix_article_style")
    val matrixArticleStyle: Int = 0,
    @SerialName("member")
    val member: Member = Member(),
    @SerialName("message_contact_unread_total")
    val messageContactUnreadTotal: Int = 0,
    @SerialName("message_on")
    val messageOn: Boolean = false,
    @SerialName("money")
    val money: Int = 0,
    @SerialName("national_area_code")
    val nationalAreaCode: String = "",
    @SerialName("national_area_id")
    val nationalAreaId: Int = 0,
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("no_read")
    val noRead: Boolean = false,
    @SerialName("password_bind")
    val passwordBind: Boolean = false,
    @SerialName("phone")
    val phone: String = "",
    @SerialName("power_plus_flag")
    val powerPlusFlag: Int = 0,
    @SerialName("product_booking_outstation_auth_on")
    val productBookingOutstationAuthOn: Boolean = false,
    @SerialName("product_booking_outstation_auth_updated_at")
    val productBookingOutstationAuthUpdatedAt: Int = 0,
    @SerialName("professional_writer")
    val professionalWriter: Boolean = false,
    @SerialName("push_community_chosen_on")
    val pushCommunityChosenOn: Boolean = false,
    @SerialName("push_community_reply_on")
    val pushCommunityReplyOn: Boolean = false,
    @SerialName("push_community_subscribe_on")
    val pushCommunitySubscribeOn: Boolean = false,
    @SerialName("push_on")
    val pushOn: Boolean = false,
    @SerialName("recommend_article_count")
    val recommendArticleCount: Int = 0,
    @SerialName("recommend_to_home_article_count")
    val recommendToHomeArticleCount: Int = 0,
    @SerialName("released_article_count")
    val releasedArticleCount: Int = 0,
    @SerialName("role")
    val role: Int = 0,
    @SerialName("series_article_noread_total")
    val seriesArticleNoreadTotal: Int = 0,
    @SerialName("series_comment_manage_count")
    val seriesCommentManageCount: Int = 0,
    @SerialName("series_pay_count")
    val seriesPayCount: Int = 0,
    @SerialName("signed_writer")
    val signedWriter: Boolean = false,
    @SerialName("slug")
    val slug: String = "",
    @SerialName("slug_update_count")
    val slugUpdateCount: Int = 0,
    @SerialName("social_networks")
    val socialNetworks: List<SocialNetwork>? = listOf(),
    @SerialName("special_column_apply_on")
    val specialColumnApplyOn: Boolean = false,
    @SerialName("special_column_writer")
    val specialColumnWriter: Boolean = false,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    @SerialName("withdrawal_corporate_on")
    val withdrawalCorporateOn: Boolean = false
)