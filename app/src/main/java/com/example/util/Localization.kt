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
        "app_subtitle" to Pair("سامانه جامع تأمین‌کنندگان صنعتی", "Industrial Suppliers Platform"),
        "search_hint" to Pair("جستجوی پیشرفته وندور، محصول، نوع فعالیت، شهر...", "Search vendors, products, activity type, city..."),
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
        "developer_name" to Pair("رضا فراهانی", "Reza Farahani")
    )

    fun get(key: String, lang: AppLanguage): String {
        val pair = translations[key] ?: return key
        return if (lang == AppLanguage.EN) pair.second else pair.first
    }
}
