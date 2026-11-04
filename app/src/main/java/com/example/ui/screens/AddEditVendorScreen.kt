package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BankAccount
import com.example.data.model.BankAccountUtils
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.ui.components.AppHeader
import com.example.ui.components.CatalogFileUtils
import com.example.ui.components.CatalogViewerDialog
import com.example.ui.components.InteractiveMapPicker
import com.example.ui.theme.AmberRating
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.VendorViewModel
import androidx.activity.compose.BackHandler
import com.example.ui.theme.GreenVerified
import com.example.util.ShebaUtils
import com.example.util.PersianUtils
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditVendorScreen(
    viewModel: VendorViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val editingVendor by viewModel.editingVendor.collectAsState()
    val isEdit = editingVendor != null

    val allActivityTypes by viewModel.allActivityTypes.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()

    var currentStep by remember { mutableIntStateOf(1) }

    // Intercept back button to step back through wizard or navigate back cleanly
    BackHandler {
        if (currentStep > 1) {
            currentStep--
        } else {
            onBack()
        }
    }

    // Step 1: Base info & Logo
    var name by remember(editingVendor) { mutableStateOf(editingVendor?.name ?: "") }
    var ceoName by remember(editingVendor) { mutableStateOf(editingVendor?.ceoName ?: "") }
    var activityType by remember(editingVendor) { mutableStateOf(editingVendor?.activityType ?: "تولیدکننده") }
    var description by remember(editingVendor) { mutableStateOf(editingVendor?.description ?: "") }
    var logoUri by remember(editingVendor) { mutableStateOf(editingVendor?.logoUri ?: "") }

    var selectedCategories by remember(editingVendor) {
        mutableStateOf(
            editingVendor?.categories?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.toSet()
                ?: setOf("صنعتی", "استیل")
        )
    }
    var tagsText by remember(editingVendor) { mutableStateOf(editingVendor?.tags ?: "تأمین مستقیم, گارانتی معتبر") }

    // Step 2: Legal & Financial (کد اقتصادی، شناسه ملی، شماره ثبت، حساب‌های بانکی)
    var economicCode by remember(editingVendor) { mutableStateOf(editingVendor?.economicCode ?: "") }
    var nationalId by remember(editingVendor) { mutableStateOf(editingVendor?.nationalId ?: "") }
    var registrationNumber by remember(editingVendor) { mutableStateOf(editingVendor?.registrationNumber ?: "") }
    val bankAccountsList = remember(editingVendor) {
        mutableStateListOf<BankAccount>().apply {
            if (editingVendor != null) {
                addAll(editingVendor!!.getBankAccountsList())
            }
            if (isEmpty()) {
                add(BankAccount("بانک ملت", "", "", "مرکزی"))
            }
        }
    }

    // Step 3: Phones, Mobiles & Personnel
    var primaryPhone by remember(editingVendor) { mutableStateOf(editingVendor?.phone ?: "") }
    val additionalPhones = remember(editingVendor) {
        mutableStateListOf<String>().apply {
            if (editingVendor != null && editingVendor!!.secondaryPhones.isNotBlank()) {
                addAll(editingVendor!!.secondaryPhones.split(",", "،").map { it.trim() }.filter { it.isNotBlank() })
            }
        }
    }
    var primaryMobile by remember(editingVendor) { mutableStateOf(editingVendor?.mobile ?: "") }
    val additionalMobiles = remember(editingVendor) {
        mutableStateListOf<String>().apply {
            if (editingVendor != null && editingVendor!!.additionalMobiles.isNotBlank()) {
                addAll(editingVendor!!.additionalMobiles.split(",", "،").map { it.trim() }.filter { it.isNotBlank() })
            }
        }
    }
    var email by remember(editingVendor) { mutableStateOf(editingVendor?.email ?: "") }
    var website by remember(editingVendor) { mutableStateOf(editingVendor?.website ?: "") }

    // Personnel in this Vendor
    val personnelList = remember(editingVendor) {
        mutableStateListOf<ContactPerson>()
    }
    // Load existing personnel for this vendor if edit
    val existingContactsFlow = remember(editingVendor?.id) {
        if (editingVendor != null) viewModel.getContactsForVendor(editingVendor!!.id) else null
    }
    val existingContacts by (existingContactsFlow?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) })

    var hasLoadedInitialPersonnel by remember { mutableStateOf(false) }
    if (!hasLoadedInitialPersonnel && existingContacts.isNotEmpty()) {
        personnelList.clear()
        personnelList.addAll(existingContacts)
        hasLoadedInitialPersonnel = true
    }

    // Step 4: Location & Map
    var province by remember(editingVendor) { mutableStateOf(editingVendor?.province ?: "تهران") }
    var city by remember(editingVendor) { mutableStateOf(editingVendor?.city ?: "تهران") }
    var address by remember(editingVendor) { mutableStateOf(editingVendor?.address ?: "") }
    var latitude by remember(editingVendor) { mutableDoubleStateOf(editingVendor?.latitude ?: 35.73576) }
    var longitude by remember(editingVendor) { mutableDoubleStateOf(editingVendor?.longitude ?: 51.44678) }

    // Step 5: Products & Catalog
    val vendorProductsList = remember(editingVendor) { mutableStateListOf<Product>() }
    val existingProductsFlow = remember(editingVendor?.id) {
        if (editingVendor != null) viewModel.getProductsForVendor(editingVendor!!.id) else null
    }
    val existingProducts by (existingProductsFlow?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) })

    var hasLoadedInitialProducts by remember { mutableStateOf(false) }
    if (!hasLoadedInitialProducts && existingProducts.isNotEmpty()) {
        vendorProductsList.clear()
        vendorProductsList.addAll(existingProducts)
        hasLoadedInitialProducts = true
    }

    var catalogTitle by remember(editingVendor) {
        mutableStateOf(editingVendor?.catalogTitle ?: "کاتالوگ جامع محصولات و مشخصات فنی")
    }
    var catalogLocalPath by remember(editingVendor) { mutableStateOf(editingVendor?.catalogLocalPath ?: "") }
    var catalogFileName by remember(editingVendor) { mutableStateOf(editingVendor?.catalogFileName ?: "") }
    var notes by remember(editingVendor) { mutableStateOf(editingVendor?.notes ?: "") }
    var isVerified by remember(editingVendor) { mutableStateOf(editingVendor?.isVerified ?: true) }
    var rating by remember(editingVendor) { mutableFloatStateOf(editingVendor?.rating ?: 4.5f) }

    var showTestCatalogDialog by remember { mutableStateOf(false) }

    // Dialog state for adding/editing personnel
    var showPersonDialog by remember { mutableStateOf(false) }
    var editingPersonIndex by remember { mutableIntStateOf(-1) }
    var personNameInput by remember { mutableStateOf("") }
    var personRoleInput by remember { mutableStateOf("مدیر فروش") }
    var personMobileInput by remember { mutableStateOf("") }
    var personPhoneInput by remember { mutableStateOf("") }
    var personExtInput by remember { mutableStateOf("") }
    var personEmailInput by remember { mutableStateOf("") }

    // Dialog state for adding/editing product
    var showProductDialog by remember { mutableStateOf(false) }
    var editingProductIndex by remember { mutableIntStateOf(-1) }
    var prodNameInput by remember { mutableStateOf("") }
    var prodBrandInput by remember { mutableStateOf("") }
    var prodModelInput by remember { mutableStateOf("") }
    var prodCodeInput by remember { mutableStateOf("") }
    var prodSpecsInput by remember { mutableStateOf("") }
    var prodDescInput by remember { mutableStateOf("") }
    var prodCategoryInput by remember { mutableStateOf("پمپ‌های سانتریفیوژ") }

    // Photo picker for logo
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val logoDir = File(context.filesDir, "vendor_logos")
                if (!logoDir.exists()) logoDir.mkdirs()
                val logoFile = File(logoDir, "logo_${System.currentTimeMillis()}.png")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(logoFile).use { output ->
                        input.copyTo(output)
                    }
                }
                logoUri = logoFile.absolutePath
                Toast.makeText(context, "لوگوی شرکت با موفقیت ثبت شد", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "خطا در ذخیره لوگو", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Document picker for catalog PDF
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val catDir = File(context.filesDir, "catalogs")
                if (!catDir.exists()) catDir.mkdirs()
                val pdfFile = File(catDir, "catalog_${System.currentTimeMillis()}.pdf")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(pdfFile).use { output ->
                        input.copyTo(output)
                    }
                }
                catalogLocalPath = pdfFile.absolutePath
                catalogFileName = "کاتالوگ_${name.ifBlank { "شرکت" }}.pdf"
                Toast.makeText(context, "فایل کاتالوگ با موفقیت بارگذاری شد", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "خطا در بارگذاری فایل کاتالوگ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(
            title = if (isEdit) "ویرایش اطلاعات وندور" else "افزودن وندور جدید",
            subtitle = if (isEdit) name else "ثبت اطلاعات در پایگاه داده آفلاین",
            onBack = onBack
        )

        // Wizard Stepper Indicator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepBadge(step = 1, currentStep = currentStep, label = "پایه و لوگو")
                StepDivider(isPassed = currentStep > 1)
                StepBadge(step = 2, currentStep = currentStep, label = "ثبتی و بانک")
                StepDivider(isPassed = currentStep > 2)
                StepBadge(step = 3, currentStep = currentStep, label = "تماس و پرسنل")
                StepDivider(isPassed = currentStep > 3)
                StepBadge(step = 4, currentStep = currentStep, label = "نقشه و آدرس")
                StepDivider(isPassed = currentStep > 4)
                StepBadge(step = 5, currentStep = currentStep, label = "محصول و کاتالوگ")
            }
        }

        // Animated Form Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(
                            slideOutHorizontally { -it } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(
                            slideOutHorizontally { it } + fadeOut()
                        )
                    }
                },
                label = "FormStepAnimation"
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (step) {
                        1 -> {
                            // STEP 1: Basic Info, Activity Type & Logo
                            Text(
                                text = "اطلاعات پایه، نشان تجاری و لوگو",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            // Logo Card with Photo Picker & Badges
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Logo preview box
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                Brush.linearGradient(listOf(NavyDeep, TealDark))
                                            )
                                            .border(2.dp, TealPrimary, RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (logoUri.isNotBlank() && File(logoUri).exists()) {
                                            AsyncImage(
                                                model = File(logoUri),
                                                contentDescription = "لوگوی وندور",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Text(
                                                text = if (name.isNotBlank()) name.take(2) else "لوگو",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "لوگو و نشان تجاری شرکت",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "امکان انتخاب تصویر از گالری یا نماد اختصاصی",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    photoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = TealPrimary,
                                                    contentColor = NavyDeep
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (logoUri.isNotBlank()) "تغییر لوگو" else "انتخاب لوگو", fontSize = 11.sp)
                                            }

                                            if (logoUri.isNotBlank()) {
                                                OutlinedButton(
                                                    onClick = { logoUri = "" },
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("حذف لوگو", fontSize = 11.sp, color = RedFavorite)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("نام شرکت یا وندور *") },
                                placeholder = { Text("مثال: بازرگانی پارس تجهیز") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("vendor_name_input")
                            )

                            OutlinedTextField(
                                value = ceoName,
                                onValueChange = { ceoName = it },
                                label = { Text("نام مدیرعامل یا مسئول ارتباط") },
                                placeholder = { Text("مثال: مجید محمودی") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Dynamic Activity Types from Room Database
                            Text(
                                text = "نوع فعالیت شرکت (قابل انتخاب و مدیریت):",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val currentTypesList = if (allActivityTypes.isNotEmpty()) {
                                    allActivityTypes.map { it.name }
                                } else {
                                    listOf("تولیدکننده", "واردکننده", "بازرگانی", "توزیع‌کننده مستقیم", "خدمات فنی و مهندسی", "پیمانکار صنعتی")
                                }

                                currentTypesList.forEach { opt ->
                                    val isSelected = activityType == opt
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { activityType = opt },
                                        label = { Text(opt, style = MaterialTheme.typography.labelSmall) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = TealPrimary,
                                            selectedLabelColor = NavyDeep
                                        )
                                    )
                                }
                            }

                            // Dynamic Categories from Room Database
                            Text(
                                text = "زمینه‌های فعالیت و شاخه‌ها:",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val currentCatNames = if (allCategories.isNotEmpty()) {
                                    allCategories.map { it.name }
                                } else {
                                    listOf("صنعتی", "ساختمان و عمرانی", "ماشین‌آلات", "مواد شیمیایی", "برق و الکترونیک", "پمپ", "استیل", "بازرگانی")
                                }

                                currentCatNames.forEach { cat ->
                                    val isSelected = selectedCategories.contains(cat)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedCategories = if (isSelected) selectedCategories - cat else selectedCategories + cat
                                        },
                                        label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = NavyDeep) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = TealPrimary,
                                            selectedLabelColor = NavyDeep
                                        )
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("توضیحات و زمینه تخصصی") },
                                placeholder = { Text("معرفی خدمات، استانداردهای بین‌المللی، برندهای قابل تأمین...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }

                        2 -> {
                            // STEP 2: Legal & Financial Information (کد اقتصادی، شناسه ملی، شماره ثبت، حساب‌های بانکی)
                            Text(
                                text = "اطلاعات حقوقی، ثبتی و حساب‌های بانکی",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            OutlinedTextField(
                                value = economicCode,
                                onValueChange = { economicCode = it },
                                label = { Text("کد اقتصادی (۱۲ رقمی)") },
                                placeholder = { Text("411354897621") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = nationalId,
                                    onValueChange = { nationalId = it },
                                    label = { Text("شناسه ملی (۱۱ رقمی)") },
                                    placeholder = { Text("10103487920") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = registrationNumber,
                                    onValueChange = { registrationNumber = it },
                                    label = { Text("شماره ثبت شرکت") },
                                    placeholder = { Text("284910") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Multiple Bank Accounts Management
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "حساب‌های بانکی و شماره شبا (${PersianUtils.toPersianDigits(bankAccountsList.size)} حساب)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Beautiful Styled Add Account Button
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        bankAccountsList.add(BankAccount("", "", "", ""))
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = TealPrimary.copy(alpha = 0.12f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimary.copy(alpha = 0.45f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = TealDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "افزودن حساب بانکی و شماره شبا جدید",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = TealDark
                                    )
                                }
                            }

                            bankAccountsList.forEachIndexed { index, account ->
                                val shebaValidation = remember(account.iban) {
                                    if (account.iban.isNotBlank()) ShebaUtils.validateSheba(account.iban) else null
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "حساب شماره ${PersianUtils.toPersianDigits(index + 1)}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            if (bankAccountsList.size > 1) {
                                                IconButton(
                                                    onClick = { bankAccountsList.removeAt(index) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = RedFavorite, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = account.bankName,
                                                onValueChange = {
                                                    bankAccountsList[index] = account.copy(bankName = it)
                                                },
                                                label = { Text("نام بانک") },
                                                placeholder = { Text("ملت، صادرات، ملی...") },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )

                                            OutlinedTextField(
                                                value = account.branch,
                                                onValueChange = {
                                                    bankAccountsList[index] = account.copy(branch = it)
                                                },
                                                label = { Text("نام یا کد شعبه") },
                                                placeholder = { Text("شعبه مرکزی") },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        OutlinedTextField(
                                            value = account.accountNumber,
                                            onValueChange = {
                                                bankAccountsList[index] = account.copy(accountNumber = it)
                                            },
                                            label = { Text("شماره حساب") },
                                            placeholder = { Text("123456789") },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        OutlinedTextField(
                                            value = account.iban,
                                            onValueChange = { input ->
                                                val raw = input.uppercase().replace(" ", "")
                                                val formatted = if (!raw.startsWith("IR") && raw.any { it.isDigit() }) {
                                                    "IR" + raw.filter { it.isDigit() }
                                                } else {
                                                    raw
                                                }
                                                val validation = ShebaUtils.validateSheba(formatted)
                                                val autoBank = validation.detectedBankName
                                                val newBankName = if (account.bankName.isBlank() && autoBank != null) {
                                                    autoBank
                                                } else {
                                                    account.bankName
                                                }
                                                bankAccountsList[index] = account.copy(
                                                    iban = formatted,
                                                    bankName = newBankName
                                                )
                                            },
                                            label = { Text("شماره شبا (IBAN)") },
                                            placeholder = { Text("IR120120000000001234567890") },
                                            singleLine = true,
                                            isError = shebaValidation != null && !shebaValidation.isValid && account.iban.length > 5,
                                            supportingText = {
                                                if (shebaValidation != null) {
                                                    if (!shebaValidation.isValid && account.iban.length > 5) {
                                                        Text(
                                                            text = shebaValidation.errorMessage ?: "شماره شبا نامعتبر است",
                                                            color = MaterialTheme.colorScheme.error,
                                                            fontSize = 11.sp
                                                        )
                                                    } else if (shebaValidation.isValid) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = null,
                                                                tint = GreenVerified,
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(
                                                                text = "شماره شبا معتبر است (${shebaValidation.detectedBankName ?: "بانک ایرانی"})",
                                                                color = GreenVerified,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.AccountBalance,
                                                    contentDescription = null,
                                                    tint = if (shebaValidation?.isValid == true) GreenVerified else MaterialTheme.colorScheme.primary
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        3 -> {
                            // STEP 3: Multiple Phones, Mobiles & Personnel
                            Text(
                                text = "شماره‌های تماس و پرسنل ارتباطی",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            // Multiple Landline Phones
                            Text(
                                text = "شماره‌های تلفن ثابت شرکت:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = primaryPhone,
                                onValueChange = { primaryPhone = it },
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

                            TextButton(
                                onClick = { additionalPhones.add("") },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("افزودن خط ثابت دیگر")
                            }

                            // Multiple Mobile Phones
                            Text(
                                text = "شماره‌های همراه و پیام‌رسان‌ها (واتس‌اپ، بله، روبیکا، تلگرام):",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = primaryMobile,
                                onValueChange = { primaryMobile = it },
                                label = { Text("شماره همراه اصلی") },
                                placeholder = { Text("0912-3456789") },
                                singleLine = true,
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

                            TextButton(
                                onClick = { additionalMobiles.add("") },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("افزودن شماره همراه دیگر")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("پست الکترونیک (ایمیل)") },
                                    placeholder = { Text("info@company.com") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = website,
                                    onValueChange = { website = it },
                                    label = { Text("وبسایت رسمی") },
                                    placeholder = { Text("www.company.com") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Dedicated Personnel Section in Vendor Creation/Edit
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
                                    onClick = {
                                        editingPersonIndex = -1
                                        personNameInput = ""
                                        personRoleInput = "مدیرعامل"
                                        personMobileInput = ""
                                        personPhoneInput = primaryPhone
                                        personExtInput = "101"
                                        personEmailInput = ""
                                        showPersonDialog = true
                                    },
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
                                                onClick = {
                                                    editingPersonIndex = index
                                                    personNameInput = person.name
                                                    personRoleInput = person.role
                                                    personMobileInput = person.mobile
                                                    personPhoneInput = person.phone
                                                    personExtInput = person.extension
                                                    personEmailInput = person.email
                                                    showPersonDialog = true
                                                }
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

                        4 -> {
                            // STEP 4: Interactive Map, Coords & Address
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
                                onLocationChange = { lat, lng, prov, cty ->
                                    latitude = lat
                                    longitude = lng
                                    if (prov != null) province = prov
                                    if (cty != null) city = cty
                                },
                                vendorTitle = name
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = province,
                                    onValueChange = { province = it },
                                    label = { Text("استان") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("شهر / شهرک صنعتی") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text("آدرس پستی کامل کارخانه یا دفتر مرکزی") },
                                placeholder = { Text("شهرک صنعتی، خیابان صنعت، پلاک...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }

                        5 -> {
                            // STEP 5: Products, Catalog Upload & Rating
                            Text(
                                text = "محصولات، بارگذاری کاتالوگ و ارزیابی",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            // Products Management Section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Inventory, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "محصولات و تجهیزات وندور (${PersianUtils.toPersianDigits(vendorProductsList.size)} محصول)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = {
                                        editingProductIndex = -1
                                        prodNameInput = ""
                                        prodBrandInput = name
                                        prodModelInput = ""
                                        prodCodeInput = ""
                                        prodSpecsInput = ""
                                        prodDescInput = ""
                                        prodCategoryInput = "پمپ‌های سانتریفیوژ"
                                        showProductDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TealPrimary,
                                        contentColor = NavyDeep
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("افزودن محصول", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (vendorProductsList.isEmpty()) {
                                Text(
                                    text = "هنوز محصولی ثبت نشده است. برای این وندور می‌توانید محصولات و کاتالوگ فنی ثبت کنید.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
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
                                                onClick = {
                                                    editingProductIndex = index
                                                    prodNameInput = prod.name
                                                    prodBrandInput = prod.brand
                                                    prodModelInput = prod.model
                                                    prodCodeInput = prod.technicalCode
                                                    prodSpecsInput = prod.specifications
                                                    prodDescInput = prod.description
                                                    prodCategoryInput = prod.category
                                                    showProductDialog = true
                                                }
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = TealPrimary, modifier = Modifier.size(18.dp))
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

                            Spacer(modifier = Modifier.height(6.dp))

                            // Catalog Upload & Management Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(RedFavorite),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "فایل کاتالوگ و بروشور فنی (PDF)",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (catalogLocalPath.isNotBlank()) "فایل بارگذاری شده آماده نمایش است" else "امکان آپلود مستقیم فایل PDF",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (catalogLocalPath.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = catalogTitle,
                                        onValueChange = { catalogTitle = it },
                                        label = { Text("عنوان نمایشی کاتالوگ") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                docPickerLauncher.launch("application/pdf")
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = TealPrimary,
                                                contentColor = NavyDeep
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (catalogLocalPath.isNotBlank()) "تغییر فایل PDF" else "بارگذاری فایل PDF", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                showTestCatalogDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = TealDark,
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("تست و نمایش کاتالوگ", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("یادداشت‌های داخلی و محرمانه") },
                                placeholder = { Text("شرایط پرداخت، سابقه همکاری، نحوه ارسال...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            // Verified toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "تأییدیه رسمی وندور (نمایش نشان سبز اعتبار)",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Switch(
                                    checked = isVerified,
                                    onCheckedChange = { isVerified = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
                                )
                            }

                            // Rating
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("امتیاز و ارزیابی کیفیت:", style = MaterialTheme.typography.titleSmall)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AmberRating, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(PersianUtils.toPersianDigits(String.format("%.1f", rating)), fontWeight = FontWeight.Bold)
                                }
                            }

                            Slider(
                                value = rating,
                                onValueChange = { rating = it },
                                valueRange = 1f..5f,
                                steps = 7,
                                colors = SliderDefaults.colors(thumbColor = TealPrimary, activeTrackColor = TealDark)
                            )
                        }
                    }
                }
            }
        }

        // Stepper Navigation Actions Bottom Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مرحله قبل")
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (currentStep < 5) {
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "لطفاً نام شرکت را وارد نمایید", Toast.LENGTH_SHORT).show()
                            } else {
                                currentStep++
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary,
                            contentColor = NavyDeep
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("next_step_button")
                    ) {
                        Text("مرحله بعد", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "لطفاً نام شرکت را وارد نمایید", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val serializedAccounts = BankAccountUtils.serialize(bankAccountsList)

                            val vendorToSave = Vendor(
                                id = editingVendor?.id ?: 0L,
                                name = name.trim(),
                                ceoName = ceoName.trim(),
                                description = description.trim(),
                                activityType = activityType,
                                categories = selectedCategories.joinToString(", "),
                                tags = tagsText.trim(),
                                phone = primaryPhone.trim(),
                                secondaryPhones = additionalPhones.filter { it.isNotBlank() }.joinToString(", "),
                                mobile = primaryMobile.trim(),
                                additionalMobiles = additionalMobiles.filter { it.isNotBlank() }.joinToString(", "),
                                email = email.trim(),
                                website = website.trim(),
                                province = province.trim(),
                                city = city.trim(),
                                address = address.trim(),
                                latitude = latitude,
                                longitude = longitude,
                                logoUri = logoUri,
                                catalogTitle = catalogTitle.trim(),
                                catalogLocalPath = catalogLocalPath,
                                catalogFileName = catalogFileName,
                                notes = notes.trim(),
                                economicCode = economicCode.trim(),
                                nationalId = nationalId.trim(),
                                registrationNumber = registrationNumber.trim(),
                                bankAccounts = serializedAccounts,
                                isVerified = isVerified,
                                rating = rating,
                                isFavorite = editingVendor?.isFavorite ?: false,
                                createdAt = editingVendor?.createdAt ?: System.currentTimeMillis()
                            )

                            viewModel.saveVendor(vendorToSave) { savedId ->
                                // Save personnel
                                personnelList.forEach { person ->
                                    viewModel.saveContact(person.copy(vendorId = savedId))
                                }
                                // Save products
                                vendorProductsList.forEach { product ->
                                    viewModel.saveProduct(product.copy(vendorId = savedId))
                                }
                                onBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary,
                            contentColor = NavyDeep
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_vendor_button")
                    ) {
                        Text(
                            text = if (isEdit) "ذخیره تغییرات وندور" else "ثبت نهایی وندور",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Dialog for adding/editing personnel
    if (showPersonDialog) {
        AlertDialog(
            onDismissRequest = { showPersonDialog = false },
            title = {
                Text(
                    text = if (editingPersonIndex >= 0) "ویرایش پرسنل شرکت" else "افزودن پرسنل جدید",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = personNameInput,
                        onValueChange = { personNameInput = it },
                        label = { Text("نام و نام خانوادگی *") },
                        placeholder = { Text("مثال: مهندس مجید محمودی") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = personRoleInput,
                        onValueChange = { personRoleInput = it },
                        label = { Text("سمت / عنوان سازمانی") },
                        placeholder = { Text("مدیرعامل، مدیر فروش، کارشناس فنی...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = personMobileInput,
                        onValueChange = { personMobileInput = it },
                        label = { Text("شماره همراه") },
                        placeholder = { Text("0912-3456789") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = personPhoneInput,
                            onValueChange = { personPhoneInput = it },
                            label = { Text("تلفن مستقیم") },
                            placeholder = { Text("021-77654321") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.5f)
                        )
                        OutlinedTextField(
                            value = personExtInput,
                            onValueChange = { personExtInput = it },
                            label = { Text("داخلی") },
                            placeholder = { Text("101") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = personEmailInput,
                        onValueChange = { personEmailInput = it },
                        label = { Text("ایمیل پرسنل") },
                        placeholder = { Text("person@company.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (personNameInput.isBlank()) {
                            Toast.makeText(context, "نام شخص الزامی است", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val newContact = ContactPerson(
                            id = if (editingPersonIndex >= 0) personnelList[editingPersonIndex].id else 0L,
                            vendorId = editingVendor?.id ?: 0L,
                            name = personNameInput.trim(),
                            role = personRoleInput.trim(),
                            mobile = personMobileInput.trim(),
                            phone = personPhoneInput.trim(),
                            extension = personExtInput.trim(),
                            email = personEmailInput.trim()
                        )
                        if (editingPersonIndex >= 0) {
                            personnelList[editingPersonIndex] = newContact
                        } else {
                            personnelList.add(newContact)
                        }
                        showPersonDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                ) {
                    Text("تأیید")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPersonDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Dialog for adding/editing product
    if (showProductDialog) {
        AlertDialog(
            onDismissRequest = { showProductDialog = false },
            title = {
                Text(
                    text = if (editingProductIndex >= 0) "ویرایش محصول" else "افزودن محصول جدید",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = prodNameInput,
                        onValueChange = { prodNameInput = it },
                        label = { Text("نام محصول یا دستگاه *") },
                        placeholder = { Text("پمپ سانتریفیوژ استیل مدل TP-100") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = prodBrandInput,
                            onValueChange = { prodBrandInput = it },
                            label = { Text("برند") },
                            placeholder = { Text("پارس پمپ") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = prodModelInput,
                            onValueChange = { prodModelInput = it },
                            label = { Text("مدل") },
                            placeholder = { Text("TP-100") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = prodCodeInput,
                        onValueChange = { prodCodeInput = it },
                        label = { Text("کد فنی / استاندارد") },
                        placeholder = { Text("PP-TP100-SS316") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = prodSpecsInput,
                        onValueChange = { prodSpecsInput = it },
                        label = { Text("مشخصات فنی و هیدرولیکی") },
                        placeholder = { Text("دبی ۱۲۰ مترمکعب، هد ۶۵ متر، پروانه استیل ۳۱۶") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = prodDescInput,
                        onValueChange = { prodDescInput = it },
                        label = { Text("توضیحات کاربردی") },
                        placeholder = { Text("مخصوص انتقال سیالات خورنده در صنایع شیمیایی") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (prodNameInput.isBlank()) {
                            Toast.makeText(context, "نام محصول الزامی است", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val newProduct = Product(
                            id = if (editingProductIndex >= 0) vendorProductsList[editingProductIndex].id else 0L,
                            vendorId = editingVendor?.id ?: 0L,
                            name = prodNameInput.trim(),
                            brand = prodBrandInput.trim(),
                            model = prodModelInput.trim(),
                            technicalCode = prodCodeInput.trim(),
                            specifications = prodSpecsInput.trim(),
                            description = prodDescInput.trim(),
                            category = prodCategoryInput.trim(),
                            isFavorite = if (editingProductIndex >= 0) vendorProductsList[editingProductIndex].isFavorite else false
                        )
                        if (editingProductIndex >= 0) {
                            vendorProductsList[editingProductIndex] = newProduct
                        } else {
                            vendorProductsList.add(newProduct)
                        }
                        showProductDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                ) {
                    Text("تأیید محصول")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProductDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Dialog for testing catalog
    if (showTestCatalogDialog) {
        CatalogViewerDialog(
            vendorName = name.ifBlank { "شرکت" },
            catalogTitle = catalogTitle,
            catalogLocalPath = catalogLocalPath,
            catalogPdfUrl = "",
            onDismiss = { showTestCatalogDialog = false }
        )
    }
}

@Composable
private fun StepBadge(step: Int, currentStep: Int, label: String) {
    val isDone = currentStep > step
    val isCurrent = currentStep == step

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCurrent -> TealPrimary
                        isDone -> TealDark
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    text = PersianUtils.toPersianDigits(step),
                    color = if (isCurrent) NavyDeep else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCurrent) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun StepDivider(isPassed: Boolean) {
    Box(
        modifier = Modifier
            .width(16.dp)
            .height(2.dp)
            .background(if (isPassed) TealDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    )
}
