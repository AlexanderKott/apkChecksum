package com.kotikov.technicalTask.forDrWeb.data.models

data class APKsInfo(
    val apkName: String,
    val apkPath: String,
)

data class FoundAPKs(
    val baseAPK: APKsInfo,
    val splitApk: List<APKsInfo>)