package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.FullAppInfo
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme

@Composable
internal fun AppCommonData(
    payload: FullAppInfo,
    onCopyClick: (String, String) -> Unit = { key, value -> }
) {

    Text(
        text = payload.appName,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        color = AppTheme.primaryColor
    )
    val modifier = Modifier.padding(horizontal = 10.dp)

    TextWithLabel("Package:", payload.packageName, modifier, onCopyClick = onCopyClick)
    TextWithLabel("Версия:", payload.versionName, modifier, onCopyClick = onCopyClick)
    TextWithLabel(
        "Код версии:",
        payload.versionCode.toString(),
        modifier,
        onCopyClick = onCopyClick
    )


}