package com.yourname.stockapp.data.repository

import com.yourname.stockapp.data.api.GlobalQuote
import com.yourname.stockapp.data.api.StockApiService
import com.yourname.stockapp.data.api.StockResponse
import com.yourname.stockapp.data.api.SymbolMatch
import com.yourname.stockapp.data.api.SymbolSearchResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StockRepositoryTest {

    private val apiService: StockApiService = mockk()
    private val repository = StockRepository(apiService)

    @Test
    fun getStockQuote_success_returnsSuccessResult() = runTest {
        val searchResponse = SymbolSearchResponse(
            bestMatches = listOf(
                SymbolMatch(
                    symbol = "AAPL",
                    name = "Apple Inc",
                    type = "Equity",
                    region = "United States"
                )
            ),
            errorMessage = null,
            note = null,
            information = null
        )
        val quoteResponse = StockResponse(
            globalQuote = GlobalQuote(
                symbol = "AAPL",
                price = "150.25",
                change = "0.75",
                changePercent = "0.50%"
            ),
            errorMessage = null,
            note = null,
            information = null
        )
        coEvery { apiService.searchSymbols(keywords = "apple", apiKey = any()) } returns searchResponse
        coEvery { apiService.getGlobalQuote(symbol = "AAPL", apiKey = any()) } returns quoteResponse

        val result = repository.getStockQuote("apple")

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals("AAPL", data.symbol)
        assertEquals("150.25", data.price)
        assertEquals("0.50%", data.changePercent)
        assertEquals("Apple Inc", data.companyName)
    }

    @Test
    fun getStockQuote_exception_returnsErrorResult() = runTest {
        coEvery { apiService.searchSymbols(keywords = "AAPL", apiKey = any()) } throws RuntimeException("Network error")

        val result = repository.getStockQuote("AAPL")

        assertTrue(result is Result.Error)
        assertEquals("Network error", (result as Result.Error).message)
    }
}
