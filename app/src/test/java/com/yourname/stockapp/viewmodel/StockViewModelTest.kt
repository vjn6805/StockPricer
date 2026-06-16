package com.yourname.stockapp.viewmodel

import app.cash.turbine.test
import com.yourname.stockapp.data.repository.Result
import com.yourname.stockapp.data.repository.StockRepository
import com.yourname.stockapp.model.StockQuote
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModelTest {

    private val repository: StockRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: StockViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = StockViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_state_is_idle() = runTest(testDispatcher) {
        assertEquals(UiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun searchStock_success_emitsLoadingThenSuccess() = runTest(testDispatcher) {
        val stock = StockQuote("AAPL", "150.25", "0.75", "0.50%", "AAPL")
        coEvery { repository.getStockQuote("AAPL") } returns Result.Success(stock)

        viewModel.uiState.test {
            assertEquals(UiState.Idle, awaitItem())
            viewModel.searchStock("AAPL")
            assertEquals(UiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(UiState.Success(stock), awaitItem())
        }
    }

    @Test
    fun searchStock_failure_emitsLoadingThenError() = runTest(testDispatcher) {
        coEvery { repository.getStockQuote("AAPL") } returns Result.Error("Error message")

        viewModel.uiState.test {
            assertEquals(UiState.Idle, awaitItem())
            viewModel.searchStock("AAPL")
            assertEquals(UiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(UiState.Error("Error message"), awaitItem())
        }
    }
}
