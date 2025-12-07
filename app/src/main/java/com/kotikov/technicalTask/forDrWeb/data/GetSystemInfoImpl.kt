package com.kotikov.technicalTask.forDrWeb.data

import android.os.Build
import com.kotikov.technicalTask.forDrWeb.domain.repositories.GetSystemInfo

object GetSystemInfoImpl : GetSystemInfo {
    override fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "Manufacturer" to Build.MANUFACTURER,
            "Model" to Build.MODEL,
            "Device" to Build.DEVICE,
            "Android Version (Release)" to Build.VERSION.RELEASE,
            "Android API Level" to Build.VERSION.SDK_INT.toString()
        )
    }
}

