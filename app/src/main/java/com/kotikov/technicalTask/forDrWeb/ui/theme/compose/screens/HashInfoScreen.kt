package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotikov.technicalTask.forDrWeb.R
import com.kotikov.technicalTask.forDrWeb.presentation.HashInfoViewModel.HashCard
import com.kotikov.technicalTask.forDrWeb.presentation.HashInfoViewModel.HashInfoViewModel
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.CheckingVerdict
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.StatedTarget
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.ErrorText
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.LoadingIndicator
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.TextWithLabel
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.copyToClipboard
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.targets.convertMillisToDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HashInfoScreen(
    navController: NavController? = null,
    viewModel: HashInfoViewModel = viewModel()

) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        fontSize = 15.sp,
                        text = stringResource(R.string.caption_hash_info),
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
                                contentDescription = null,
                                tint = AppTheme.primaryColor
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val appHash by viewModel.appHash.collectAsState()
        val context = LocalContext.current

        val copyAction: (String, String) -> Unit = { key, value ->
            copyToClipboard(context, key, value)
            Toast.makeText(
                context, context.getString(R.string.copied_toast),
                Toast.LENGTH_SHORT
            ).show()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            if (appHash is HashCard.Error) {
                ErrorText()
                return@Column
            }

            if (appHash is HashCard.Loading) {
                LoadingIndicator()
                return@Column
            }

            if (appHash is HashCard.DoTakeSnapshot) {
                SnapshotNotice()
                return@Column
            }

            DisplayCard(appHash, copyAction)
        }
    }
}

@Composable
private fun DisplayCard(
    appHash: HashCard,
    copyAction: (String, String) -> Unit
) {
    val info = (appHash as HashCard.Success).payload

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.backgroundColor
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Text(
            text = info.target.packageName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = AppTheme.primaryColor
        )

        val modifier = Modifier.padding(horizontal = 10.dp)

        val refHash = info.target.referenceHash ?: stringResource(R.string.label_no_data_stub)
        val actualHash = info.target.actualHash ?: stringResource(R.string.label_no_data_stub)

        TextWithLabel(
            stringResource(R.string.label_reference_hash),
            refHash,
            modifier,
            copyAction
        )

        TextWithLabel(
            stringResource(R.string.label_reference_hash_time),
            convertMillisToDateTime(
                info
                    .target
                    .referenceHashTimeStamp
            ),
            modifier,
            copyAction
        )


        Spacer(modifier.height(30.dp))

        TextWithLabel(
            stringResource(R.string.label_actual_hash), actualHash,
            modifier,
            copyAction
        )


        TextWithLabel(
            stringResource(R.string.label_actual_hash_time),
            convertMillisToDateTime(
                info
                    .target
                    .actualHashTimeStamp
            ),
            modifier,
            copyAction
        )
        Verdict(info)
    }
}

@Composable
private fun Verdict(info: StatedTarget) {
    val resultText = when (info.hashAssertResult) {
        CheckingVerdict.NEW,
        CheckingVerdict.MISSING_DATA -> stringResource(R.string.label_extra_one_snapshot_needed)

        CheckingVerdict.DIFFERENT -> stringResource(R.string.label_verdict_hashes_different)
        CheckingVerdict.MATCH -> stringResource(R.string.label_verdict_hashes_match)
    }

    Text(
        text = stringResource(R.string.label_result, resultText),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp),
        color = Color.Black
    )
}

@Composable
fun SnapshotNotice() {
    Text(
        modifier = Modifier.padding(20.dp),
        text = stringResource(R.string.advice_go_back_and_take_snaphot),
        style = MaterialTheme.typography.titleLarge,
        color = AppTheme.primaryColor
    )
}