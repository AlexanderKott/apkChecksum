package com.kotikov.technicalTask.forDrWeb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kotikov.technicalTask.forDrWeb.ui.theme.AppTheme
import com.kotikov.technicalTask.forDrWeb.ui.theme.TaskApkChecksumTheme
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.navigation.NavGraph


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskApkChecksumTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppTheme.backgroundColor
                ) {
                    NavGraph()
                }
            }
        }
    }
}






