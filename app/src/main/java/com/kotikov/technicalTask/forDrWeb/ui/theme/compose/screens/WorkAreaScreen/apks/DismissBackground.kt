package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.apks

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kotikov.technicalTask.forDrWeb.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.Settled -> Color.LightGray
            SwipeToDismissBoxValue.StartToEnd -> Color.Red
            SwipeToDismissBoxValue.EndToStart -> Color.Transparent
        }, label = "DismissBackground Color"
    )

    val customShape = CutCornerShape(
        topStart = 5.dp,
        topEnd = 0.dp,
        bottomStart = 5.dp,
        bottomEnd = 0.dp
    )

    Box(
        Modifier
            .padding(all = 10.dp)
            .fillMaxSize()
            .background(color, shape = customShape),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            stringResource(R.string.add_to_target_labels),
            modifier = Modifier.padding(start = 10.dp)
        )
    }

}