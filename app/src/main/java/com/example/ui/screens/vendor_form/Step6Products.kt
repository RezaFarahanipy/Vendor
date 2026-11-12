package com.example.ui.screens.vendor_form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.InkDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.BrandPrimary
import com.example.util.PersianUtils

/**
 * Step 6: Products (محصولات) - Dedicated step with single-line button and product items
 */
@Composable
fun Step6Products(
    vendorProductsList: SnapshotStateList<Product>,
    onAddProductClick: () -> Unit,
    onEditProductClick: (index: Int, product: Product) -> Unit
) {
    Text(
        text = "محصولات",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Icon(Icons.Default.Inventory, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "محصولات (${PersianUtils.toPersianDigits(vendorProductsList.size)} کالا)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }

        Button(
            onClick = onAddProductClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "افزودن محصول",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }

    if (vendorProductsList.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Text(
                text = "هنوز محصول یا کالایی برای این وندور ثبت نشده است. با زدن دکمه «افزودن محصول» می‌توانید نام دستگاه، برند، کد فنی و مشخصات فنی را اضافه کنید.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(14.dp)
            )
        }
    } else {
        vendorProductsList.forEachIndexed { index, prod ->
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = prod.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${prod.brand} • مدل: ${prod.model.ifBlank { "-" }} • ${prod.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (prod.specifications.isNotBlank()) {
                            Text(
                                text = prod.specifications,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = { onEditProductClick(index, prod) }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = BrandPrimary, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = { vendorProductsList.removeAt(index) }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = RedFavorite, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
