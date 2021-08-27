package com.example.untitled

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi


class SmsUtils {
    @SuppressLint("MissingPermission", "NewApi")
    fun sendSMS(context: Context, phoneNumber: String, message: String, simSlot: Int) {
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
        val localPendingIntent1 = PendingIntent.getBroadcast(context, 0, Intent(SENT), 0)
        val localPendingIntent2 = PendingIntent.getBroadcast(context, 0, Intent(DELIVERED), 0)

        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        context, "SMS sent",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
                        context, "Generic failure",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
                        context, "No service",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
                        context, "Null PDU",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
                        context, "Radio off",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, IntentFilter(SENT))

        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        context, "SMS delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                    Activity.RESULT_CANCELED -> Toast.makeText(
                        context, "SMS not delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, IntentFilter(DELIVERED))
        if (Build.VERSION.SDK_INT >= 22) {
            val subscriptionManager = context.getSystemService(SubscriptionManager::class.java)
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(simSlot)
            SmsManager.getSmsManagerForSubscriptionId(subscriptionInfo.subscriptionId)
                .sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    localPendingIntent1,
                    localPendingIntent2
                )


        }
    }
}
