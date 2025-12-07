package com.kotikov.technicalTask.forDrWeb.data.models

data class APKsInfo(
    val apkName: String,
    val apkPath: String,
)

sealed class APKLookUpResult {
    data class FoundAPKs(
        val baseAPK: APKsInfo,
        val splitApk: List<APKsInfo>? = null
    ) : APKLookUpResult()

    object Error : APKLookUpResult()
    object Loading : APKLookUpResult()
}