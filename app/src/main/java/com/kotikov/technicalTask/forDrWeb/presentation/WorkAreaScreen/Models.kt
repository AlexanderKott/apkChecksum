package com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen

import android.graphics.Bitmap

data class FullAppInfo(
    val appName: String,
    val packageName: String,
    val isSystemApp: Boolean,
    val canBeLaunched: Boolean,
    val isDebuggable: Boolean,
    val icon: Bitmap,
    val versionName: String,
    val versionCode: Long,
    val isTechnicalName: Boolean,
)

sealed class UIStatus {
    object Loading : UIStatus()
    object EmptyList : UIStatus()
    data class Ready(val payload: List<FullAppInfo>) : UIStatus()
    data class Error(val messageResID: Int) : UIStatus()
}