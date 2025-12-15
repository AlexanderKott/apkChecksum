package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen.ApkDetails

@Composable
internal fun ApkInfoAndHash(
    apkHashResponse: ApkDetails,
    onCopyClick: (String, String) -> Unit = { key, value -> }
) {
    if (apkHashResponse is ApkDetails.Loading) {
        LoadingIndicator()
    }



    if (apkHashResponse is ApkDetails.Error) {
        val errorText = apkHashResponse.errorMessage

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(errorText)
        }
    }

    if (apkHashResponse is ApkDetails.Success) {
        ApkItemCard(
            title = "Base APK",
            apkInfo = apkHashResponse.payload.apkInfo.baseAPK,
            hash = apkHashResponse.payload.hash,
            onCopyClick = onCopyClick
        )

        if (apkHashResponse.payload.apkInfo.splitApk.isEmpty()) {
            Text(
                text = "Сплит APK не найдены \nдля этого приложения",
                fontSize = 14.sp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 22.dp
                )
            )
            return
        }

        apkHashResponse.payload.apkInfo.splitApk.let { splitApks ->
            Text(
                text = "Split APKs:",
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            splitApks.forEachIndexed { i, splitApk ->
                ApkItemCard(
                    title = "${i + 1} split APK",
                    apkInfo = splitApk,
                    onCopyClick = onCopyClick
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}