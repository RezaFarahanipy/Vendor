package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.ui.screens.vendor_detail.VendorDetailHeaderCard
import com.example.ui.screens.vendor_detail.VendorDetailLegalBankSection
import com.example.ui.screens.vendor_detail.VendorDetailPersonnelSection
import com.example.ui.components.CatalogViewerDialog
import com.example.ui.components.DirectContactActionsRow
import com.example.ui.components.dialPhone
import com.example.ui.components.openMap
import com.example.ui.components.openWebsite
import com.example.ui.components.sendEmail
import com.example.ui.components.sendSms
import com.example.ui.components.shareVendor
import com.example.ui.theme.AmberRating
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.GreenVerified
import com.example.ui.theme.InkDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandLight
import com.example.ui.theme.BrandPrimary
import com.example.ui.viewmodel.VendorViewModel
import com.example.util.MessengerUtils
import com.example.util.PersianUtils
import java.io.File
import com.example.ui.theme.StatusBarStyle
import com.example.data.model.CatalogItem
import com.example.data.model.catalogItems
import com.example.util.t
import com.example.util.tf
import com.example.data.model.addressItems
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.components.GenderSelector
import com.example.ui.components.MaskedLandlineField

@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun VendorDetailScreen(
    vendorId: Long,
    viewModel: VendorViewModel,
    onBack: () -> Unit,
    onEditVendor: (Vendor) -> Unit,
    onOpenMap: (Long) -> Unit
) {
    StatusBarStyle(onGradient = false)
    val context = LocalContext.current
    val allVendors by viewModel.allVendors.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val vendor = allVendors.find { it.id == vendorId }

    if (vendor == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(t("وندور مورد نظر یافت نشد"))
        }
        return
    }

    val contactsFlow = remember(vendor.id) { viewModel.getContactsForVendor(vendor.id) }
    val contacts by contactsFlow.collectAsState(initial = emptyList())
    val vendorProducts = allProducts.filter { it.vendorId == vendor.id }

    var showInquiryDialog by remember { mutableStateOf(false) }
    var showNotesDialog by remember { mutableStateOf(false) }
    var notesText by remember(vendor.notes) { mutableStateOf(vendor.notes) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Personnel dialog state
    var showContactDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<ContactPerson?>(null) }
    var contactName by remember { mutableStateOf("") }
    var contactRole by remember { mutableStateOf("مدیرعامل") }
    var contactMobile by remember { mutableStateOf("") }
    var contactGender by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactExt by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }

    // Product dialog state
    var showProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var prodName by remember { mutableStateOf("") }
    var prodBrand by remember { mutableStateOf("") }
    var prodModel by remember { mutableStateOf("") }
    var prodCode by remember { mutableStateOf("") }
    var prodSpecs by remember { mutableStateOf("") }
    var prodDesc by remember { mutableStateOf("") }
    var prodCat by remember { mutableStateOf("") }

    var showCatalogList by remember { mutableStateOf(false) }
    var viewingCatalog by remember { mutableStateOf<CatalogItem?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Action Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = t("بازگشت"),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { shareVendor(context, vendor) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = t("اشتراک‌گذاری"), tint = MaterialTheme.colorScheme.onSurface)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { viewModel.toggleVendorFavorite(vendor) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("favorite_button")
                    ) {
                        Icon(
                            imageVector = if (vendor.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = t("علاقه‌مندی"),
                            tint = if (vendor.isFavorite) RedFavorite else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { onEditVendor(vendor) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = t("ویرایش"), tint = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("delete_vendor_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = t("حذف وندور"), tint = RedFavorite)
                    }
                }
            }
        }

        // Elegant Header Card: Logo Side-by-Side with Company Name & Info
        item {
            VendorDetailHeaderCard(vendor = vendor)
        }

        // Consolidated Contact & Messengers Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = t("ارتباط مستقیم با وندور"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Compact Messengers Row (WhatsApp, Telegram, Bale, Rubika)
                    val activePhone = vendor.mobile.ifBlank { vendor.phone }
                    if (activePhone.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            com.example.ui.components.CompactMessengerRow(
                                mobile = vendor.mobile,
                                phone = vendor.phone
                            )
                        }
                    }

                    // Direct Call & SMS Buttons
                    val primaryNum = vendor.phone.ifBlank { vendor.mobile }
                    if (primaryNum.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { dialPhone(context, primaryNum) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(t("تماس تلفنی"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            if (vendor.mobile.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { sendSms(context, vendor.mobile) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(t("ارسال پیامک"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    // Landline phones with detected city
                    val allPhones = vendor.getAllPhones()
                    if (allPhones.isNotEmpty()) {
                        Text(
                            text = t("تلفن‌های ثابت:"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        allPhones.forEachIndexed { i, p ->
                            val detectedCity = PersianUtils.detectLandlineCity(p)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${if (i == 0) "خط اصلی: " else "خط ${PersianUtils.toPersianDigits(i + 1)}: "} ${PersianUtils.toPersianDigits(p)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (detectedCity != null) {
                                            Text(
                                                text = tf("پیش‌شماره {0}", detectedCity),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BrandDark,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                                IconButton(onClick = { dialPhone(context, p) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Call, contentDescription = t("تماس"), tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    // Mobile numbers
                    val allMobiles = vendor.getAllMobiles()
                    if (allMobiles.isNotEmpty()) {
                        Text(
                            text = t("شماره‌های همراه:"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        allMobiles.forEachIndexed { i, m ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${if (i == 0) "همراه اصلی: " else "همراه ${PersianUtils.toPersianDigits(i + 1)}: "} ${PersianUtils.toPersianDigits(m)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Row {
                                    IconButton(onClick = { dialPhone(context, m) }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Default.Call, contentDescription = t("تماس"), tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { sendSms(context, m) }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Default.Message, contentDescription = t("پیامک"), tint = BlueAccent, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Email & Website
                    if (vendor.email.isNotBlank() || vendor.website.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (vendor.email.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { sendEmail(context, vendor.email) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(t("ایمیل"), fontSize = 11.sp)
                                }
                            }
                            if (vendor.website.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { openWebsite(context, vendor.website) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(t("وبسایت"), fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Addresses & Map (one row per address)
                    vendor.addressItems().forEach { addr ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tf("{0} • {1}، {2}", t(addr.type), addr.province, addr.city),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (addr.address.isNotBlank()) {
                                    Text(
                                        text = addr.address,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (addr.postalCode.isNotBlank()) {
                                    Text(
                                        text = tf("کد پستی: {0}", PersianUtils.toPersianDigits(addr.postalCode)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(
                                onClick = { onOpenMap(vendor.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = t("نقشه"), tint = BrandPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Legal & Financial Section
        item {
            VendorDetailLegalBankSection(vendor = vendor)
        }

        // Personnel Management Section
        item {
            VendorDetailPersonnelSection(
                contacts = contacts,
                onEditContact = { contact ->
                    editingContact = contact
                    contactName = contact.name
                    contactRole = contact.role
                    contactMobile = contact.mobile
                    contactGender = contact.gender
                    contactPhone = contact.phone
                    contactExt = contact.extension
                    contactEmail = contact.email
                    showContactDialog = true
                },
                onDeleteContact = { contactId ->
                    viewModel.deleteContact(contactId)
                }
            )
        }

        // Full PDF Catalog Card (only when the vendor has catalog files)
        if (vendor.catalogItems().isNotEmpty() || vendor.catalogPdfUrl.isNotBlank()) item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RedFavorite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color.White, modifier = Modifier.size(24.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = vendor.catalogItems().let { items ->
                                    if (items.size > 1) tf("کاتالوگ‌ها ({0} فایل)", PersianUtils.toPersianDigits(items.size))
                                    else (items.firstOrNull()?.title ?: vendor.catalogTitle).ifBlank { t("کاتالوگ فنی") }
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = vendor.catalogItems().joinToString("، ") { it.title.ifBlank { it.fileName } },
                                maxLines = 2,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val items = vendor.catalogItems()
                                when {
                                    items.size > 1 -> showCatalogList = true
                                    items.size == 1 -> viewingCatalog = items[0]
                                    else -> viewingCatalog = CatalogItem(vendor.catalogTitle, "", "")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(t("مشاهده و دانلود کاتالوگ"))
                        }
                    }
                }
            }
        }

        // Products Section with ADD, EDIT, DELETE
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tf("محصولات و تجهیزات تخصصی ({0})", PersianUtils.toPersianDigits(vendorProducts.size)),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Button(
                        onClick = {
                            editingProduct = null
                            prodName = ""
                            prodBrand = vendor.name
                            prodModel = ""
                            prodCode = ""
                            prodSpecs = ""
                            prodDesc = ""
                            prodCat = "پمپ‌های سانتریفیوژ"
                            showProductDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("افزودن محصول"), style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (vendorProducts.isEmpty()) {
                    Text(
                        text = t("محصولی ثبت نشده است. با دکمه بالا می‌توانید محصول یا تجهیز اضافه کنید."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    vendorProducts.forEach { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = product.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${product.brand} • مدل: ${product.model.ifBlank { "-" }} • دسته‌بندی: ${product.category}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            editingProduct = product
                                            prodName = product.name
                                            prodBrand = product.brand
                                            prodModel = product.model
                                            prodCode = product.technicalCode
                                            prodSpecs = product.specifications
                                            prodDesc = product.description
                                            prodCat = product.category
                                            showProductDialog = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = t("ویرایش محصول"), tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteProduct(product.id) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = t("حذف محصول"), tint = RedFavorite, modifier = Modifier.size(18.dp))
                                    }
                                }

                                if (product.specifications.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = tf("مشخصات فنی: {0}", product.specifications),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }


        // Internal Notes Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NoteAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = t("یادداشت‌های داخلی (محرمانه)"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(onClick = { showNotesDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = t("ویرایش یادداشت"), tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (vendor.notes.isNotBlank()) vendor.notes else t("هیچ یادداشتی ثبت نشده است."),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

    }

    // Catalog list (several files) and viewer
    if (showCatalogList) {
        AlertDialog(
            onDismissRequest = { showCatalogList = false },
            title = { Text(tf("کاتالوگ‌های {0}", vendor.name), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    vendor.catalogItems().forEach { item ->
                        Card(
                            onClick = { showCatalogList = false; viewingCatalog = item },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = RedFavorite)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = item.title.ifBlank { item.fileName },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCatalogList = false }) { Text(t("بستن")) }
            }
        )
    }
    viewingCatalog?.let { item ->
        CatalogViewerDialog(
            vendorName = vendor.name,
            catalogTitle = item.title,
            catalogLocalPath = item.path,
            catalogPdfUrl = if (item.path.isBlank()) vendor.catalogPdfUrl else "",
            onDismiss = { viewingCatalog = null }
        )
    }

    // Personnel Dialog (Add / Edit)
    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = {
                Text(
                    text = if (editingContact != null) t("ویرایش پرسنل") else t("افزودن پرسنل به وندور"),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { contactName = it },
                        label = { Text(t("نام و نام خانوادگی *")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    GenderSelector(gender = contactGender, onChange = { contactGender = it })
                    OutlinedTextField(
                        value = contactRole,
                        onValueChange = { contactRole = it },
                        label = { Text(t("سمت سازمانی")) },
                        placeholder = { Text(t("مدیرعامل، کارشناس فنی...")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    val contactMobileError = contactMobile.isNotBlank() && !PersianUtils.isValidIranianMobile(contactMobile)
                    OutlinedTextField(
                        value = contactMobile,
                        onValueChange = { contactMobile = PersianUtils.toEnglishDigits(it).filter { ch -> ch.isDigit() }.take(11) },
                        label = { Text(t("شماره همراه")) },
                        placeholder = { Text("09123456789") },
                        singleLine = true,
                        isError = contactMobileError,
                        supportingText = {
                            Text(
                                text = if (contactMobileError) t("شماره همراه باید ۱۱ رقم و با 09 شروع شود") else t("مثال: 09123456789"),
                                fontSize = 10.sp,
                                color = if (contactMobileError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val contactPhoneError = contactPhone.isNotBlank() && contactPhone.filter { it.isDigit() }.length != 11
                        MaskedLandlineField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text(t("تلفن ثابت")) },
                            placeholder = { Text("021-77654321") },
                            singleLine = true,
                            isError = contactPhoneError,
                            supportingText = if (contactPhoneError) {
                                { Text(t("تلفن باید با کد شهر و ۸ رقم باشد (مثال: 021-77654321)"), fontSize = 10.sp, color = MaterialTheme.colorScheme.error) }
                            } else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.5f)
                        )
                        OutlinedTextField(
                            value = contactExt,
                            onValueChange = { contactExt = PersianUtils.toEnglishDigits(it).filter { ch -> ch.isDigit() }.take(6) },
                            label = { Text(t("داخلی")) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = { contactEmail = it },
                        label = { Text(t("ایمیل")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (contactName.isBlank()) {
                            Toast.makeText(context, t("نام شخص الزامی است"), Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if ((contactMobile.isNotBlank() && !PersianUtils.isValidIranianMobile(contactMobile)) ||
                            (contactPhone.isNotBlank() && contactPhone.filter { it.isDigit() }.length != 11)
                        ) {
                            Toast.makeText(context, t("شماره تلفن یا همراه معتبر نیست"), Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val toSave = ContactPerson(
                            id = editingContact?.id ?: 0L,
                            vendorId = vendor.id,
                            name = contactName.trim(),
                            role = contactRole.trim(),
                            mobile = contactMobile.trim(),
                            phone = contactPhone.trim(),
                            extension = contactExt.trim(),
                            email = contactEmail.trim(),
                            gender = contactGender
                        )
                        viewModel.saveContact(toSave)
                        showContactDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
                ) {
                    Text(t("ذخیره"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text(t("انصراف"))
                }
            }
        )
    }

    // Product Dialog (Add / Edit)
    if (showProductDialog) {
        AlertDialog(
            onDismissRequest = { showProductDialog = false },
            title = {
                Text(
                    text = if (editingProduct != null) t("ویرایش محصول") else t("افزودن محصول جدید"),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = prodName,
                        onValueChange = { prodName = it },
                        label = { Text(t("نام محصول *")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = prodBrand,
                            onValueChange = { prodBrand = it },
                            label = { Text(t("برند")) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = prodModel,
                            onValueChange = { prodModel = it },
                            label = { Text(t("مدل")) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = prodCode,
                        onValueChange = { prodCode = it },
                        label = { Text(t("کد فنی استاندارد")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = prodSpecs,
                        onValueChange = { prodSpecs = it },
                        label = { Text(t("مشخصات فنی و هیدرولیکی")) },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = prodDesc,
                        onValueChange = { prodDesc = it },
                        label = { Text(t("توضیحات کاربردی")) },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (prodName.isBlank()) {
                            Toast.makeText(context, t("نام محصول الزامی است"), Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val toSave = Product(
                            id = editingProduct?.id ?: 0L,
                            vendorId = vendor.id,
                            name = prodName.trim(),
                            brand = prodBrand.trim(),
                            model = prodModel.trim(),
                            technicalCode = prodCode.trim(),
                            specifications = prodSpecs.trim(),
                            description = prodDesc.trim(),
                            category = prodCat.trim(),
                            isFavorite = editingProduct?.isFavorite ?: false
                        )
                        viewModel.saveProduct(toSave)
                        showProductDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
                ) {
                    Text(t("ذخیره محصول"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showProductDialog = false }) {
                    Text(t("انصراف"))
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(t("حذف وندور")) },
            text = { Text(tf("آیا از حذف شرکت «{0}» از لیست وندورها اطمینان دارید؟ تمامی پرسنل و محصولات این وندور نیز حذف خواهند شد.", vendor.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteVendor(vendor.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(t("حذف قطعی"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(t("انصراف"))
                }
            }
        )
    }

    // Internal Notes Dialog
    if (showNotesDialog) {
        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text(t("ویرایش یادداشت محرمانه")) },
            text = {
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text(t("یادداشت‌های داخلی")) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateVendorNotes(vendor.id, notesText)
                        showNotesDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
                ) {
                    Text(t("ذخیره یادداشت"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) {
                    Text(t("انصراف"))
                }
            }
        )
    }

    // Request Inquiry Dialog
    if (showInquiryDialog) {
        AlertDialog(
            onDismissRequest = { showInquiryDialog = false },
            title = { Text(t("درخواست استعلام قیمت و کاتالوگ")) },
            text = {
                Text(tf("درخواست استعلام قیمت با اطلاعات تماس شما برای {0} ارسال خواهد شد. همچنین می‌توانید مستقیماً از طریق پیام‌رسان‌ها پیام دهید.", vendor.name))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInquiryDialog = false
                        val activePhone = vendor.mobile.ifBlank { vendor.phone }
                        MessengerUtils.openWhatsApp(context, activePhone, "با سلام، احتراماً درخواست استعلام قیمت و کاتالوگ محصولات شرکت ${vendor.name} را دارم.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
                ) {
                    Text(t("ارسال در واتس‌اپ"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showInquiryDialog = false }) {
                    Text(t("بستن"))
                }
            }
        )
    }
}

@Composable
private fun LegalCodeBadge(label: String, code: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = PersianUtils.toPersianDigits(code), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MessengerTile(label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Send, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ContactDetailRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = PersianUtils.toPersianDigits(value), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}
