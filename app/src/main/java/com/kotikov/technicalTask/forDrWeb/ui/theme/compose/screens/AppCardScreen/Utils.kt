package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

    private const val REPORT_FILE_NAME = "snapshots_report.html"

fun copyToClipboard(context: Context, key: String, value: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val textToCopy = "$key $value"
    val clip = ClipData.newPlainText("Copied text", textToCopy)
    clipboardManager.setPrimaryClip(clip)
}

fun launchAppByPackageName(context: Context, packageName: String): Result<Boolean> {
    try {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)

        val intentToLaunch = launchIntent ?: Intent(Intent.ACTION_VIEW).apply {
            setPackage(packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (intentToLaunch.resolveActivity(context.packageManager) != null) {
            context.startActivity(intentToLaunch)
            return Result.success(true)
        } else {
            return Result.success(false)
        }

    } catch (e: ActivityNotFoundException) {
        Log.e("LaunchApp", "Activity not found for package: $packageName", e)
        return Result.failure(e)

    } catch (e: SecurityException) {
        Log.e("LaunchApp", "Security exception launching package: $packageName", e)
        return Result.failure(e)
    }
}



suspend fun saveReportToInternalStorage(
    context: Context,
    reportContent: String
): Result<File> = withContext(Dispatchers.IO) {
    val cacheDir = context.cacheDir
    val file = File(cacheDir, REPORT_FILE_NAME)

    try {
        file.writeText(reportContent, Charsets.UTF_8)
        Result.success(file)
    } catch (e: IOException) {
        Log.e("SaveReport", "Failed to save report to internal storage", e)
        Result.failure(e)
    }
}