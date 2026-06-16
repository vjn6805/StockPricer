package com.yourname.stockapp.data.repository

import com.yourname.stockapp.data.api.StockApiService
import com.yourname.stockapp.model.StockQuote
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

@Singleton
class StockRepository @Inject constructor(
    private val apiService: StockApiService
) {
    suspend fun getStockQuote(symbol: String): Result<StockQuote> {
        return try {
            val response = apiService.getGlobalQuote(symbol = symbol, apiKey = "DKTQ66QTDFKFYX7M")
            val quote = response.globalQuote
            if (quote != null && quote.symbol != null) {
                Result.Success(
                    StockQuote(
                        symbol = quote.symbol,
                        price = quote.price ?: "0.0",
                        change = quote.change ?: "0.0",
                        changePercent = quote.changePercent ?: "0.0%",
                        companyName = quote.symbol
                    )
                )
            } else if (response.errorMessage != null) {
                Result.Error(response.errorMessage)
            } else if (response.note != null) {
                Result.Error("API call frequency limit reached. Please try again later.")
            } else if (response.information != null) {
                Result.Error(response.information)
            } else {
                Result.Error("Symbol not found or empty response.")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unexpected error occurred.")
        }
    }

    companion object {
        private const val API_KEY = "demo"
    }
}
