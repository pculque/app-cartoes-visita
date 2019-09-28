package com.pculque.linqme

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric


class CustomApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
    }
}