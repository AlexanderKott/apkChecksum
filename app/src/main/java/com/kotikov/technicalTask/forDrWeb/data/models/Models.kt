package com.kotikov.technicalTask.forDrWeb.data.models

data class APKsInfo(
    val apkName: String,
    val apkPath: String,
)

data class FoundAPKs(
    val baseAPK: APKsInfo,
    val splitApk: List<APKsInfo>)


sealed class AppChangeEvent {
    abstract val packageName: String

    data class Added(override val packageName: String) : AppChangeEvent()
    data class Removed(override val packageName: String) : AppChangeEvent()
    data class Changed(override val packageName: String) : AppChangeEvent()
}