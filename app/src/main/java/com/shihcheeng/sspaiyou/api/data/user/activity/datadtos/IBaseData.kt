package com.shihcheeng.sspaiyou.api.data.user.activity.datadtos

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable(with = DataInteraction::class)
sealed interface IBaseData

