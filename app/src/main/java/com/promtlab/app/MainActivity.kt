package com.promtlab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.navigation.PromtLabApp
import com.promtlab.app.ui.theme.PromtLabTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: AppViewModel = viewModel()
            val settings by viewModel.settings.collectAsStateWithLifecycle()

            PromtLabTheme(settings = settings) {
                PromtLabApp(viewModel = viewModel)
            }
        }
    }
}
