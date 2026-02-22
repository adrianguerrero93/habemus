package com.habemus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import ar.habemosbar.sales.di.AppModule
import ar.habemosbar.sales.presentation.viewmodel.SalesViewModel

@Composable
fun AppWithViewModel(): Unit {
    val context = LocalContext.current
    val viewModel = remember {
        AppModule.provideSalesViewModel(context)
    }
    App(viewModel)
}
