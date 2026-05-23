package com.groupe9.streamvod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.groupe9.streamvod.ui.navigation.AppNavigation
import com.groupe9.streamvod.ui.theme.StreamVODTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StreamVODTheme {
                AppNavigation()
            }
        }
    }
}