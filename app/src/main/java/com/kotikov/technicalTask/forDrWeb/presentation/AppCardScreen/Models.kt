package com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen

import com.kotikov.technicalTask.forDrWeb.data.models.FoundAPKs
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.FullAppInfo

sealed class AppInfoResult {
    object Loading : AppInfoResult()
    data class DataReady(val data: FullAppInfo) : AppInfoResult()
    object Error : AppInfoResult()
    object AppHasBeenDeleted : AppInfoResult()
}

sealed class ApkDetails {
    object Loading : ApkDetails()
    data class Success(val data: APKsInfoWithHash) : ApkDetails()
    data class Error(val errorMessage: String) : ApkDetails()
}

data class APKsInfoWithHash(
    val hash: String,
    val apkInfo: FoundAPKs,
)

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}


enum class CurrentAppUpdate {
    INIT, DELETED, CHANGED
}