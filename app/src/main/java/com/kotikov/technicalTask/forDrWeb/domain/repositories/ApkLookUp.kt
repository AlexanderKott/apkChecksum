package com.kotikov.technicalTask.forDrWeb.domain.repositories

import com.kotikov.technicalTask.forDrWeb.data.models.FoundAPKs

interface ApkLookUp {
    fun getApkInfo(packageName: String): Result<FoundAPKs>
}