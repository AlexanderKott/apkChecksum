package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.apks

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.UIStatus
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.EmptyList
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.LoadingIndicator
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
@Composable
fun APKsList(
    modifier: Modifier = Modifier,
    items: UIStatus,
    listState: LazyListState,
    onRefresh: () -> Unit,
    onAppClick: (String) -> Unit,
    onMarkAsTarget: (String) -> Unit,
) {

    var isRefreshing by remember { mutableStateOf(false) }


    val configuration = LocalConfiguration.current
    val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val coroutineScope = rememberCoroutineScope()

    if (isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    fun refreshData() {
        coroutineScope.launch {
            isRefreshing = true
            onRefresh()
            isRefreshing = false
        }
    }

    if (items is UIStatus.Loading) {
        LoadingIndicator()
        return
    }

    if (items is UIStatus.EmptyList) {
        EmptyList()
        return
    }

    val elements = (items as UIStatus.Ready).payload

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { refreshData() },
        modifier = modifier
    ) {
        var isMarked by remember { mutableStateOf(false) }
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            itemsIndexed(
                elements,
                key = { _, app -> app.packageName }
            ) { index, app ->


            val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        if (dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                            if (!isMarked) {
                                onMarkAsTarget(app.packageName)
                                isMarked = true
                            }
                        } else
                            if (dismissValue == SwipeToDismissBoxValue.Settled) {
                                isMarked = false
                            }
                        false
                    },
                    positionalThreshold = { totalDistance ->
                        totalDistance * 0.8f
                    }
                )
                val bgColor = if (index % 2 == 0) AppTheme.zebraEven
                else AppTheme.zebraOdd

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = true,
                    enableDismissFromEndToStart = false,
                    backgroundContent = {
                        DismissBackground(dismissState)
                    },
                ) {
                    ApkListItem(
                        landscape = landscape,
                        app = app,
                        backgroundColor = bgColor,
                        onClick = { onAppClick(app.packageName) }
                    )
                }
            }
        }
    }
}
