package com.shihcheeng.sspaiyou.api.data.user.activity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class UserFlag(
    @SerialName("admin_id")
    val adminId: Int = 0,
    @SerialName("allow_add_user")
    val allowAddUser: Boolean = false,
    @SerialName("allow_delete_user")
    val allowDeleteUser: Boolean = false,
    @SerialName("allow_update_status")
    val allowUpdateStatus: Boolean = false,
    @SerialName("color")
    val color: String = "",
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("description")
    val description: String = "",
    @SerialName("icon")
    val icon: String = "",
    @SerialName("icon_3d")
    val icon3d: String = "",
    @SerialName("icon_3d_id")
    val icon3dId: Int = 0,
    @SerialName("icon_app")
    val iconApp: String = "",
    @SerialName("icon_app_3d")
    val iconApp3d: String = "",
    @SerialName("icon_app_3d_id")
    val iconApp3dId: Int = 0,
    @SerialName("icon_app_id")
    val iconAppId: Int = 0,
    @SerialName("icon_id")
    val iconId: Int = 0,
    @SerialName("icon_unlighted")
    val iconUnlighted: String = "",
    @SerialName("icon_unlighted_id")
    val iconUnlightedId: Int = 0,
    @SerialName("icon_unlighted_show_on")
    val iconUnlightedShowOn: Boolean = false,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("key")
    val key: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("object_id")
    val objectId: Int = 0,
    @SerialName("remuneration_fee")
    val remunerationFee: Int = 0,
    @SerialName("status")
    val status: Int = 0,
    @SerialName("sub_type")
    val subType: Int = 0,
    @SerialName("tag")
    val tag: String = "",
    @SerialName("type")
    val type: Int = 0,
    @SerialName("updated_at")
    val updatedAt: Int = 0,
    @SerialName("url")
    val url: String = "",
    @SerialName("user_get_on")
    val userGetOn: Boolean = false,
    @SerialName("weight")
    val weight: Int = 0
)