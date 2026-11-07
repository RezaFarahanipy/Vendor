package com.example.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.data.model.BankAccount
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.util.PersianUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VendorFormViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Step navigation
    val currentStep = MutableStateFlow(savedStateHandle.get<Int>("current_step") ?: 1)

    fun setStep(step: Int) {
        val bounded = step.coerceIn(1, 5)
        currentStep.value = bounded
        savedStateHandle["current_step"] = bounded
    }

    fun nextStep(): Boolean {
        return if (validateStep(currentStep.value)) {
            setStep(currentStep.value + 1)
            true
        } else {
            false
        }
    }

    fun prevStep(): Boolean {
        return if (currentStep.value > 1) {
            setStep(currentStep.value - 1)
            true
        } else {
            false
        }
    }

    // Step 1: Base info
    val name = MutableStateFlow(savedStateHandle.get<String>("name") ?: "")
    val ceoName = MutableStateFlow(savedStateHandle.get<String>("ceo_name") ?: "")
    val activityType = MutableStateFlow(savedStateHandle.get<String>("activity_type") ?: "تولیدکننده")
    val activityTypeId = MutableStateFlow(savedStateHandle.get<Long>("activity_type_id"))
    val description = MutableStateFlow(savedStateHandle.get<String>("description") ?: "")
    val logoUri = MutableStateFlow(savedStateHandle.get<String>("logo_uri") ?: "")
    val selectedCategories = MutableStateFlow(savedStateHandle.get<List<String>>("selected_categories")?.toSet() ?: emptySet())
    val tagsText = MutableStateFlow(savedStateHandle.get<String>("tags_text") ?: "")

    // Step 2: Legal & Financial
    val economicCode = MutableStateFlow(savedStateHandle.get<String>("economic_code") ?: "")
    val nationalId = MutableStateFlow(savedStateHandle.get<String>("national_id") ?: "")
    val registrationNumber = MutableStateFlow(savedStateHandle.get<String>("registration_number") ?: "")
    val bankAccounts = MutableStateFlow<List<BankAccount>>(emptyList())

    // Step 3: Contacts & Personnel
    val primaryPhone = MutableStateFlow(savedStateHandle.get<String>("primary_phone") ?: "")
    val additionalPhones = MutableStateFlow<List<String>>(emptyList())
    val primaryMobile = MutableStateFlow(savedStateHandle.get<String>("primary_mobile") ?: "")
    val additionalMobiles = MutableStateFlow<List<String>>(emptyList())
    val email = MutableStateFlow(savedStateHandle.get<String>("email") ?: "")
    val website = MutableStateFlow(savedStateHandle.get<String>("website") ?: "")
    val personnelList = MutableStateFlow<List<ContactPerson>>(emptyList())

    // Step 4: Location & Map
    val province = MutableStateFlow(savedStateHandle.get<String>("province") ?: "تهران")
    val city = MutableStateFlow(savedStateHandle.get<String>("city") ?: "تهران")
    val address = MutableStateFlow(savedStateHandle.get<String>("address") ?: "")
    val latitude = MutableStateFlow(savedStateHandle.get<Double>("latitude") ?: 35.73576)
    val longitude = MutableStateFlow(savedStateHandle.get<Double>("longitude") ?: 51.44678)

    // Step 5: Products & Catalog
    val productsList = MutableStateFlow<List<Product>>(emptyList())
    val catalogTitle = MutableStateFlow(savedStateHandle.get<String>("catalog_title") ?: "")
    val catalogLocalPath = MutableStateFlow(savedStateHandle.get<String>("catalog_local_path") ?: "")
    val catalogFileName = MutableStateFlow(savedStateHandle.get<String>("catalog_file_name") ?: "")
    val notes = MutableStateFlow(savedStateHandle.get<String>("notes") ?: "")
    val isVerified = MutableStateFlow(savedStateHandle.get<Boolean>("is_verified") ?: false)
    val rating = MutableStateFlow(savedStateHandle.get<Float>("rating") ?: 0f)

    // Inline field errors
    val nameError = MutableStateFlow<String?>(null)
    val nationalIdError = MutableStateFlow<String?>(null)
    val mobileError = MutableStateFlow<String?>(null)
    val phoneError = MutableStateFlow<String?>(null)

    fun initializeFromVendor(vendor: Vendor?, contacts: List<ContactPerson> = emptyList(), products: List<Product> = emptyList()) {
        if (vendor == null) return
        name.value = vendor.name
        ceoName.value = vendor.ceoName
        activityType.value = vendor.activityType
        activityTypeId.value = vendor.activityTypeId
        description.value = vendor.description
        logoUri.value = vendor.logoUri
        selectedCategories.value = vendor.categories.split(",", "،").map { it.trim() }.filter { it.isNotBlank() }.toSet()
        tagsText.value = vendor.tags
        economicCode.value = vendor.economicCode
        nationalId.value = vendor.nationalId
        registrationNumber.value = vendor.registrationNumber
        bankAccounts.value = vendor.getBankAccountsList()
        primaryPhone.value = vendor.phone
        additionalPhones.value = vendor.secondaryPhones.split(",", "،").map { it.trim() }.filter { it.isNotBlank() }
        primaryMobile.value = vendor.mobile
        additionalMobiles.value = vendor.additionalMobiles.split(",", "،").map { it.trim() }.filter { it.isNotBlank() }
        email.value = vendor.email
        website.value = vendor.website
        province.value = vendor.province
        city.value = vendor.city
        address.value = vendor.address
        latitude.value = vendor.latitude
        longitude.value = vendor.longitude
        catalogTitle.value = vendor.catalogTitle
        catalogLocalPath.value = vendor.catalogLocalPath
        catalogFileName.value = vendor.catalogFileName
        notes.value = vendor.notes
        isVerified.value = vendor.isVerified
        rating.value = vendor.rating

        if (contacts.isNotEmpty()) {
            personnelList.value = contacts
        }
        if (products.isNotEmpty()) {
            productsList.value = products
        }
    }

    fun validateStep(step: Int): Boolean {
        when (step) {
            1 -> {
                if (name.value.trim().isBlank()) {
                    nameError.value = "نام شرکت الزامی است"
                    return false
                }
                nameError.value = null
                return true
            }
            2 -> {
                val nat = nationalId.value.trim()
                if (nat.isNotBlank() && !PersianUtils.isValidNationalIdOrCode(nat)) {
                    nationalIdError.value = "شناسه ملی یا کد ملی معتبر نیست (۱۰ یا ۱۱ رقم با بررسی الگوریتم)"
                    return false
                }
                nationalIdError.value = null
                return true
            }
            3 -> {
                val mob = primaryMobile.value.trim()
                if (mob.isNotBlank() && !PersianUtils.isValidIranianMobile(mob)) {
                    mobileError.value = "شماره موبایل نامعتبر است (الگوی صحیح: ۰۹۱۲۳۴۵۶۷۸۹)"
                    return false
                }
                mobileError.value = null
                return true
            }
            else -> return true
        }
    }

    fun buildVendor(existingVendor: Vendor?): Vendor {
        val base = existingVendor ?: Vendor(name = name.value.trim())
        return base.copy(
            name = name.value.trim(),
            ceoName = ceoName.value.trim(),
            activityType = activityType.value.trim(),
            activityTypeId = activityTypeId.value,
            description = description.value.trim(),
            logoUri = logoUri.value.trim(),
            categories = selectedCategories.value.joinToString(", "),
            tags = tagsText.value.trim(),
            economicCode = economicCode.value.trim(),
            nationalId = nationalId.value.trim(),
            registrationNumber = registrationNumber.value.trim(),
            bankAccounts = com.example.data.model.BankAccountUtils.serialize(bankAccounts.value),
            phone = primaryPhone.value.trim(),
            secondaryPhones = additionalPhones.value.joinToString(", "),
            mobile = primaryMobile.value.trim(),
            additionalMobiles = additionalMobiles.value.joinToString(", "),
            email = email.value.trim(),
            website = website.value.trim(),
            province = province.value.trim(),
            city = city.value.trim(),
            address = address.value.trim(),
            latitude = latitude.value,
            longitude = longitude.value,
            catalogTitle = catalogTitle.value.trim(),
            catalogLocalPath = catalogLocalPath.value.trim(),
            catalogFileName = catalogFileName.value.trim(),
            notes = notes.value.trim(),
            isVerified = isVerified.value,
            rating = rating.value
        )
    }
}
