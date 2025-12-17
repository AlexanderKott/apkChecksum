package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotikov.technicalTask.forDrWeb.R
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.UIStatus
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.WorkAreaViewModel
import com.kotikov.technicalTask.forDrWeb.presentation.models.AppsFilter
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.ErrorText
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.apks.APKsList
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.apks.AppsStatsToolBar
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.chipFilters.FilterChipsRow
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.targets.TargetsList
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.targets.TargetsStatsToolBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkAreaScreen(
    onAppClick: (String) -> Unit,
    onAuthorClick: () -> Unit,
    onHashClick: (String) -> Unit,
    viewModel: WorkAreaViewModel = viewModel()
) {
    val apks by viewModel.apksList.collectAsState()
    val targets by viewModel.targets.collectAsState()

    var selectedFilter by rememberSaveable { mutableStateOf(AppsFilter.ALL) }
    var snapshotMode by rememberSaveable { mutableStateOf(false) }
    val apksListState = rememberLazyListState()
    val targetsListState = rememberLazyListState()

    val context: Context = LocalContext.current

    LaunchedEffect(selectedFilter) {
        viewModel.filter(selectedFilter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_task_alt_24),
                            contentDescription = null,
                            tint = AppTheme.primaryColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.main_screen_title),
                                color = AppTheme.primaryColor
                            )
                            Text(
                                text = stringResource(R.string.main_screen_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.textColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.backgroundColor
                ),
                actions = {
                    IconButton(onClick = onAuthorClick) {
                        Text(
                            text = stringResource(R.string.main_screen_info_icon),
                            style = MaterialTheme.typography.titleLarge,
                            color = AppTheme.primaryColor
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                if (apks is UIStatus.Error) {
                    val messageID = (apks as UIStatus.Error).messageResID
                    ErrorText(stringResource(messageID))

                } else {
                    if (selectedFilter == AppsFilter.MY_TARGETS) {
                        TargetsList(
                            targetsListState = targetsListState,
                            items = targets,
                            uiStatus = apks,
                            onAppClick = onAppClick,
                            onHashClick = onHashClick
                        )

                    } else {
                        APKsList(
                            items = apks,
                            listState = apksListState,
                            onRefresh = { viewModel.refresh() },
                            onAppClick = onAppClick,
                            modifier = Modifier
                                .fillMaxWidth(),
                            onMarkAsTarget = { apkId ->
                                viewModel.markApkAsTarget(apkId)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.toast_apk_now_in_targets),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }

            if (selectedFilter == AppsFilter.MY_TARGETS) {
                TargetsStatsToolBar(
                    targets.size,
                    onTakeSnapshot = {
                        viewModel.recordTargetsSnapshot()
                        snapshotMode = true
                    },
                    onDeleteTargetsSet = {
                        viewModel.deleteTargets()
                        snapshotMode = false
                    },
                    onShareTargetsSet = {
                        viewModel.prepareReport()
                    },
                )
            } else {
                if (apks is UIStatus.Ready) {
                    AppsStatsToolBar((apks as UIStatus.Ready).payload.size)
                }
            }

            if (!snapshotMode) {
                FilterChipsRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    onTargetsSelected = { selectedFilter = it }
                )
            } else {
                Spacer(modifier = Modifier.height(63.dp))
            }

        }
    }
}
