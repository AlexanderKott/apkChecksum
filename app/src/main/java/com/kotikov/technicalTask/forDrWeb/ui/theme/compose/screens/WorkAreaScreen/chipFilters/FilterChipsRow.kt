package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.chipFilters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kotikov.technicalTask.forDrWeb.R
import com.kotikov.technicalTask.forDrWeb.presentation.models.AppsFilter
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme


@Composable
fun FilterChipsRow(
    selectedFilter: AppsFilter,
    onFilterSelected: (AppsFilter) -> Unit,
    onTargetsSelected: (AppsFilter) -> Unit
) {

    val filters = listOf(
        AppsFilter.ALL to stringResource(R.string.filter_all),
        AppsFilter.USER_ONLY to stringResource(R.string.filter_user),
        AppsFilter.SYSTEM_ONLY to stringResource(R.string.filter_system),
        AppsFilter.SERVICE to stringResource(R.string.filter_library),
        AppsFilter.DEBUG to stringResource(R.string.filter_debug)
    )

    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Chip(
                AppsFilter.MY_TARGETS,
                stringResource(R.string.filter_target),
                selectedFilter,
                onTargetsSelected
            )
        }

        items(filters) { (filter, label) ->
            Chip(
                filter,
                label,
                selectedFilter,
                onFilterSelected
            )
        }
    }
}

@Composable
private fun Chip(
    filter: AppsFilter,
    label: String,
    selectedFilter: AppsFilter,
    onFilterSelected: (AppsFilter) -> Unit,
) {
    AssistChip(
        onClick = {
            onFilterSelected(filter)
        },
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (filter == selectedFilter) {
                AppTheme.primaryColor
            } else {
                AppTheme.backgroundColor
            },
            labelColor = if (filter == selectedFilter) {
                AppTheme.backgroundColor
            } else {
                AppTheme.primaryColor
            }
        ),
    )
}