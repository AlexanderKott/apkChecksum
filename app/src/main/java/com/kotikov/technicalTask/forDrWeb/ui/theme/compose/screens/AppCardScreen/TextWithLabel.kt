package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme

@Composable
fun TextWithLabel(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onCopyClick: ((key: String, value: String) -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                softWrap = true,
                maxLines = Int.MAX_VALUE,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 2.dp, end = if (onCopyClick != null) 8.dp else 0.dp)
            )

            if (onCopyClick != null) {
                IconButton(
                    onClick = { onCopyClick(label, value) },
                    modifier = Modifier.size(15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Копировать",
                        tint = AppTheme.primaryColor
                    )
                }
            }
        }
    }
}
