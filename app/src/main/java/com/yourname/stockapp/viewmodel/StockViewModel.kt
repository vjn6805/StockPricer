package com.yourname.stockapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.stockapp.data.repository.Result
import com.yourname.stockapp.data.repository.StockRepository
import com.yourname.stockapp.model.StockQuote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val stock: StockQuote) : UiState()
    data class Error(val message: String) : UiState()
}

class StockViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun searchStock(symbol: String) {
        val trimmed = symbol.trim()
        if (trimmed.isEmpty()) return
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.getStockQuote(trimmed)) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }
}
