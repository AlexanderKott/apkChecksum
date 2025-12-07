package com.kotikov.technicalTask.forDrWeb.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class FileSharer(private val context: Context) {

    fun shareFile(file: File, title: String = "Отправить отчет") {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "text/html"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, title)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }
}