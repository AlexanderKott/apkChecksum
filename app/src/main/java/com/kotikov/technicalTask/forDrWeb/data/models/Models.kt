package com.kotikov.technicalTask.forDrWeb.data.models

import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.StatedTarget

data class APKsInfo(
    val apkName: String,
    val apkPath: String,
)

data class FoundAPKs(
    val baseAPK: APKsInfo,
    val splitApk: List<APKsInfo>
)


sealed class AppChangeEvent {
    abstract val packageName: String

    data class Added(override val packageName: String) : AppChangeEvent()
    data class Removed(override val packageName: String) : AppChangeEvent()
    data class Changed(override val packageName: String) : AppChangeEvent()
}


sealed class TargetsResult {
    data class Found(val target: StatedTarget) : TargetsResult()
    object NotFound : TargetsResult()
}