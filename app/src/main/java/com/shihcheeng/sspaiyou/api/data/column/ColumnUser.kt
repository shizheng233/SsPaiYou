package com.shihcheeng.sspaiyou.api.data.column

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Keep
@Serializable
data class ColumnUser(
    @SerialName("id") val id: Int = 0,
    @SerialName("id_hash") val idHash: String = "",
    @SerialName("slug") val slug: String = "",
    @SerialName("avatar") val avatar: String = "",
    @SerialName("nickname") val nickname: String = "",
    @SerialName("bio") val bio: String = "",
    @SerialName("role") val role: String = "",
    @SerialName("followed") val followed: Boolean = false,
    @SerialName("follow_count") val followCount: Int = 0,
    @SerialName("fans_count") val fansCount: Int = 0,
    @SerialName("article_count") val articleCount: Int = 0,
    @SerialName("user_flags") val userFlags: List<ColumnBadge> = emptyList(),
    @SerialName("user_badges") val userBadges: List<ColumnBadge> = emptyList(),
    @SerialName("user_plan_flags") val userPlanFlags: List<ColumnBadge> = emptyList(),
    @SerialName("user_id_verify_flags") val userIdVerifyFlags: List<JsonElement> = emptyList(),
    @SerialName("intro") val intro: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("recommend_to_home_flag") val recommendToHomeFlag: ColumnBadge = ColumnBadge()
)

@Keep
@Serializable
data class ColumnBadge(
    @SerialName("id") val id: Int = 0,
    @SerialName("key") val key: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("icon_id") val iconId: Int = 0,
    @SerialName("icon") val icon: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("status") val status: Int = 0,
    @SerialName("weight") val weight: Int = 0,
    @SerialName("color") val color: String = "",
    @SerialName("type") val type: Int = 0,
    @SerialName("sub_type") val subType: Int = 0,
    @SerialName("object_id") val objectId: Int = 0,
    @SerialName("allow_add_user") val allowAddUser: Boolean = false,
    @SerialName("allow_delete_user") val allowDeleteUser: Boolean = false,
    @SerialName("allow_update_status") val allowUpdateStatus: Boolean = false,
    @SerialName("icon_app") val iconApp: String = "",
    @SerialName("icon_app_id") val iconAppId: Int = 0,
    @SerialName("icon_unlighted") val iconUnlighted: String = "",
    @SerialName("icon_unlighted_id") val iconUnlightedId: Int = 0,
    @SerialName("tag") val tag: String = "",
    @SerialName("icon_unlighted_show_on") val iconUnlightedShowOn: Boolean = false,
    @SerialName("admin_id") val adminId: Int = 0,
    @SerialName("user_get_on") val userGetOn: Boolean = false,
    @SerialName("icon_3d_id") val icon3dId: Int = 0,
    @SerialName("icon_3d") val icon3d: String = "",
    @SerialName("icon_app_3d_id") val iconApp3dId: Int = 0,
    @SerialName("icon_app_3d") val iconApp3d: String = "",
    @SerialName("created_at") val createdAt: Long = 0L,
    @SerialName("updated_at") val updatedAt: Long = 0L,
    @SerialName("recommend_to_home_on") val recommendToHomeOn: Boolean = false,
    @SerialName("remuneration_fee") val remunerationFee: Int = 0
)
