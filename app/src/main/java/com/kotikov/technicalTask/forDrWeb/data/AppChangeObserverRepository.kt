package com.kotikov.technicalTask.forDrWeb.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.kotikov.technicalTask.forDrWeb.data.models.AppChangeEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class AppChangeObserverRepository(private val context: Context) {
    companion object {
        private const val PACKAGE_SCHEME = "package"
    }


    @OptIn(DelicateCoroutinesApi::class)
    val appChanges: Flow<AppChangeEvent> = callbackFlow {
        val appInstallReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val packageName = intent.data?.encodedSchemeSpecificPart ?: return
                val event = when (intent.action) {
                    Intent.ACTION_PACKAGE_ADDED -> AppChangeEvent.Added(packageName)
                    Intent.ACTION_PACKAGE_REMOVED -> AppChangeEvent.Removed(packageName)
                    Intent.ACTION_PACKAGE_REPLACED -> AppChangeEvent.Changed(packageName)
                    else -> return
                }
                if (!isClosedForSend) {
                    trySend(event)
                }
            }
        }

        register(appInstallReceiver)

        awaitClose {
            context.unregisterReceiver(appInstallReceiver)
        }
    }

    private fun register(
        appInstallReceiver: BroadcastReceiver,
    ) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme(PACKAGE_SCHEME)
        }

        ContextCompat.registerReceiver(
            context,
            appInstallReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }
}