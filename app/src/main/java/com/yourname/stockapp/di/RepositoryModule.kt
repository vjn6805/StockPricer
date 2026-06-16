package com.yourname.stockapp.di

import com.yourname.stockapp.data.api.StockApiService
import com.yourname.stockapp.data.repository.StockRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideStockRepository(apiService: StockApiService): StockRepository {
        return StockRepository(apiService)
    }
}
