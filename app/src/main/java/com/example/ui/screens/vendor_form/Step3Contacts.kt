package com.example.ui.screens.vendor_form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.util.PersianUtils

/**
 * Step 3: Contacts (Landline with 021-xxxxxxxx mask & city detection, Mobiles, Stacked Email & Website)
 */
@Composable
fun Step3Contacts(
    primaryPhone: String,
    onPrimaryPhoneChange: (String) -> Unit,
    additionalPhones: SnapshotStateList<String>,
    primaryMobile: String,
    onPrimaryMobileChange: (String) -> Unit,
    mobileError: String? = null,
    additionalMobiles: SnapshotStateList<String>,
    email: String,
    onEmailChange: (String) -> Unit,
    website: String,
    onWebsiteChange: (String) -> Unit
) {
    Text(
        text = "اطلاعات تماس، ارتباطات و وب‌سایت",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    // Landline Section
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Phone, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "شماره‌های تلفن ثابت شرکت (با کد شهر)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        val primaryCity = PersianUtils.detectLandlineCity(primaryPhone)
        OutlinedTextField(
            value = primaryPhone,
            onValueChange = { input ->
                val formatted = PersianUtils.formatLandlinePhone(input)
                onPrimaryPhoneChange(formatted)
            },
            label = { Text("تلفن ثابت اصلی (ماسک 021-xxxxxxxx)") },
            placeholder = { Text("021-77654321") },
            singleLine = true,
            trailingIcon = {
                if (primaryCity != null) {
                    Surface(
                        color = TealPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = primaryCity,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            },
            supportingText = {
                if (primaryCity != null) {
                    Text("شهر شناسایی‌شده: $primaryCity", color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("الگوی معتبر: کد شهر ۳ رقم + ۸ رقم تلفن (مثال: 021-88776655)", fontSize = 10.sp)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        additionalPhones.forEachIndexed { idx, p ->
            val city = PersianUtils.detectLandlineCity(p)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = p,
                    onValueChange = { input ->
                        additionalPhones[idx] = PersianUtils.formatLandlinePhone(input)
                    },
                    label = { Text("تلفن ثابت فرعی ${PersianUtils.toPersianDigits(idx + 1)}") },
                    placeholder = { Text("021-77654322") },
                    singleLine = true,
                    trailingIcon = {
                        if (city != null) {
                            Surface(
                                color = TealPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = city,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { additionalPhones.removeAt(idx) }) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف خط", tint = RedFavorite)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { additionalPhones.add("") }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("افزودن خط ثابت دیگر")
            }
        }
    }

    // Mobiles & Messengers
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "شماره‌های همراه شرکت (ارتباط پیام‌رسان‌ها)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        val isMobError = mobileError != null || (primaryMobile.isNotBlank() && !PersianUtils.isValidIranianMobile(primaryMobile))
        OutlinedTextField(
            value = primaryMobile,
            onValueChange = onPrimaryMobileChange,
            label = { Text("شماره همراه اصلی") },
            placeholder = { Text("09123456789") },
            singleLine = true,
            isError = isMobError,
            supportingText = if (isMobError) {
                { Text(mobileError ?: "شماره همراه باید ۱۱ رقم بوده و با ۰۹ شروع شود", color = MaterialTheme.colorScheme.error) }
            } else {
                { Text("برای تماس مستقیم و اتصال به روبیکا، تلگرام، بله و واتس‌اپ", fontSize = 10.sp) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        additionalMobiles.forEachIndexed { idx, m ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = m,
                    onValueChange = { additionalMobiles[idx] = it },
                    label = { Text("شماره همراه فرعی ${PersianUtils.toPersianDigits(idx + 1)}") },
                    placeholder = { Text("09129876543") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { additionalMobiles.removeAt(idx) }) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف شماره", tint = RedFavorite)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { additionalMobiles.add("") }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("افزودن شماره همراه دیگر")
            }
        }
    }

    // Stacked Email & Website (زیر هم باشد)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("پست الکترونیک (ایمیل رسمی)") },
            placeholder = { Text("info@company.com") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TealPrimary) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = website,
            onValueChange = onWebsiteChange,
            label = { Text("وبسایت رسمی شرکت") },
            placeholder = { Text("www.company.com") },
            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = TealPrimary) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
