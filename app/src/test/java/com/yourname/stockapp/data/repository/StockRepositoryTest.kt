package com.yourname.stockapp.data.repository

import com.yourname.stockapp.data.api.GlobalQuote
import com.yourname.stockapp.data.api.StockApiService
import com.yourname.stockapp.data.api.StockResponse
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
        val response = StockResponse(
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
        coEvery { apiService.getGlobalQuote(symbol = "AAPL", apiKey = any()) } returns response

        val result = repository.getStockQuote("AAPL")

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals("AAPL", data.symbol)
        assertEquals("150.25", data.price)
        assertEquals("0.50%", data.changePercent)
        assertEquals("AAPL", data.companyName)
    }

    @Test
    fun getStockQuote_exception_returnsErrorResult() = runTest {
        coEvery { apiService.getGlobalQuote(symbol = "AAPL", apiKey = any()) } throws RuntimeException("Network error")

        val result = repository.getStockQuote("AAPL")

        assertTrue(result is Result.Error)
        assertEquals("Network error", (result as Result.Error).message)
    }
}
