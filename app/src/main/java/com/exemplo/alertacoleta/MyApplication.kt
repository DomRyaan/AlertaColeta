package com.exemplo.alertacoleta

import android.app.Application
import com.exemplo.alertacoleta.dataLayer.model.AppDataStoreManager
import com.exemplo.alertacoleta.dataLayer.model.Repository

class MyApplication : Application() {
    private val dataStoreManager by lazy {
        AppDataStoreManager(applicationContext)
    }

    val repository by lazy {
        Repository(dataStoreManager)
    }
}