package com.yourname.stockapp.data.repository

import com.yourname.stockapp.data.api.StockApiService
import com.yourname.stockapp.data.api.StockResponse
import com.yourname.stockapp.data.api.SymbolSearchResponse
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
    suspend fun getStockQuote(query: String): Result<StockQuote> {
        return try {
            val searchResponse = apiService.searchSymbols(keywords = query, apiKey = API_KEY)
            val searchError = apiError(searchResponse.errorMessage, searchResponse.note, searchResponse.information)
            if (searchError != null) return Result.Error(searchError)

            val match = searchResponse.bestMatches
                ?.firstOrNull { it.type?.contains("Equity", ignoreCase = true) == true && it.region == "United States" }
                ?: searchResponse.bestMatches?.firstOrNull()

            if (match?.symbol == null) {
                return Result.Error("No company found for \"$query\".")
            }

            val response = apiService.getGlobalQuote(symbol = match.symbol, apiKey = API_KEY)
            val quoteError = apiError(response.errorMessage, response.note, response.information)
            if (quoteError != null) return Result.Error(quoteError)

            val quote = response.globalQuote
            if (quote != null && quote.symbol != null) {
                Result.Success(
                    StockQuote(
                        symbol = quote.symbol,
                        price = quote.price ?: "0.0",
                        change = quote.change ?: "0.0",
                        changePercent = quote.changePercent ?: "0.0%",
                        companyName = match.name ?: quote.symbol
                    )
                )
            } else {
                Result.Error("Could not load price for ${match.name ?: match.symbol}.")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unexpected error occurred.")
        }
    }

    private fun apiError(errorMessage: String?, note: String?, information: String?): String? {
        if (errorMessage != null) return errorMessage
        if (note != null) return "API call frequency limit reached. Please try again later."
        if (information != null) return information
        return null
    }

    companion object {
        private const val API_KEY = "NP6BNTWEIYUZISYL"
    }
}
