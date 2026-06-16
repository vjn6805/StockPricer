package com.yourname.stockapp.model

data class StockQuote(
    val symbol: String,
    val price: String,
    val change: String,
    val changePercent: String,
    val companyName: String
)
