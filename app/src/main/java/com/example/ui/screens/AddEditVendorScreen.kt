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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
import com.example.ui.screens.vendor_form.Step1BaseInfo
import com.example.ui.screens.vendor_form.Step2LegalFinancial
import com.example.ui.screens.vendor_form.Step3ContactsPersonnel
import com.example.ui.screens.vendor_form.Step4LocationMap
import com.example.ui.screens.vendor_form.Step5ProductsCatalog
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

    val coroutineScope = rememberCoroutineScope()
    var currentStep by rememberSaveable { mutableStateOf(1) }

    // Intercept back button to step back through wizard or navigate back cleanly
    BackHandler {
        if (currentStep > 1) {
            currentStep--
        } else {
            onBack()
        }
    }

    // Step 1: Base info & Logo
    var name by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.name ?: "") }
    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var nationalIdError by rememberSaveable { mutableStateOf<String?>(null) }
    var mobileError by rememberSaveable { mutableStateOf<String?>(null) }
    var ceoName by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.ceoName ?: "") }
    var activityType by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.activityType ?: "تولیدکننده") }
    var description by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.description ?: "") }
    var logoUri by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.logoUri ?: "") }

    var selectedCategories by remember(editingVendor) {
        mutableStateOf(
            editingVendor?.categories?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.toSet()
                ?: emptySet()
        )
    }
    var tagsText by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.tags ?: "") }

    // Step 2: Legal & Financial
    var economicCode by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.economicCode ?: "") }
    var nationalId by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.nationalId ?: "") }
    var registrationNumber by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.registrationNumber ?: "") }
    val bankAccountsList = remember(editingVendor) {
        mutableStateListOf<BankAccount>().apply {
            if (editingVendor != null) {
                addAll(editingVendor!!.getBankAccountsList())
            }
        }
    }

    // Duplicate warning state
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var duplicateFoundVendor by remember { mutableStateOf<Vendor?>(null) }
    var pendingVendorToSave by remember { mutableStateOf<Vendor?>(null) }

    // Step 3: Phones, Mobiles & Personnel
    var primaryPhone by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.phone ?: "") }
    val additionalPhones = remember(editingVendor) {
        mutableStateListOf<String>().apply {
            if (editingVendor != null && editingVendor!!.secondaryPhones.isNotBlank()) {
                addAll(editingVendor!!.secondaryPhones.split(",", "،").map { it.trim() }.filter { it.isNotBlank() })
            }
        }
    }
    var primaryMobile by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.mobile ?: "") }
    val additionalMobiles = remember(editingVendor) {
        mutableStateListOf<String>().apply {
            if (editingVendor != null && editingVendor!!.additionalMobiles.isNotBlank()) {
                addAll(editingVendor!!.additionalMobiles.split(",", "،").map { it.trim() }.filter { it.isNotBlank() })
            }
        }
    }
    var email by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.email ?: "") }
    var website by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.website ?: "") }

    // Personnel in this Vendor
    val personnelList = remember(editingVendor) {
        mutableStateListOf<ContactPerson>()
    }
    // Load existing personnel for this vendor if edit
    val existingContactsFlow = remember(editingVendor?.id) {
        if (editingVendor != null) viewModel.getContactsForVendor(editingVendor!!.id) else null
    }
    val existingContacts by (if (existingContactsFlow != null) {
        existingContactsFlow.collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<ContactPerson>()) }
    })

    var hasLoadedInitialPersonnel by remember { mutableStateOf(false) }
    if (!hasLoadedInitialPersonnel && existingContacts.isNotEmpty()) {
        personnelList.clear()
        personnelList.addAll(existingContacts)
        hasLoadedInitialPersonnel = true
    }

    // Step 4: Location & Map
    var province by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.province ?: "تهران") }
    var city by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.city ?: "تهران") }
    var address by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.address ?: "") }
    var latitude by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.latitude ?: 35.73576) }
    var longitude by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.longitude ?: 51.44678) }

    // Step 5: Products & Catalog
    val vendorProductsList = remember(editingVendor) { mutableStateListOf<Product>() }
    val existingProductsFlow = remember(editingVendor?.id) {
        if (editingVendor != null) viewModel.getProductsForVendor(editingVendor!!.id) else null
    }
    val existingProducts by (if (existingProductsFlow != null) {
        existingProductsFlow.collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<Product>()) }
    })

    var hasLoadedInitialProducts by remember { mutableStateOf(false) }
    if (!hasLoadedInitialProducts && existingProducts.isNotEmpty()) {
        vendorProductsList.clear()
        vendorProductsList.addAll(existingProducts)
        hasLoadedInitialProducts = true
    }

    var catalogTitle by rememberSaveable(editingVendor) {
        mutableStateOf(editingVendor?.catalogTitle ?: "")
    }
    var catalogLocalPath by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.catalogLocalPath ?: "") }
    var catalogFileName by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.catalogFileName ?: "") }
    var notes by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.notes ?: "") }
    var isVerified by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.isVerified ?: false) }
    var rating by rememberSaveable(editingVendor) { mutableStateOf(editingVendor?.rating ?: 0f) }

    var showTestCatalogDialog by remember { mutableStateOf(false) }

    // Dialog state for adding/editing personnel
    var showPersonDialog by remember { mutableStateOf(false) }
    var editingPersonIndex by remember { mutableStateOf(-1) }
    var personNameInput by remember { mutableStateOf("") }
    var personRoleInput by remember { mutableStateOf("مدیر فروش") }
    var personMobileInput by remember { mutableStateOf("") }
    var personPhoneInput by remember { mutableStateOf("") }
    var personExtInput by remember { mutableStateOf("") }
    var personEmailInput by remember { mutableStateOf("") }

    // Dialog state for adding/editing product
    var showProductDialog by remember { mutableStateOf(false) }
    var editingProductIndex by remember { mutableStateOf(-1) }
    var prodNameInput by remember { mutableStateOf("") }
    var prodBrandInput by remember { mutableStateOf("") }
    var prodModelInput by remember { mutableStateOf("") }
    var prodCodeInput by remember { mutableStateOf("") }
    var prodSpecsInput by remember { mutableStateOf("") }
    var prodDescInput by remember { mutableStateOf("") }
    var prodCategoryInput by remember { mutableStateOf("") }
    var prodImageUriInput by remember { mutableStateOf("") }

    // Photo picker for product image
    val productPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val prodDir = File(context.filesDir, "product_images")
                if (!prodDir.exists()) prodDir.mkdirs()
                val imgFile = File(prodDir, "prod_${System.currentTimeMillis()}.png")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(imgFile).use { output ->
                        input.copyTo(output)
                    }
                }
                prodImageUriInput = imgFile.absolutePath
                Toast.makeText(context, "تصویر محصول انتخاب شد", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "خطا در ذخیره تصویر محصول", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                        1 -> Step1BaseInfo(
                            name = name,
                            onNameChange = {
                                name = it
                                if (it.isNotBlank()) nameError = null
                            },
                            nameError = nameError,
                            ceoName = ceoName,
                            onCeoNameChange = { ceoName = it },
                            logoUri = logoUri,
                            onLogoUriChange = { logoUri = it },
                            onPickLogo = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            activityType = activityType,
                            onActivityTypeChange = { activityType = it },
                            allActivityTypes = allActivityTypes,
                            selectedCategories = selectedCategories,
                            onSelectedCategoriesChange = { selectedCategories = it },
                            allCategories = allCategories,
                            description = description,
                            onDescriptionChange = { description = it }
                        )
                        2 -> Step2LegalFinancial(
                            economicCode = economicCode,
                            onEconomicCodeChange = { economicCode = it },
                            nationalId = nationalId,
                            onNationalIdChange = {
                                nationalId = it
                                if (nationalIdError != null) nationalIdError = null
                            },
                            nationalIdError = nationalIdError,
                            registrationNumber = registrationNumber,
                            onRegistrationNumberChange = { registrationNumber = it },
                            bankAccountsList = bankAccountsList
                        )
                        3 -> Step3ContactsPersonnel(
                            primaryPhone = primaryPhone,
                            onPrimaryPhoneChange = { primaryPhone = it },
                            additionalPhones = additionalPhones,
                            primaryMobile = primaryMobile,
                            onPrimaryMobileChange = {
                                primaryMobile = it
                                if (mobileError != null) mobileError = null
                            },
                            mobileError = mobileError,
                            additionalMobiles = additionalMobiles,
                            email = email,
                            onEmailChange = { email = it },
                            website = website,
                            onWebsiteChange = { website = it },
                            personnelList = personnelList,
                            onAddPersonnelClick = {
                                editingPersonIndex = -1
                                personNameInput = ""
                                personRoleInput = "مدیرعامل"
                                personMobileInput = ""
                                personPhoneInput = primaryPhone
                                personExtInput = "101"
                                personEmailInput = ""
                                showPersonDialog = true
                            },
                            onEditPersonnelClick = { index, person ->
                                editingPersonIndex = index
                                personNameInput = person.name
                                personRoleInput = person.role
                                personMobileInput = person.mobile
                                personPhoneInput = person.phone
                                personExtInput = person.extension
                                personEmailInput = person.email
                                showPersonDialog = true
                            }
                        )
                        4 -> Step4LocationMap(
                            name = name,
                            latitude = latitude,
                            longitude = longitude,
                            onLocationChange = { lat, lng, prov, cty ->
                                latitude = lat
                                longitude = lng
                                if (prov != null) province = prov
                                if (cty != null) city = cty
                            },
                            province = province,
                            onProvinceChange = { province = it },
                            city = city,
                            onCityChange = { city = it },
                            address = address,
                            onAddressChange = { address = it }
                        )
                        5 -> Step5ProductsCatalog(
                            vendorProductsList = vendorProductsList,
                            onAddProductClick = {
                                editingProductIndex = -1
                                prodNameInput = ""
                                prodBrandInput = name
                                prodModelInput = ""
                                prodCodeInput = ""
                                prodSpecsInput = ""
                                prodDescInput = ""
                                prodCategoryInput = "پمپ‌های سانتریفیوژ"
                                prodImageUriInput = ""
                                showProductDialog = true
                            },
                            onEditProductClick = { index, prod ->
                                editingProductIndex = index
                                prodNameInput = prod.name
                                prodBrandInput = prod.brand
                                prodModelInput = prod.model
                                prodCodeInput = prod.technicalCode
                                prodSpecsInput = prod.specifications
                                prodDescInput = prod.description
                                prodCategoryInput = prod.category
                                prodImageUriInput = prod.imageDrawable
                                showProductDialog = true
                            },
                            catalogTitle = catalogTitle,
                            onCatalogTitleChange = { catalogTitle = it },
                            catalogLocalPath = catalogLocalPath,
                            onUploadPdfClick = {
                                docPickerLauncher.launch("application/pdf")
                            },
                            onTestCatalogClick = {
                                showTestCatalogDialog = true
                            },
                            notes = notes,
                            onNotesChange = { notes = it },
                            isVerified = isVerified,
                            onVerifiedChange = { isVerified = it },
                            rating = rating,
                            onRatingChange = { rating = it }
                        )
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مرحله قبل")
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (currentStep < 5) {
                    Button(
                        onClick = {
                            if (currentStep == 1) {
                                if (name.isBlank()) {
                                    nameError = "لطفاً نام شرکت را وارد نمایید"
                                    return@Button
                                }
                                nameError = null
                            } else if (currentStep == 2) {
                                val nat = nationalId.trim()
                                if (nat.isNotBlank() && !PersianUtils.isValidNationalIdOrCode(nat)) {
                                    nationalIdError = "شناسه ملی یا کد ملی نامعتبر است"
                                    return@Button
                                }
                                nationalIdError = null
                            } else if (currentStep == 3) {
                                val mob = primaryMobile.trim()
                                if (mob.isNotBlank() && !PersianUtils.isValidIranianMobile(mob)) {
                                    mobileError = "شماره همراه باید ۱۱ رقم و با ۰۹ آغاز شود"
                                    return@Button
                                }
                                mobileError = null
                            }
                            currentStep++
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
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "لطفاً نام شرکت را وارد نمایید", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Validation for Iranian mobile (09...)
                            if (primaryMobile.isNotBlank() && !PersianUtils.isValidIranianMobile(primaryMobile)) {
                                Toast.makeText(context, "شماره موبایل نامعتبر است (مثال: ۰۹۱۲۳۴۵۶۷۸۹)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Validation for National Code / Company National ID
                            if (nationalId.isNotBlank() && !PersianUtils.isValidNationalIdOrCode(nationalId)) {
                                Toast.makeText(context, "کد ملی یا شناسه ملی نامعتبر است", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val serializedAccounts = BankAccountUtils.serialize(bankAccountsList)
                            val currentEditing = editingVendor

                            val vendorToSave = if (currentEditing != null) {
                                currentEditing.copy(
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
                                    rating = rating
                                )
                            } else {
                                Vendor(
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
                                    isFavorite = false,
                                    createdAt = System.currentTimeMillis()
                                )
                            }

                            // Check duplicate before saving
                            val checkId = currentEditing?.id ?: 0L
                            coroutineScope.launch {
                                val duplicate = viewModel.checkDuplicateVendor(
                                    name = name.trim(),
                                    nationalId = nationalId.trim(),
                                    mobile = primaryMobile.trim(),
                                    currentId = checkId
                                )
                                if (duplicate != null) {
                                    pendingVendorToSave = vendorToSave
                                    duplicateFoundVendor = duplicate
                                    showDuplicateDialog = true
                                } else {
                                    viewModel.saveVendorWithRelations(
                                        vendor = vendorToSave,
                                        contacts = personnelList.toList(),
                                        products = vendorProductsList.toList(),
                                        onComplete = {
                                            onBack()
                                        }
                                    )
                                }
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

                    // Product Image Picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (prodImageUriInput.isNotBlank() && File(prodImageUriInput).exists()) {
                                AsyncImage(
                                    model = File(prodImageUriInput),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Inventory, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                productPhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (prodImageUriInput.isNotBlank()) "تغییر تصویر" else "انتخاب تصویر محصول", fontSize = 11.sp)
                        }
                        if (prodImageUriInput.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            OutlinedButton(
                                onClick = { prodImageUriInput = "" },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("حذف", fontSize = 11.sp, color = RedFavorite)
                            }
                        }
                    }
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
                            imageDrawable = prodImageUriInput.ifBlank { "industrial_pump_system" },
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

    // Duplicate Vendor Warning Dialog
    if (showDuplicateDialog && duplicateFoundVendor != null) {
        AlertDialog(
            onDismissRequest = { showDuplicateDialog = false },
            title = {
                Text("هشدار ثبت وندور تکراری", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "وندوری با اطلاعات مشابه (نام، شناسه ملی یا شماره موبایل) با عنوان «${duplicateFoundVendor?.name}» از قبل در سامانه ثبت شده است.\nآیا با وجود تشابه، مایل به ذخیره‌سازی این وندور هستید؟",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDuplicateDialog = false
                        pendingVendorToSave?.let { vendorToSave ->
                            viewModel.saveVendorWithRelations(
                                vendor = vendorToSave,
                                contacts = personnelList.toList(),
                                products = vendorProductsList.toList(),
                                onComplete = {
                                    onBack()
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = NavyDeep
                    )
                ) {
                    Text("ثبت با این حال", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDuplicateDialog = false }) {
                    Text("انصراف و ویرایش")
                }
            }
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
