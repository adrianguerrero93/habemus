package com.habemus

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ar.habemosbar.sales.presentation.screen.SalesCalculatorScreen
import ar.habemosbar.sales.presentation.viewmodel.SalesViewModel
import com.habemus.ui.HabemosBarTheme

@Composable
fun App(viewModel: SalesViewModel) {
    HabemosBarTheme {
        SalesCalculatorScreen(viewModel)
    }
}