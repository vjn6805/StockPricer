package com.yourname.stockapp.di

import com.yourname.stockapp.viewmodel.StockViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, RepositoryModule::class])
interface AppComponent {
    fun getStockViewModel(): StockViewModel
}
