package com.example.ui.screens.vendor_form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContactPerson
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.util.PersianUtils

/**
 * Step 3: Phones, Mobiles & Personnel
 */
@Composable
fun Step3ContactsPersonnel(
    primaryPhone: String,
    onPrimaryPhoneChange: (String) -> Unit,
    phoneError: String? = null,
    additionalPhones: SnapshotStateList<String>,
    primaryMobile: String,
    onPrimaryMobileChange: (String) -> Unit,
    mobileError: String? = null,
    additionalMobiles: SnapshotStateList<String>,
    email: String,
    onEmailChange: (String) -> Unit,
    website: String,
    onWebsiteChange: (String) -> Unit,
    personnelList: SnapshotStateList<ContactPerson>,
    onAddPersonnelClick: () -> Unit,
    onEditPersonnelClick: (index: Int, person: ContactPerson) -> Unit
) {
    Text(
        text = "شماره‌های تماس و پرسنل ارتباطی",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    // Landline Phones
    Text(
        text = "شماره‌های تلفن ثابت شرکت:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
    )

    OutlinedTextField(
        value = primaryPhone,
        onValueChange = onPrimaryPhoneChange,
        label = { Text("شماره تلفن ثابت اصلی") },
        placeholder = { Text("021-77654321") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth()
    )

    additionalPhones.forEachIndexed { idx, p ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = p,
                onValueChange = { additionalPhones[idx] = it },
                label = { Text("تلفن ثابت فرعی ${PersianUtils.toPersianDigits(idx + 1)}") },
                placeholder = { Text("021-77654322") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { additionalPhones.removeAt(idx) }) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = RedFavorite)
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

    // Mobiles & Messengers
    Text(
        text = "شماره‌های همراه و پیام‌رسان‌ها (واتس‌اپ، بله، تلگرام):",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
    )

    val isMobError = mobileError != null || (primaryMobile.isNotBlank() && !PersianUtils.isValidIranianMobile(primaryMobile))
    OutlinedTextField(
        value = primaryMobile,
        onValueChange = onPrimaryMobileChange,
        label = { Text("شماره همراه اصلی") },
        placeholder = { Text("0912-3456789") },
        singleLine = true,
        isError = isMobError,
        supportingText = if (isMobError) { { Text(mobileError ?: "شماره همراه باید با ۰۹ آغاز شده و ۱۱ رقم باشد", color = MaterialTheme.colorScheme.error) } } else null,
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
                placeholder = { Text("0912-9876543") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { additionalMobiles.removeAt(idx) }) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = RedFavorite)
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("پست الکترونیک (ایمیل)") },
            placeholder = { Text("info@company.com") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = website,
            onValueChange = onWebsiteChange,
            label = { Text("وبسایت رسمی") },
            placeholder = { Text("www.company.com") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Dedicated Personnel Section
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.People, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "پرسنل و مدیران ارتباطی شرکت (${PersianUtils.toPersianDigits(personnelList.size)} نفر)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onAddPersonnelClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = TealPrimary,
                contentColor = NavyDeep
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("افزودن پرسنل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (personnelList.isEmpty()) {
        Text(
            text = "هنوز پرسنلی برای این شرکت ثبت نشده است. با دکمه بالا می‌توانید مدیرعامل، مسئول فروش و سایر افراد را ثبت کنید.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        personnelList.forEachIndexed { index, person ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(TealDark.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = person.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${person.role} ${if (person.extension.isNotBlank()) "• داخلی ${PersianUtils.toPersianDigits(person.extension)}" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (person.mobile.isNotBlank()) {
                            Text(
                                text = "موبایل: ${PersianUtils.toPersianDigits(person.mobile)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = { onEditPersonnelClick(index, person) }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = TealPrimary, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = { personnelList.removeAt(index) }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = RedFavorite, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
