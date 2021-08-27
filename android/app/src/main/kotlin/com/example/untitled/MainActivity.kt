package com.example.untitled

import android.annotation.SuppressLint
import android.os.Build.VERSION_CODES
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/battery"
    private val CHANNEL_SMS = "samples.flutter.dev/SMS"

    @SuppressLint("ServiceCast", "HardwareIds")
    @RequiresApi(VERSION_CODES.LOLLIPOP_MR1)
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "getBatteryLevel") {
                val results = getSIMModules()
                result.success(results)
            } else if (call.method == "SMS") {
                call.argument<Int>("selectedSimSlot")?.let {
                    SmsUtils().sendSMS(
                        applicationContext, "+919585313659", "Hello There", it
                    )
                }
            } else {
                result.notImplemented()
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(VERSION_CODES.LOLLIPOP_MR1)
    private fun getSIMModules(): ArrayList<String> {
        val localSubscriptionManager =
            getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

        val values = ArrayList<String>()//Cre
        if (localSubscriptionManager.activeSubscriptionInfoCount > 1) {
            val localList: List<SubscriptionInfo> =
                localSubscriptionManager.activeSubscriptionInfoList
            val simInfo = localList[0] as SubscriptionInfo
            val simInfo1 = localList[1] as SubscriptionInfo
//            val sim1 = simInfo.displayName.toString()
            val sim1 = simInfo.displayName.toString()
            val sim2 = simInfo1.displayName.toString()
            values.add(sim1)
            values.add(sim2)
        } else {
            //if there is 1 sim in dual sim mobile
            val tManager = baseContext
                .getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val sim1 = tManager.networkOperatorName
            values.add(sim1)
        }
        return values
    }
}
