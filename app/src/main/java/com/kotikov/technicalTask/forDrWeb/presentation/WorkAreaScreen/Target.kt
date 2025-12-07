package com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen

import android.graphics.Bitmap

data class Target(
    val name: String,
    val apkPath: String,
    val packageName: String,
    val icon: Bitmap,
    val referenceHash: String? = null,
    val referenceHashTimeStamp: Long = 0L,
    val actualHash: String? = null,
    val actualHashTimeStamp: Long = 0L,
)

enum class CheckingVerdict {
    NEW, MATCH, DIFFERENT, MISSING_DATA
}

