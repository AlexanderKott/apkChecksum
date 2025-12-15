package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen.ApkDetails
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.FullAppInfo
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme

@Composable
internal fun InfoCard(
    payload: FullAppInfo,
    appHash: ApkDetails,
    onLaunchClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.backgroundColor
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {

        val context = LocalContext.current
        val copyAction: (String, String) -> Unit = { key, value ->
            copyToClipboard(context, key, value)
            Toast.makeText(context, "Скопировано", Toast.LENGTH_SHORT).show()
        }

        AppCommonData(payload, copyAction)
        LaunchSection(payload.canBeLaunched, onLaunchClick = onLaunchClick)
        Spacer(Modifier.height(8.dp))
        ApkInfoAndHash(appHash, copyAction)
    }
}