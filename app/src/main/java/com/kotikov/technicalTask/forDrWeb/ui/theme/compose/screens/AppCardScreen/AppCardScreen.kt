package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotikov.technicalTask.forDrWeb.R
import com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen.AppCardViewModel
import com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen.AppInfoResult
import com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen.UiEvent
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCardScreen(
    navController: NavController? = null,
    viewModel: AppCardViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        fontSize = 15.sp,
                        text = stringResource(R.string.caption_apk_details),
                        color = AppTheme.primaryColor
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.backgroundColor
                ),
                navigationIcon = {
                    if (navController != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = AppTheme.primaryColor
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        val appInfo by viewModel.appInfo.collectAsState()
        val appHash by viewModel.appHash.collectAsState()
        val context = LocalContext.current

        LaunchedEffect (Unit) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowToast ->
                        Toast.makeText(context, event.message,
                            Toast.LENGTH_SHORT)
                            .show()
                }
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (appInfo) {
                is AppInfoResult.Error -> {
                    item { ErrorText() }
                }

                is AppInfoResult.AppHasBeenDeleted -> {
                    item { ErrorText("Приложение было удалено с устройства") }
                }

                is AppInfoResult.Loading -> {
                    item { LoadingIndicator() }
                }

                is AppInfoResult.DataReady -> {
                    val payload = (appInfo as AppInfoResult.DataReady).data
                    item {
                        InfoCard(payload, appHash) {
                            viewModel.onLaunchAppClicked(payload.packageName)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp),
            color = AppTheme.primaryColor,
            strokeWidth = 5.dp
        )
    }
}

@Composable
fun ErrorText(errorMessage: String? = null) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage ?: "Произошла ошибка при получении данных",
            style = MaterialTheme.typography.titleLarge,
            color = AppTheme.primaryColor,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun EmptyList(text: String = "Нечего отобразить") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = AppTheme.primaryColor,
            modifier = Modifier.padding(16.dp)
        )
    }
}




