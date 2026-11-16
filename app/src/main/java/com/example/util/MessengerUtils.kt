package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.util.t

object MessengerUtils {

    /**
     * Formats Iranian numbers to international format (e.g., 0912... -> 98912...)
     * Converts Persian/Arabic digits first to prevent stripping digits.
     */
    fun cleanPhoneForMessenger(phone: String): String {
        val english = PersianUtils.toEnglishDigits(phone)
        var clean = english.replace(Regex("[^0-9+]"), "")
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
            Toast.makeText(context, t("برنامه واتس‌اپ یافت نشد"), Toast.LENGTH_SHORT).show()
        }
    }

    fun openTelegram(context: Context, rawPhone: String) {
        try {
            val phone = cleanPhoneForMessenger(rawPhone)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+$phone"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, t("برنامه تلگرام یافت نشد"), Toast.LENGTH_SHORT).show()
        }
    }

    fun openBale(context: Context, rawPhone: String) {
        try {
            val phone = cleanPhoneForMessenger(rawPhone)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ble.ir/+$phone"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, t("پیام‌رسان بله باز نشد"), Toast.LENGTH_SHORT).show()
        }
    }

    fun openRubika(context: Context, rawPhone: String) {
        val phone = PersianUtils.normalizePhone(rawPhone)
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            clipboard.setPrimaryClip(android.content.ClipData.newPlainText("phone", phone))
        } catch (_: Exception) {
        }
        val launch = listOf("app.rbmain.a", "ir.resaneh1.iptv").firstNotNullOfOrNull {
            context.packageManager.getLaunchIntentForPackage(it)
        }
        if (launch != null) {
            Toast.makeText(context, t("شماره کپی شد. در روبیکا آن را جستجو کنید"), Toast.LENGTH_LONG).show()
            context.startActivity(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } else {
            Toast.makeText(context, t("برنامه روبیکا نصب نیست"), Toast.LENGTH_SHORT).show()
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://rubika.ir")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (_: Exception) {
            }
        }
    }
}
