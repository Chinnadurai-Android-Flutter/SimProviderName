package com.example.untitled

import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.plugins.GeneratedPluginRegistrant
import android.os.Bundle
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log;
import android.telephony.SubscriptionManager
import android.telephony.SubscriptionInfo
import android.telephony.TelephonyManager
import android.widget.Toast

class MainActivity : FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/battery"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "getBatteryLevel") {
                /*val batteryLevel = getBatteryLevel()
                if (batteryLevel != -1) {
                    result.success(batteryLevel)
                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }*/
                val batteryLevel = getSIMModules()
                result.success(batteryLevel)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            batteryLevel =
                intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(
                    BatteryManager.EXTRA_SCALE,
                    -1
                )
        }
        return batteryLevel
    }

    private fun getSIMModules(): String {
        val localSubscriptionManager =
            getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        var values = ""
        if (localSubscriptionManager.activeSubscriptionInfoCount > 1) {
            val localList: List<SubscriptionInfo> =
                localSubscriptionManager.activeSubscriptionInfoList
            val simInfo = localList[0] as SubscriptionInfo
            val simInfo1 = localList[1] as SubscriptionInfo
            val sim1 = simInfo.displayName.toString()
            val sim2 = simInfo1.displayName.toString()
            values = "$sim1, $sim2 Dual Sim"
        } else {
            //if there is 1 sim in dual sim mobile
            val tManager = baseContext
                .getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val sim1 = tManager.networkOperatorName
            values = sim1
        }
        Toast.makeText(baseContext, values, Toast.LENGTH_LONG).show()
        return values
    }
}
