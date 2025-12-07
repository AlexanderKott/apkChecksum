package com.kotikov.technicalTask.forDrWeb.domain.repositories

import com.kotikov.technicalTask.forDrWeb.data.models.APKLookUpResult

interface ApkLookUp {
    fun getApkInfo(packageName: String): APKLookUpResult
}