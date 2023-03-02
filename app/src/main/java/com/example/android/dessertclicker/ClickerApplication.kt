package com.example.android.dessertclicker

import android.app.Application
import timber.log.Timber

class ClickerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Dòng mã này khởi tạo Timber thư viện cho ứng dụng của bạn để bạn có thể sử dụng thư viện trong các hoạt động của mình.
        Timber.plant(Timber.DebugTree())
    }
}