package com.pculque.linqme

import android.app.Application
import br.com.hands.mdm.libs.android.bundle.MDMBundle
import br.com.hands.mdm.libs.android.core.MDMCore
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric


class CustomApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //if (!BuildConfig.DEBUG)
        Fabric.with(this, Crashlytics())
        MDMCore.setDebugMode(true)

        // Start do Bundle com todos os módulos
        MDMBundle.start(this)
    }
}