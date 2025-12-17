package com.kotikov.technicalTask.forDrWeb.presentation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.kotikov.technicalTask.forDrWeb.BuildConfig
import java.io.File

class FileSharer(private val context: Context) {

    fun shareFile(file: File, title: String = "Отправить отчет"): Result<Unit> {
        if (!file.exists()) {
            return Result.failure(NoSuchFileException(file))
        }

        val uri: Uri = try {
            FileProvider.getUriForFile(
                context,
                BuildConfig.FILE_PROVIDER_AUTHORITY,
                file
            )
        } catch (e: IllegalArgumentException) {
            return Result.failure(e)
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "text/html"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return try {
            if (shareIntent.resolveActivity(context.packageManager) == null) {
                return Result.failure(ActivityNotFoundException())
            }

            context.startActivity(chooserIntent)
            Result.success(Unit)
        } catch (e: ActivityNotFoundException) {
            Result.failure(e)
        } catch (e: SecurityException) {
            Result.failure(e)
        }
    }

}