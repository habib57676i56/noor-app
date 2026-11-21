package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.navigation.NoorAppShell
import com.example.ui.theme.NoorTheme
import com.example.viewmodel.NoorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NoorViewModel = viewModel()
            val prefs by viewModel.preferences.collectAsStateWithLifecycle()
            val layoutDirection = if (prefs.language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(
                LocalAppLanguage provides prefs.language,
                LocalLayoutDirection provides layoutDirection
            ) {
                NoorTheme(themeMode = prefs.themeMode) {
                    NoorAppShell(viewModel = viewModel)
                }
            }
        }
    }
}
