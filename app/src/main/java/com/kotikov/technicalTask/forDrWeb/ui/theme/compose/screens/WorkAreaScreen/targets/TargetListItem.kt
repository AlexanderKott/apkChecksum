package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.targets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.CheckingVerdict
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.StatedTarget
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme


@Composable
fun TargetListItem(
    targetElement: StatedTarget,
    onInfoClick: () -> Unit,
    onHashClick: (String) -> Unit
) {

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(

            containerColor = when (targetElement.hashAssertResult) {
                CheckingVerdict.NEW -> AppTheme.newColor
                CheckingVerdict.MATCH -> AppTheme.matchColor
                CheckingVerdict.DIFFERENT -> AppTheme.differentColor
                CheckingVerdict.MISSING_DATA -> AppTheme.newColor
            }
        ),
        onClick = onInfoClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val painter: Painter = BitmapPainter(
                targetElement
                    .target
                    .icon
                    .asImageBitmap()
            )

            Spacer(modifier = Modifier.width(4.dp))
            Image(
                painter = painter,
                contentDescription = "App Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = targetElement.target.name,
                        color = AppTheme.textColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = targetElement.target.packageName,
                        color = AppTheme.textColor.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                HashButton(targetElement.target.packageName, onHashClick)
            }
        }
    }
}

@Composable
private fun HashButton(
    packageName: String,
    onHashClick: (String) -> Unit,
) {

    val borderColor = Color.DarkGray
    val cutCornerShape = CutCornerShape(
        topStart = 8.dp,
        bottomEnd = 8.dp
    )


    OutlinedButton(
        modifier = Modifier.padding(end = 10.dp),
        onClick = { onHashClick(packageName) },
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = borderColor,
            containerColor = Color.White
        ),
        shape = cutCornerShape,
        border = BorderStroke(width = 1.dp, color = borderColor)
    ) {
        Text(
            color = Color.Black,
            text = "hash"
        )
    }
}

