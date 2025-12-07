package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.apks

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kotikov.technicalTask.forDrWeb.R
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.FullAppInfo
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme


@Composable
fun ApkListItem(
    landscape: Boolean,
    app: FullAppInfo,
    backgroundColor: Color,
    onClick: () -> Unit
) {

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(start = 2.dp, end = 2.dp, bottom = 3.dp)
            .clickable { onClick() },

        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                val painter: Painter = BitmapPainter(app.icon.asImageBitmap())

                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painter,
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        color = AppTheme.textColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = app.packageName,
                        color = AppTheme.textColor.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (landscape) {
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = "v${app.versionName}   (${app.versionCode})",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Category(app)
            }
        }

    }
}


@Composable
private fun Category(app: FullAppInfo) {
    Text(
        text = stringResource(R.string.category_label),
        style = MaterialTheme.typography.bodySmall,
        color = Color.Gray,
    )

    if (!app.isSystemApp && !app.isTechnicalName) {
        Text(
            text = stringResource(R.string.category_user),
            color = Color.Blue,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.width(3.dp))
    } else if (app.isTechnicalName && app.packageName.endsWith(".overlay")) {
        Text(
            text = stringResource(R.string.category_overlay),
            color = Color.Blue,
            style = MaterialTheme.typography.labelSmall
        )
    } else if (app.isTechnicalName) {
        Text(
            text = stringResource(R.string.category_library),
            color = Color.Blue,
            style = MaterialTheme.typography.labelSmall
        )
    } else {
        Text(
            text = stringResource(R.string.category_system),
            color = Color.Red,
            style = MaterialTheme.typography.labelSmall
        )
    }

    if (app.isDebuggable) {
        Text(
            text = stringResource(R.string.category_debug),
            color = Color.Magenta,
            style = MaterialTheme.typography.labelSmall
        )
    }

    Spacer(modifier = Modifier.width(3.dp))
}

