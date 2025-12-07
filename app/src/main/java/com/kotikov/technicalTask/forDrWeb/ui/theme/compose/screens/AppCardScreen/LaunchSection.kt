package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme

@Composable
fun LaunchSection(
    isLaunchable: Boolean,
    modifier: Modifier = Modifier,
    onLaunchClick: () -> Unit = {}
) {
    if (isLaunchable) {
        Button(
            onClick = onLaunchClick,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(35.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.primaryColor
            )
        ) {
            Text("Запустить")
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Text(
                text = "Прямой запуск невозможен",
                color = AppTheme.textColor,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}