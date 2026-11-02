package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.VendorListTheme
import com.example.ui.viewmodel.VendorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: VendorViewModel = viewModel()
            val isCustomDark by viewModel.isDarkMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val useDark = isCustomDark || systemDark

            VendorListTheme(darkTheme = useDark) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}
