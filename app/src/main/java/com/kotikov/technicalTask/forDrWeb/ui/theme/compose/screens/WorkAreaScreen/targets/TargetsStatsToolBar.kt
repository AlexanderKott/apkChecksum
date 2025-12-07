package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.targets

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotikov.technicalTask.forDrWeb.R
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme


@Composable
fun TargetsStatsToolBar(
    targetsCount: Int,
    onTakeSnapshot: () -> Unit,
    onDeleteTargetsSet: () -> Unit,
    onShareTargetsSet: () -> Unit,
) {

    val context: Context = LocalContext.current
    var snapshotTaken by rememberSaveable { mutableStateOf(false) }
    var time by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            fontSize = 12.sp,
            text = stringResource(R.string.targets, targetsCount),
            color = AppTheme.textColor,
        )

        Spacer(modifier = Modifier.height(15.dp))

        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = null,
            tint = AppTheme.primaryColor,
            modifier = Modifier
                .size(50.dp)
                .clickable(onClick = {
                    if (targetsCount > 0) {
                        onTakeSnapshot()
                        snapshotTaken = true
                        time = convertMillisToTime(System.currentTimeMillis())
                    } else {
                        Toast.makeText(context, "Сначала добавте цели", Toast.LENGTH_SHORT).show()
                    }
                })
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (snapshotTaken == true) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = null,
                tint = AppTheme.primaryColor,
                modifier = Modifier
                    .size(50.dp)
                    .clickable(onClick = {
                        onDeleteTargetsSet()
                        snapshotTaken = false
                    })
            )


            Text(
                fontSize = 12.sp,
                text = context.getString(R.string.snapshot, time),
                color = AppTheme.textColor,
                modifier = Modifier
                    .weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = AppTheme.primaryColor,
                modifier = Modifier
                    .size(50.dp)
                    .clickable(onClick = onShareTargetsSet)
            )
        }
    }
}
