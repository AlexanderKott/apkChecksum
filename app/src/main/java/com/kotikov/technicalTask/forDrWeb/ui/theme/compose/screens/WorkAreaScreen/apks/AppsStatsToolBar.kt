package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.apks

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme


@Composable
fun AppsStatsToolBar(appCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            fontSize = 12.sp,
            text = "Найдено: $appCount",
            color = AppTheme.textColor,
            modifier = Modifier.weight(1f)
        )
    }
}
