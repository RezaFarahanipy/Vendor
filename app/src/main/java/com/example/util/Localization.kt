package com.example.util

enum class AppLanguage(val code: String, val titleFa: String, val titleEn: String) {
    FA("fa", "فارسی", "Persian"),
    EN("en", "English", "English")
}

object Localization {

    private val translations = mapOf(
        // Tabs
        "tab_home" to Pair("خانه", "Home"),
        "tab_search" to Pair("جستجو", "Search"),
        "tab_favorites" to Pair("نشان‌شده‌ها", "Favorites"),
        "tab_settings" to Pair("تنظیمات", "Settings"),

        // App Branding
        "app_title" to Pair("وندوریاب", "VendorHub"),
        "search" to Pair("جستجو", "Search"),
        "search_hint" to Pair("جستجوی پیشرفته وندور، محصول، نوع فعالیت، شهر...", "Search vendors, products, activity type, city..."),
        "search_placeholder" to Pair("جستجوی وندور، محصول، کد اقتصادی، شماره تماس...", "Search vendors, products, economic code, phone..."),
        "stat_total_vendors" to Pair("کل وندورها", "Total Vendors"),
        "stat_favorites" to Pair("علاقه‌مندی‌ها", "Favorites"),
        "stat_recent_calls" to Pair("تماس‌های اخیر", "Recent Contacts"),
        "registered_vendors" to Pair("وندورهای ثبت‌شده", "Registered Vendors"),
        "total_vendors" to Pair("کل وندورها", "Total Vendors"),
        "favorites" to Pair("علاقه‌مندی‌ها", "Favorites"),
        "recent_contacts" to Pair("تماس‌های اخیر", "Recent Contacts"),
        "main_categories" to Pair("دسته‌بندی‌های اصلی", "Main Categories"),
        "view_all" to Pair("مشاهده همه", "View All"),
        "latest_vendors" to Pair("آخرین وندورهای ثبت‌شده", "Latest Vendors"),
        "new_vendor_btn" to Pair("ثبت وندور جدید", "Add New Vendor"),
        "exit_confirm" to Pair("برای خروج از برنامه، دوباره دکمه بازگشت را بزنید", "Press back again to exit"),

        // Filter & Sort
        "filter" to Pair("فیلترها", "Filters"),
        "sort" to Pair("مرتب‌سازی", "Sort"),
        "all" to Pair("همه", "All"),
        "verified_only" to Pair("فقط تأییدشده‌ها", "Verified Only"),
        "has_catalog" to Pair("دارای کاتالوگ", "Has Catalog"),
        "sort_relevance" to Pair("مرتبط‌ترین", "Most Relevant"),
        "sort_rating" to Pair("بالاترین امتیاز", "Top Rated"),
        "sort_newest" to Pair("جدیدترین", "Newest"),
        "sort_name" to Pair("نام شرکت", "Company Name"),

        // Contact & Actions
        "call" to Pair("تماس", "Call"),
        "message" to Pair("پیامک", "SMS"),
        "whatsapp" to Pair("واتس‌اپ", "WhatsApp"),
        "telegram" to Pair("تلگرام", "Telegram"),
        "bale" to Pair("بله", "Bale"),
        "rubika" to Pair("روبیکا", "Rubika"),
        "website" to Pair("وبسایت", "Website"),
        "navigation" to Pair("مسیریابی", "Navigate"),
        "share" to Pair("اشتراک‌گذاری", "Share"),
        "edit" to Pair("ویرایش", "Edit"),
        "delete" to Pair("حذف", "Delete"),
        "save" to Pair("ذخیره", "Save"),
        "cancel" to Pair("لغو", "Cancel"),
        "confirm" to Pair("تأیید", "Confirm"),
        "back" to Pair("بازگشت", "Back"),
        "next" to Pair("مرحله بعد", "Next Step"),
        "prev" to Pair("مرحله قبل", "Previous Step"),

        // Details
        "about_company" to Pair("درباره شرکت و زمینه فعالیت", "About Company"),
        "contact_info" to Pair("اطلاعات تماس و آدرس", "Contact Info & Address"),
        "phone_numbers" to Pair("تلفن‌های ثابت", "Landline Phones"),
        "mobile_numbers" to Pair("شماره‌های همراه", "Mobile Numbers"),
        "personnel_title" to Pair("پرسنل و رابطین تجاری", "Company Personnel"),
        "bank_accounts" to Pair("حساب‌های بانکی و شماره شبا", "Bank Accounts & IBAN"),
        "economic_info" to Pair("شناسه‌ها و کد اقتصادی", "Company Registration & Tax IDs"),
        "products_catalog" to Pair("محصولات و تجهیزات", "Products & Equipment"),
        "view_catalog" to Pair("مشاهده کاتالوگ دیجیتال", "View Catalog PDF"),
        "inquiry" to Pair("استعلام قیمت و موجودی", "Request Quotation"),
        "notes" to Pair("یادداشت‌های محرمانه داخلی", "Internal Confidential Notes"),

        // Settings
        "settings_title" to Pair("تنظیمات و اطلاعات سامانه", "Settings & Information"),
        "language" to Pair("زبان برنامه", "Language"),
        "theme" to Pair("حالت تاریک / شب", "Dark Mode"),
        "features_guide" to Pair("راهنما و امکانات سامانه", "Features & App Guide"),
        "manage_activity_types" to Pair("مدیریت انواع فعالیت", "Manage Activity Types"),
        "manage_categories" to Pair("مدیریت زمینه‌های فعالیت", "Manage Industry Fields"),
        "database_info" to Pair("وضعیت دیتابیس محلی (Room)", "Local Database Status"),
        "export_csv" to Pair("خروجی اکسل و CSV", "Export to CSV/Excel"),
        "import_csv" to Pair("ورود اطلاعات از CSV", "Import from CSV"),
        "reset_data" to Pair("بازیابی اطلاعات پیش‌فرض", "Reset to Default Data"),
        "developer_info" to Pair("مشخصات توسعه‌دهنده", "Developer Info"),
        "developer_name" to Pair("رضا فراهانی", "Reza Farahani"),

        // Form Steps & Wizard
        "step_base" to Pair("پایه و لوگو", "Base & Logo"),
        "step_legal" to Pair("اطلاعات حقوقی و مالی", "Legal & Financial"),
        "step_contacts" to Pair("تماس و آدرس", "Contacts & Address"),
        "step_personnel" to Pair("پرسنل", "Personnel"),
        "step_products" to Pair("محصولات", "Products"),
        "step_catalog_rating" to Pair("کاتالوگ و امتیاز", "Catalog & Rating"),
        "add_personnel" to Pair("افزودن پرسنل", "Add Personnel"),
        "add_product" to Pair("افزودن محصول", "Add Product"),
        "save_vendor" to Pair("ذخیره وندور", "Save Vendor"),
        "save_and_exit" to Pair("ذخیره", "Save"),
        "address_type" to Pair("نوع آدرس", "Address Type"),
        "postal_code" to Pair("کد پستی", "Postal Code"),
        "city" to Pair("شهر", "City"),
        "province" to Pair("استان", "Province"),
        "direct_call" to Pair("تماس مستقیم", "Direct Call"),
        "address_type_headquarters" to Pair("دفتر مرکزی", "Headquarters"),
        "address_type_factory" to Pair("کارخانه / کارگاه", "Factory / Workshop"),
        "address_type_warehouse" to Pair("انبار", "Warehouse"),
        "address_type_showroom" to Pair("شوروم / نمایشگاه", "Showroom / Sales Office"),

        // Industry Categories
        "cat_industrial" to Pair("صنعتی", "Industrial"),
        "cat_machinery" to Pair("تجهیزات و ماشین‌آلات صنعتی", "Machinery & Equipment"),
        "cat_electrical" to Pair("برق و الکترونیک صنعتی", "Electrical & Electronics"),
        "cat_chemical" to Pair("مواد شیمیایی و پتروشیمی", "Chemicals & Petrochemicals"),
        "cat_polymer" to Pair("پلیمر و پلاستیک", "Polymers & Plastics"),
        "cat_steel" to Pair("آهن‌آلات و صنایع فلزی", "Steel & Metal Industries"),
        "cat_construction" to Pair("ساختمان و عمرانی", "Construction & Civil"),
        "cat_oil_gas" to Pair("نفت و گاز و پتروشیمی", "Oil, Gas & Petrochemical"),
        "cat_automation" to Pair("ابزار دقیق و اتوماسیون", "Instrumentation & Automation"),
        "cat_food_pharma" to Pair("صنایع غذایی و دارویی", "Food & Pharma Industries"),
        "cat_packaging" to Pair("بسته‌بندی و چاپ صنعتی", "Packaging & Industrial Printing"),
        "cat_pipes_valves" to Pair("لوله و اتصالات و شیرآلات", "Pipes, Fittings & Valves"),
        "cat_hydraulics" to Pair("هیدرولیک و پنوماتیک", "Hydraulics & Pneumatics")
    )

    fun translateCategory(catName: String, lang: AppLanguage): String {
        if (lang == AppLanguage.FA) return catName
        return when (catName.trim()) {
            "تجهیزات و ماشین‌آلات صنعتی", "تجهیزات صنعتی" -> "Machinery & Equipment"
            "برق و الکترونیک صنعتی", "برق و الکترونیک" -> "Electrical & Electronics"
            "مواد شیمیایی و پتروشیمی", "شیمیایی و پتروشیمی" -> "Chemicals & Petrochemicals"
            "پلیمر و پلاستیک" -> "Polymers & Plastics"
            "آهن‌آلات و صنایع فلزی", "فولاد و متالورژی" -> "Steel & Metal Industries"
            "ساختمان و عمرانی", "ساختمان و ابنیه" -> "Construction & Civil"
            "نفت و گاز و پتروشیمی" -> "Oil, Gas & Petrochemical"
            "ابزار دقیق و اتوماسیون" -> "Instrumentation & Automation"
            "صنایع غذایی و دارویی" -> "Food & Pharma Industries"
            "بسته‌بندی و چاپ صنعتی" -> "Packaging & Industrial Printing"
            "لوله و اتصالات و شیرآلات" -> "Pipes, Fittings & Valves"
            "هیدرولیک و پنوماتیک" -> "Hydraulics & Pneumatics"
            else -> catName
        }
    }

    fun get(key: String, lang: AppLanguage): String {
        val pair = translations[key] ?: return key
        return if (lang == AppLanguage.EN) pair.second else pair.first
    }
}
