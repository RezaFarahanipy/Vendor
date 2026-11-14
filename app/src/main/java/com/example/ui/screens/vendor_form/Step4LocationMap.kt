package com.example.ui.screens.vendor_form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LocationPickerCard
import com.example.util.t
import com.example.ui.theme.InkDeep
import com.example.ui.theme.BrandPrimary
import com.example.util.PersianUtils
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.util.tf

/**
 * Step 4: Address (type, province/city, postal code, full address) and map location.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step4LocationMap(
    name: String,
    latitude: Double,
    longitude: Double,
    onLocationChange: (lat: Double, lng: Double, prov: String?, cty: String?) -> Unit,
    province: String,
    onProvinceChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    addressType: String = "کارخانه / کارگاه",
    onAddressTypeChange: (String) -> Unit = {},
    postalCode: String = "",
    onPostalCodeChange: (String) -> Unit = {},
    address: String,
    onAddressChange: (String) -> Unit
) {
    Text(
        text = t("نشانی و موقعیت مکانی"),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

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
                    val isSelected = addressType == typeOption
                    FilterChip(
                        selected = isSelected,
                        onClick = { onAddressTypeChange(typeOption) },
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
                    value = province,
                    onValueChange = onProvinceChange,
                    label = { Text(t("استان")) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = onCityChange,
                    label = { Text(t("شهر")) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            val postalDigits = PersianUtils.toEnglishDigits(postalCode).filter { it.isDigit() }
            val isPostalError = postalCode.isNotBlank() && postalDigits.length != 10
            OutlinedTextField(
                value = postalCode,
                onValueChange = { input ->
                    val digits = PersianUtils.toEnglishDigits(input).filter { it.isDigit() }.take(10)
                    onPostalCodeChange(digits)
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
                value = address,
                onValueChange = onAddressChange,
                label = { Text(t("نشانی پستی دقیق")) },
                placeholder = { Text(t("خیابان، کوچه، پلاک، واحد...")) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
    }

    LocationPickerCard(
        latitude = latitude,
        longitude = longitude,
        onLocationChange = { lat, lng -> onLocationChange(lat, lng, null, null) },
        vendorTitle = name.ifBlank { "Vendor" }
    )
}
