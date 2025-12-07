package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import com.kotikov.technicalTask.forDrWeb.R
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorScreen(navController: NavController? = null) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        fontSize = 15.sp,
                        text = "Об авторе",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.backgroundColor
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Информация об авторе",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppTheme.primaryColor
                    )

                    HtmlText(stringResource(R.string.about_text))

                }
            }
        }
    }
}

@Composable
fun HtmlText(html: String) {
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(
                html,
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )
        }
    )
}