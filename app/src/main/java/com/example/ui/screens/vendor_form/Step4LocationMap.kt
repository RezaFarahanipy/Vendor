package com.example.ui.screens.vendor_form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VendorAddress
import com.example.ui.components.LocationPickerCard
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandPrimary
import com.example.util.PersianUtils
import com.example.util.t
import com.example.util.tf

/**
 * Step 4: One or more addresses (type, province/city, postal code, full address) with a map location each.
 */
@Composable
fun Step4LocationMap(
    name: String,
    addresses: List<VendorAddress>,
    onUpdate: (Int, VendorAddress) -> Unit,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit
) {
    Text(
        text = t("نشانی و موقعیت مکانی"),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    addresses.forEachIndexed { index, addr ->
        key(addr.uid) {
            AddressEditor(
                index = index,
                total = addresses.size,
                addr = addr,
                vendorName = name,
                onChange = { onUpdate(index, it) },
                onRemove = { onRemove(index) }
            )
        }
    }

    OutlinedButton(
        onClick = onAdd,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(t("افزودن آدرس دیگر"), fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddressEditor(
    index: Int,
    total: Int,
    addr: VendorAddress,
    vendorName: String,
    onChange: (VendorAddress) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tf("آدرس شماره {0}", PersianUtils.toPersianDigits(index + 1)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandDark,
                    modifier = Modifier.weight(1f)
                )
                if (total > 1) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Delete, contentDescription = t("حذف آدرس"), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Text(
                text = t("نوع محل"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            val addressTypes = listOf("کارخانه / کارگاه", "دفتر مرکزی", "انبار", "شوروم / دفتر فروش")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                addressTypes.forEach { typeOption ->
                    FilterChip(
                        selected = addr.type == typeOption,
                        onClick = { onChange(addr.copy(type = typeOption)) },
                        label = { Text(t(typeOption), style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = addr.province,
                    onValueChange = { onChange(addr.copy(province = it)) },
                    label = { Text(t("استان")) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = addr.city,
                    onValueChange = { onChange(addr.copy(city = it)) },
                    label = { Text(t("شهر")) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            val postalDigits = PersianUtils.toEnglishDigits(addr.postalCode).filter { it.isDigit() }
            val isPostalError = addr.postalCode.isNotBlank() && postalDigits.length != 10
            OutlinedTextField(
                value = addr.postalCode,
                onValueChange = { input ->
                    onChange(addr.copy(postalCode = PersianUtils.toEnglishDigits(input).filter { it.isDigit() }.take(10)))
                },
                label = { Text(t("کد پستی (۱۰ رقمی)")) },
                placeholder = { Text("1234567890") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isPostalError,
                supportingText = if (isPostalError) {
                    { Text(tf("کد پستی باید ۱۰ رقم باشد (تعداد واردشده: {0})", postalDigits.length), color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                } else null,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = addr.address,
                onValueChange = { onChange(addr.copy(address = it)) },
                label = { Text(t("نشانی پستی دقیق")) },
                placeholder = { Text(t("خیابان، کوچه، پلاک، واحد...")) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            LocationPickerCard(
                latitude = addr.latitude,
                longitude = addr.longitude,
                onLocationChange = { lat, lng -> onChange(addr.copy(latitude = lat, longitude = lng)) },
                vendorTitle = vendorName.ifBlank { "Vendor" } + if (index > 0) " (${index + 1})" else ""
            )
        }
    }
}
