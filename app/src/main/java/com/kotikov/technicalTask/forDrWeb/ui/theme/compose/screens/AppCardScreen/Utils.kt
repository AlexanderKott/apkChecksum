package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

fun copyToClipboard(context: Context, key: String, value: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val textToCopy = "$key $value"
    val clip = ClipData.newPlainText("Copied text", textToCopy)
    clipboardManager.setPrimaryClip(clip)
}

fun launchAppByPackageName(context: Context, packageName: String): Boolean {
    return try {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            context.startActivity(intent)
            true
        } else {
            val altIntent = Intent(Intent.ACTION_VIEW).apply {
                setPackage(packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (altIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(altIntent)
                true
            } else {
                false
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}