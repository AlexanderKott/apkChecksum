package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotikov.technicalTask.forDrWeb.data.models.APKsInfo
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme

@Composable
fun ApkItemCard(
    modifier: Modifier = Modifier,
    title: String,
    apkInfo: APKsInfo,
    hash: String = "в этой версии недоступно",
    onCopyClick: (String, String) -> Unit = { _, _ -> }
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.zebraEven
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 3.dp),
                color = Color.Black
            )

            TextWithLabel("Имя файла:", apkInfo.apkName, onCopyClick = onCopyClick)
            TextWithLabel("Путь:", apkInfo.apkPath, onCopyClick = onCopyClick)
            TextWithLabel("Hash SHA-256:", hash, onCopyClick = onCopyClick)
        }
    }
}