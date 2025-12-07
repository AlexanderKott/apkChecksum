package com.kotikov.technicalTask.forDrWeb.domain.repositories

import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.FullAppInfo

interface GetAllInstalledAppsRepository {
    fun getFullAppList(): List<FullAppInfo>
    fun appLookup(packageName: String): FullAppInfo
}