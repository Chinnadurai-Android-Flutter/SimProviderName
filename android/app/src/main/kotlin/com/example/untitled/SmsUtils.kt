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
import android.widget.Toast


class SmsUtils {
    @SuppressLint("MissingPermission", "NewApi", "WrongConstant", "UnspecifiedImmutableFlag")
    fun sendSMS(context: Context, phoneNumber: String, message: String, simSlot: Int) {
        val sent = "SMS_SENT"
        val delivered = "SMS_DELIVERED"
        val sendPendingIntent: PendingIntent
        val deliveryPendingIntent: PendingIntent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sendPendingIntent = PendingIntent.getBroadcast(context, 1, Intent(sent), PendingIntent.FLAG_IMMUTABLE)
            deliveryPendingIntent =
                PendingIntent.getBroadcast(context, 2, Intent(delivered), PendingIntent.FLAG_IMMUTABLE)
        } else {
            sendPendingIntent = PendingIntent.getBroadcast(context, 1, Intent(sent), PendingIntent.FLAG_ONE_SHOT)
            deliveryPendingIntent =
                PendingIntent.getBroadcast(context, 2, Intent(delivered), PendingIntent.FLAG_ONE_SHOT)
        }


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
        }, IntentFilter(sent))

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
        }, IntentFilter(delivered))
        val subscriptionManager = context.getSystemService(SubscriptionManager::class.java)
        val subscriptionInfo =
            subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(simSlot)
        SmsManager.getSmsManagerForSubscriptionId(subscriptionInfo.subscriptionId)
            .sendTextMessage(
                phoneNumber,
                null,
                message,
                sendPendingIntent,
                deliveryPendingIntent
            )
    }
}
