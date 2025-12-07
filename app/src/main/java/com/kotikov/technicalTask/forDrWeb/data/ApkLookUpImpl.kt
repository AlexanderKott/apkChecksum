package com.kotikov.technicalTask.forDrWeb.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.kotikov.technicalTask.forDrWeb.data.models.APKLookUpResult
import com.kotikov.technicalTask.forDrWeb.data.models.APKsInfo
import com.kotikov.technicalTask.forDrWeb.domain.repositories.ApkLookUp

import java.io.File

class ApkLookUpImpl(context: Context) : ApkLookUp {
    private val packageManager = context.packageManager

    override fun getApkInfo(packageName: String): APKLookUpResult {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)

            val baseApkFile = File(appInfo.sourceDir)
            val baseApkName = baseApkFile.name
            val baseApkPath = baseApkFile.absolutePath

            val baseApk = APKsInfo(baseApkName, baseApkPath)

            val splits = getSplitsInfo(appInfo)
            APKLookUpResult.FoundAPKs(baseApk, splits)
        } catch (_: PackageManager.NameNotFoundException) {
            APKLookUpResult.Error
        }
    }


    private fun getSplitsInfo(app: ApplicationInfo): List<APKsInfo>? {
        if (app.splitSourceDirs == null) return null
        val foundSplitApk = mutableListOf<APKsInfo>()

        app.splitSourceDirs?.forEachIndexed { i, path ->
            val splitApk = File(path)
            foundSplitApk.add(
                APKsInfo(
                    splitApk.name,
                    splitApk.absolutePath
                )
            )
        }
        return foundSplitApk
    }

}