package com.yourname.stockapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yourname.stockapp.viewmodel.StockViewModel
import com.yourname.stockapp.viewmodel.UiState

@Composable
fun StockScreen(viewModel: StockViewModel, modifier: Modifier = Modifier) {
    var symbol by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = symbol,
                onValueChange = { symbol = it },
                label = { Text("Ticker") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.searchStock(symbol) }
            ) {
                Text("Search")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (val state = uiState) {
            is UiState.Idle -> {}
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Company: ${state.stock.companyName}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Price: ${state.stock.price}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Change: ${state.stock.changePercent}")
                    }
                }
            }
            is UiState.Error -> {
                Text(state.message)
            }
        }
    }
}
