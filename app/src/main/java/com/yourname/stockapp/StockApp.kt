package com.yourname.stockapp

import android.app.Application
import com.yourname.stockapp.di.AppComponent
import com.yourname.stockapp.di.DaggerAppComponent

class StockApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().build()
    }

    companion object {
        lateinit var appComponent: AppComponent
            private set
    }
}
