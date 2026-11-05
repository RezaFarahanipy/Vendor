package com.example.ui.screens.vendor_form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.InteractiveMapPicker

/**
 * Step 4: Interactive Map, Coordinates & Address
 */
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
    address: String,
    onAddressChange: (String) -> Unit
) {
    Text(
        text = "موقعیت جغرافیایی روی نقشه و آدرس دقیق",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    // Interactive Map Picker
    InteractiveMapPicker(
        latitude = latitude,
        longitude = longitude,
        onLocationChange = onLocationChange,
        vendorTitle = name
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = province,
            onValueChange = onProvinceChange,
            label = { Text("استان") },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            label = { Text("شهر / شهرک صنعتی") },
            modifier = Modifier.weight(1f)
        )
    }

    OutlinedTextField(
        value = address,
        onValueChange = onAddressChange,
        label = { Text("آدرس پستی کامل کارخانه یا دفتر مرکزی") },
        placeholder = { Text("شهرک صنعتی، خیابان صنعت، پلاک...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2
    )
}
