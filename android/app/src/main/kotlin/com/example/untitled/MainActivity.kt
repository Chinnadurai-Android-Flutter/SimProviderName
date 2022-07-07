package com.example.untitled
//
//import android.annotation.SuppressLint
//import android.os.Build.VERSION_CODES
//import android.telephony.SubscriptionInfo
//import android.telephony.SubscriptionManager
//import android.telephony.TelephonyManager
//import androidx.annotation.NonNull
//import android.content.Context
//import android.content.ContextWrapper
//import androidx.annotation.RequiresApi
//import io.flutter.embedding.android.FlutterActivity
//import io.flutter.embedding.engine.FlutterEngine
//import io.flutter.embedding.android.FlutterFragmentActivity
//import io.flutter.plugin.common.MethodChannel
//import io.flutter.plugins.GeneratedPluginRegistrant
//
//class MainActivity : FlutterFragmentActivity() {
//    private val CHANNEL = "samples.flutter.dev"
//
//    @RequiresApi(VERSION_CODES.M)
//    @SuppressLint("ServiceCast", "HardwareIds")
//    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
//        GeneratedPluginRegistrant.registerWith(flutterEngine)
//        MethodChannel(
//            flutterEngine.dartExecutor.binaryMessenger,
//            CHANNEL
//        ).setMethodCallHandler { call, result ->
//            when (call.method) {
//                "activeSubscriptionInfoList" -> {
//                    val results = getSIMModules()
//                    result.success(results)
//                }
//                "SMS" -> {
//                    call.argument<String>("selectedSimSlotName")?.let { name ->
//                        call.argument<Int>("selectedSimSlotNumber")?.let {
//                            SmsUtils().sendSMS(
//                                applicationContext, "+919585313659", name, it
//                            )
//                        }
//                    }
//                }
//                else -> {
//                    result.notImplemented()
//                }
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    @RequiresApi(VERSION_CODES.LOLLIPOP_MR1)
//    private fun getSIMModules(): ArrayList<String> {
//        val localSubscriptionManager =
//            getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
//        val values = ArrayList<String>()
//        val _sb: List<SubscriptionInfo> =
//            SubscriptionManager.from(applicationContext).activeSubscriptionInfoList
//        if (localSubscriptionManager.activeSubscriptionInfoCount > 1) {
//            for (element in _sb) {
//                values.add(element.displayName.toString())
//            }
//        } else {
//            val tManager = baseContext
//                .getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//            values.add(tManager.networkOperatorName)
//        }
//        return values
//    }
//}


import android.annotation.SuppressLint
import android.os.Build.VERSION_CODES
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterFragmentActivity() {
    @RequiresApi(VERSION_CODES.M)
    @SuppressLint("ServiceCast", "HardwareIds")
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            "icici.flutter.methodChannel"
        ).setMethodCallHandler { call, result ->
            //Call -> What is getting from flutter
            //Result what you send to flutter form here
            when (call.method) {
                "activeSubscriptionInfoList" -> {
                    val results = getSIMModules() // Whatever single sim / double sim
                    result.success(results)
                }
                "SMS" -> {
                    call.argument<String>("selectedSimSlotName")?.let { name ->
                        call.argument<String>("selectedSimSlotNumber")?.let {
                            SmsUtils().sendSMS(
                                applicationContext, "+919585313659", name, it.toInt()
                            )
                        }
                    }
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(VERSION_CODES.LOLLIPOP_MR1)
    private fun getSIMModules(): ArrayList<HashMap<String, String>> {
        val localSubscriptionManager =
            getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val mapValues = ArrayList<HashMap<String, String>>()
//        val _sb: List<SubscriptionInfo> =
//            SubscriptionManager.from(applicationContext).activeSubscriptionInfoList
        val _sb: List<SubscriptionInfo> =
            localSubscriptionManager.activeSubscriptionInfoList
//        if (localSubscriptionManager.activeSubscriptionInfoCount > 1) {
//            for (element in _sb) {
//                val meMap = HashMap<String, String>()
//                values.add(element.displayName.toString())
//                meMap["displayName"] = element.displayName.toString()
//                meMap["simSlotIndex"] = element.simSlotIndex.toString()
//                meMap["carrierName"] = element.carrierName.toString()
//                meMap["iccId"] = element.iccId.toString()
//            }
//        } else {
//            val tManager = baseContext
//                .getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//            values.add(tManager.networkOperatorName)
//        }
        for (element in _sb) {
            val meMap = HashMap<String, String>()
            meMap["displayName"] = element.displayName.toString()
            meMap["simSlotIndex"] = element.simSlotIndex.toString()
            meMap["carrierName"] = element.carrierName.toString()
            meMap["iccId"] = element.iccId.toString()
            meMap["subscriptionId"] = element.subscriptionId.toString()
            if ("No service" != element.carrierName.toString())
                mapValues.add(meMap)
        }
        return mapValues
    }
}
