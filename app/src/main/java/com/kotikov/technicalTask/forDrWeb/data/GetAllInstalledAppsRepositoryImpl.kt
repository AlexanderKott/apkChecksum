package com.kotikov.technicalTask.forDrWeb.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.TransactionTooLargeException
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.kotikov.technicalTask.forDrWeb.domain.repositories.GetAllInstalledAppsRepository
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.FullAppInfo


class GetAllInstalledAppsRepositoryImpl(context: Context) : GetAllInstalledAppsRepository {

    private val packageManager = context.packageManager

    @SuppressLint("QueryPermissionsNeeded")
    override fun getFullAppList(): Result<List<FullAppInfo>> {
        return try {
            val applications = packageManager.getInstalledApplications(
                PackageManager.GET_META_DATA
            )

            val installedAppsList = mutableListOf<FullAppInfo>()

            for (app in applications) {
                val info = getInfoAboutApp(app, packageManager)
                installedAppsList.add(info)
            }

            Result.success(installedAppsList.sortedBy { it.appName })

        } catch (e: TransactionTooLargeException) {
            Result.failure(e)
        } catch (e: android.os.RemoteException) {
            Result.failure(e)
        } catch (e: PackageManager.NameNotFoundException) {
            Result.failure(e)
        } catch (e: android.content.res.Resources.NotFoundException) {
            Result.failure(e)
        } catch (e: IllegalStateException) {
            Result.failure(e)
        }
    }

    override fun appLookup(packageName: String): Result<FullAppInfo> {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val result = getInfoAboutApp(appInfo, packageManager)
           Result.success(result)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppRepo", "Lookup failed for package: $packageName", e)
            Result.failure(e)
        }
    }


    private fun getInfoAboutApp(
        app: ApplicationInfo,
        packageManager: PackageManager,
    ): FullAppInfo {
        val appName = app.loadLabel(packageManager).toString()
        val packageName = app.packageName
        val isService = appName == packageName
        val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val isDebuggable = (app.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val isLaunchable = isAppLaunchable(packageManager, packageName)
        val icon = app.loadIcon(packageManager).toBitmap()
        val (versionCode, versionName) = getVersion(packageManager, packageName)

        return FullAppInfo(
            appName = appName,
            packageName = packageName,
            isSystemApp = isSystem,
            canBeLaunched = isLaunchable,
            isDebuggable = isDebuggable,
            icon = icon,
            versionName = versionName,
            versionCode = versionCode,
            isTechnicalName = isService
        )
    }


    private fun isAppLaunchable(packageManager: PackageManager, packageName: String): Boolean {
        val launchIntent: Intent? = packageManager.getLaunchIntentForPackage(packageName)
        return launchIntent != null
    }


    private fun getVersion(
        packageManager: PackageManager?,
        packageName: String,
    ): Pair<Long, String> {
        var versionName = "N/A"
        var versionCode: Long? = 0L
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager?.getPackageInfo(
                    packageName, PackageManager
                        .PackageInfoFlags.of(0L)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager?.getPackageInfo(packageName, 0)
            }

            versionName = packageInfo?.versionName ?: "N/A"
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo?.versionCode?.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppInfoLog", "Package not found: $packageName", e)
        }
        return Pair(versionCode ?: 0L, versionName)
    }
}