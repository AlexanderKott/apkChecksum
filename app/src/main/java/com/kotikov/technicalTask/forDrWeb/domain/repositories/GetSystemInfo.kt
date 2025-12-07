package com.kotikov.technicalTask.forDrWeb.domain.repositories

interface GetSystemInfo {
    fun getDeviceInfo(): Map<String, String>
}