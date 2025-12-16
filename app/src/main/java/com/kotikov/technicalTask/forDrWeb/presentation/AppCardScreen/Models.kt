package com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen

import com.kotikov.technicalTask.forDrWeb.data.models.FoundAPKs
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.FullAppInfo

sealed class AppInfoResult {
    object Loading : AppInfoResult()
    data class Success(val payload: FullAppInfo) : AppInfoResult()
    object Error : AppInfoResult()
}

sealed class ApkDetails {
    object Loading : ApkDetails()
    data class Success(val payload: APKsInfoWithHash) : ApkDetails()
    data class Error(val errorMessage: String) : ApkDetails()
}

data class APKsInfoWithHash(
    val hash: String,
    val apkInfo: FoundAPKs,
)