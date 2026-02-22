package ar.habemosbar.sales.di

import android.content.Context
import ar.habemosbar.sales.data.local.db.SimpleProductDatabase
import ar.habemosbar.sales.data.local.datasource.LocalProductDataSource
import ar.habemosbar.sales.data.local.repository.ProductRepositoryImpl
import ar.habemosbar.sales.presentation.viewmodel.SalesViewModel

object AppModule {
    fun provideProductRepository(context: Context): ProductRepositoryImpl {
        val database = SimpleProductDatabase(context)
        val localDataSource = LocalProductDataSource(database)
        return ProductRepositoryImpl(localDataSource)
    }

    fun provideSalesViewModel(context: Context): SalesViewModel {
        val repository = provideProductRepository(context)
        return SalesViewModel(repository)
    }
}


