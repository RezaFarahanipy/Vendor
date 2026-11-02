package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object MessengerUtils {

    /**
     * Formats Iranian numbers to international format (e.g., 0912... -> 98912...)
     */
    fun cleanPhoneForMessenger(phone: String): String {
        var clean = phone.replace(Regex("[^0-9+]"), "")
        if (clean.startsWith("+")) clean = clean.substring(1)
        if (clean.startsWith("0")) {
            clean = "98" + clean.substring(1)
        }
        return clean
    }

    fun openWhatsApp(context: Context, rawPhone: String, message: String = "") {
        try {
            val phone = cleanPhoneForMessenger(rawPhone)
            val url = if (message.isNotBlank()) {
                "https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"
            } else {
                "https://api.whatsapp.com/send?phone=$phone"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "برنامه واتس‌اپ یافت نشد", Toast.LENGTH_SHORT).show()
        }
    }

    fun openTelegram(context: Context, rawPhone: String) {
        try {
            val phone = cleanPhoneForMessenger(rawPhone)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+$phone"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "برنامه تلگرام یافت نشد", Toast.LENGTH_SHORT).show()
        }
    }

    fun openBale(context: Context, rawPhone: String) {
        try {
            val phone = cleanPhoneForMessenger(rawPhone)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ble.ir/+$phone"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "پیام‌رسان بله باز نشد", Toast.LENGTH_SHORT).show()
        }
    }

    fun openRubika(context: Context, rawPhone: String) {
        try {
            val phone = cleanPhoneForMessenger(rawPhone)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://rubika.ir/joinc/$phone"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "پیام‌رسان روبیکا باز نشد", Toast.LENGTH_SHORT).show()
        }
    }
}
