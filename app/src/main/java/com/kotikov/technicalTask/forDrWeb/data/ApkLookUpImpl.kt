package com.kotikov.technicalTask.forDrWeb.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.kotikov.technicalTask.forDrWeb.data.models.APKsInfo
import com.kotikov.technicalTask.forDrWeb.data.models.FoundAPKs
import com.kotikov.technicalTask.forDrWeb.domain.repositories.ApkLookUp

import java.io.File

class ApkLookUpImpl(context: Context) : ApkLookUp {
    private val packageManager = context.packageManager

    override fun getApkInfo(packageName: String): Result<FoundAPKs> {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)

            val baseApkFile = File(appInfo.sourceDir)
            val baseApkName = baseApkFile.name
            val baseApkPath = baseApkFile.absolutePath

            val baseApk = APKsInfo(baseApkName, baseApkPath)

            val splits = getSplitsInfo(appInfo)
            Result.success(FoundAPKs(baseApk, splits))
        } catch (e: PackageManager.NameNotFoundException) {
            Result.failure(e)
        }
    }


    private fun getSplitsInfo(app: ApplicationInfo): List<APKsInfo> {
        if (app.splitSourceDirs == null) return listOf()
        val foundSplitApk = mutableListOf<APKsInfo>()

        app.splitSourceDirs?.forEach { path ->
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