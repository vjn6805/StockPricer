package com.yourname.stockapp.data.api

import com.google.gson.annotations.SerializedName

data class StockResponse(
    @SerializedName("Global Quote") val globalQuote: GlobalQuote?,
    @SerializedName("Error Message") val errorMessage: String?,
    @SerializedName("Note") val note: String?,
    @SerializedName("Information") val information: String?
)

data class GlobalQuote(
    @SerializedName("01. symbol") val symbol: String?,
    @SerializedName("05. price") val price: String?,
    @SerializedName("09. change") val change: String?,
    @SerializedName("10. change percent") val changePercent: String?
)
