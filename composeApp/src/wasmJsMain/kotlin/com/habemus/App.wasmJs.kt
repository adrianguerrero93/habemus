package com.habemus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ar.habemosbar.sales.data.local.repository.ProductRepositoryImpl
import ar.habemosbar.sales.presentation.viewmodel.SalesViewModel

@Composable
fun AppWithViewModel() {
    val viewModel = remember {
        SalesViewModel(ProductRepositoryImpl())
    }
    App(viewModel)
}
