package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.targets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.StatedTarget
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.UIStatus
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.EmptyList
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.LoadingIndicator


@Composable
fun TargetsList(
    items: List<StatedTarget>,
    uiStatus: UIStatus,
    targetsListState: LazyListState,
    onAppClick: (String) -> Unit,
    onHashClick: (String) -> Unit
) {

    if (uiStatus is UIStatus.Loading) {
        LoadingIndicator()
        return
    }

    if (items.isEmpty()) {
        EmptyList()
        return
    }

    LazyColumn(
        state = targetsListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
    ) {
        itemsIndexed(
            items,
            key = { _, app -> app.target.packageName }
        ) { index, targetElement ->
            TargetListItem(
                targetElement = targetElement,
                onInfoClick = { onAppClick(targetElement.target.packageName) },
                onHashClick = onHashClick
            )
        }
    }
}
