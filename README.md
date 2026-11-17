# VendorYab · وندوریاب

**An offline vendor & supplier directory for industrial procurement teams — built with Kotlin and Jetpack Compose.**
Keep every supplier, contact person, product, catalog and bank detail in one fast, private app on your phone.

[English](#english) · [فارسی](#فارسی)

> **Download:** grab the latest APK from the [Releases page](../../releases/tag/latest-debug)
> (`app-debug.apk`). Allow "install unknown apps" for your browser/file manager, then open the file.

---

## English

### What it does

VendorYab is a mobile address book for purchasing and procurement. Instead of scattered spreadsheets and
chat messages, you get searchable supplier profiles with everything you need to call, message and evaluate them.

**Vendors**
- Company profile: name, CEO, activity type, categories, tags, description, rating, verified badge, confidential internal notes
- Several landline and mobile numbers with a `021-xxxxxxxx` mask, area-code detection and validation
- **Multiple addresses** per vendor (factory, head office, warehouse, showroom) with postal code and a map location each
- Legal & banking data: economic code, national ID, registration number, several bank accounts with **IBAN (Sheba) validation** and bank auto-detection
- **Multiple catalog PDFs** per vendor with a built-in viewer, sharing and renaming

**People & products**
- Personnel with job title, gender, direct phone, extension, e-mail and one-tap job-title helpers
- Quick contact via **call, SMS, WhatsApp, Telegram, Bale and Rubika** (real app icons are shown when the apps are installed)
- Product / equipment list per vendor: name, brand, model, technical code, specifications

**Search (the heart of the app)**
- Instant, diacritic- and digit-insensitive Persian search (ی/ي, ک/ك, Persian & Arabic digits, half-spaces)
- Results grouped into **personnel, products and vendors** — a matched person is shown with the company they work for and quick-contact buttons
- Relevance ranking, category / city / verified / has-catalog filters, list or card view, recent searches

**Data & safety**
- 100 % offline storage in a local Room (SQLite) database — nothing is uploaded anywhere
- Optional **biometric / device-credential app lock** (locks again on every launch) and screenshot blocking
- **Excel / CSV export & import** with addresses, postal codes, IBAN, personnel and products
- **Full backup & restore** as a ZIP (database, logos, PDF catalogs) or JSON
- Editable lists in Settings: activity types, industry categories and quick specialty suggestions (e.g. rebar, angle iron, cable…)

**Experience**
- Persian (RTL) and English (LTR) UI, light/dark themes, bright indigo–violet design with smooth animations

### Tech stack

| Area | Technology |
|---|---|
| Language / UI | Kotlin 2.2, Jetpack Compose (Material 3), Compose animations |
| Architecture | Single-activity, ViewModel + StateFlow, repository pattern |
| Storage | Room 2.7 (explicit migrations), DataStore Preferences |
| Security | AndroidX Biometric, `FLAG_SECURE` |
| Imaging | Coil |
| Build | Gradle 9.3 · AGP 9.1 · JDK 17 · `minSdk 24` · `targetSdk 36` |

### Project structure

```
app/src/main/java/com/example/
├── MainActivity.kt          # entry point, app lock, language/theme wiring
├── data/
│   ├── local/               # Room database, DAOs, migrations (v3 → v8)
│   ├── model/               # Vendor, ContactPerson, Product, BankAccount, VendorAddress, CatalogItem…
│   ├── preferences/         # DataStore settings (theme, lock, recent searches, suggestions)
│   └── repository/          # VendorRepository (transactions, search index, import/export)
├── ui/
│   ├── screens/             # Home, Search, Favorites, Settings, Vendor detail, add/edit wizard…
│   ├── components/          # shared composables (cards, location picker, messenger icons…)
│   ├── theme/               # palette, typography, motion helpers
│   └── viewmodel/           # VendorViewModel (search pipeline, filters, state)
└── util/                    # CSV / backup, Persian text helpers, IBAN tools, translations (Lang.kt / Translations.kt)
```

### Build it yourself

**Locally** (Android Studio Ladybug or newer, JDK 17):

```bash
git clone https://github.com/<your-user>/Vendor.git
cd Vendor
gradle :app:assembleDebug        # or open the project in Android Studio and press Run
```

The APK is produced at `app/build/outputs/apk/debug/app-debug.apk`.

**With GitHub Actions (no computer setup needed):**

1. Fork this repository.
2. *(Recommended)* create a signing key so that every build can update the previous install:
   ```bash
   keytool -genkeypair -keystore debug.keystore -alias androiddebugkey \
     -storepass android -keypass android -keyalg RSA -keysize 2048 -validity 10000 \
     -dname "CN=Android Debug,O=Android,C=US"
   base64 -w0 debug.keystore      # copy the output
   ```
   In your fork open **Settings → Secrets and variables → Actions → New repository secret**, name it
   `DEBUG_KEYSTORE_BASE64` and paste the value. (Without it the workflow still builds but signs with a
   temporary key, so each APK must be installed as a fresh app.)
3. Push to `main` (or run the workflow manually). The APK is published to the **`latest-debug`** release
   of your fork. Unit tests run before every build.

### Using CSV import / export

*Settings → Excel / CSV export* creates a UTF-8 (BOM) file that opens correctly in Excel. Columns are matched
by **name** on import, so you can add or remove columns freely. Multi-value cells separate entries with ` ## `
and the parts of an entry with `|`:

| Column | Entry format |
|---|---|
| Other addresses | `type\|province\|city\|postal code\|address\|lat\|lng` |
| Other bank accounts | `bank\|account\|IBAN\|branch` |
| Personnel | `name\|job title\|mobile\|phone\|extension\|e-mail\|gender (آقا/خانم)` |
| Products | `name\|brand\|model\|technical code\|category\|specifications` |

Duplicates (same name, national ID or mobile) are skipped; mobile numbers whose leading zero was removed by Excel are repaired.

### Privacy

All data stays on the device. Backups are plain files that you choose where to save. The app lock and screenshot
blocking can be enabled in *Settings → Security & data privacy*.

### Contributing

Issues and pull requests are welcome. Please keep UI text in Persian inside `Text(...)`/`t("…")` calls and add the English
translation to `util/Translations.kt`; schema changes need an explicit Room migration.

---

## فارسی

### وندوریاب چیست؟

وندوریاب یک **دفترچه‌ی آفلاین تأمین‌کنندگان** برای واحدهای خرید و تدارکات است. به‌جای فایل‌های اکسل و پیام‌های پراکنده،
همه‌ی اطلاعات هر وندور (شرکت، پرسنل، محصولات، کاتالوگ، حساب بانکی) را در یک برنامه‌ی سریع و خصوصی روی گوشی نگه می‌دارید.

**قابلیت‌های اصلی**

- **پروفایل وندور:** نام، مدیرعامل، نوع فعالیت، دسته‌ها و برچسب‌ها، امتیاز، نشان «تأییدشده» و یادداشت‌های محرمانه
- **شماره‌ها:** چند تلفن ثابت و همراه با ماسک `021-xxxxxxxx`، تشخیص شهر از پیش‌شماره و اعتبارسنجی
- **چند آدرس** برای هر وندور (کارخانه، دفتر مرکزی، انبار، شوروم) با کد پستی و موقعیت روی نقشه
- **اطلاعات حقوقی و بانکی:** کد اقتصادی، شناسه ملی، شماره ثبت، چند حساب بانکی با **اعتبارسنجی شبا** و تشخیص خودکار بانک
- **چند کاتالوگ PDF** برای هر وندور با نمایش‌دهنده، اشتراک‌گذاری و تغییر عنوان
- **پرسنل:** سمت، جنسیت (آقا/خانم)، تلفن مستقیم، داخلی، ایمیل و دکمه‌های کمکی سمت‌های رایج
- **تماس سریع:** تماس، پیامک، واتس‌اپ، تلگرام، بله و روبیکا (با آیکن واقعی برنامه‌ها)
- **محصولات و تجهیزات** هر وندور: نام، برند، مدل، کد فنی و مشخصات

**جستجو (قلب برنامه)**

- جستجوی سریع و هوشمند فارسی (یکسان‌سازی ی/ي و ک/ك، ارقام فارسی و عربی، نیم‌فاصله)
- نتایج در سه گروه **پرسنل، محصولات و وندورها**؛ کارت هر فرد با نام شرکتش و دکمه‌های تماس سریع نمایش داده می‌شود
- رتبه‌بندی بر اساس ارتباط، فیلتر دسته/شهر/تأییدشده/دارای کاتالوگ، نمای لیست یا کارت و جستجوهای اخیر

**داده و امنیت**

- ذخیره‌ی کاملاً **آفلاین** در پایگاه داده‌ی محلی (Room/SQLite) — هیچ داده‌ای به جایی ارسال نمی‌شود
- قفل برنامه با اثر انگشت/رمز گوشی (در هر بار اجرا) و جلوگیری از اسکرین‌شات
- **خروجی و ورودی Excel/CSV** شامل آدرس، کد پستی، شبا، پرسنل و محصولات
- **پشتیبان‌گیری کامل** (ZIP شامل دیتابیس، لوگوها و کاتالوگ‌ها یا JSON) و بازیابی
- فهرست‌های قابل‌ویرایش در تنظیمات: انواع فعالیت، زمینه‌های فعالیت و پیشنهادهای زمینه‌ی تخصصی (مثل میلگرد، نبشی، کابل…)

**ظاهر:** رابط فارسی (راست‌به‌چپ) و انگلیسی، تم روشن و تیره، طراحی شاد ایندیگو–بنفش با انیمیشن‌های روان

### نصب

آخرین فایل `app-debug.apk` را از بخش [Releases](../../releases/tag/latest-debug) دانلود و روی گوشی اندروید (نسخه ۷ به بالا) نصب کنید.
برای نصب باید «نصب برنامه از منابع ناشناس» را برای مرورگر یا فایل‌منیجر فعال کنید.

### ساخت از روی سورس

- **روی کامپیوتر:** Android Studio و JDK 17؛ دستور `gradle :app:assembleDebug` یا دکمه‌ی Run
- **بدون نصب هیچ‌چیز (GitHub Actions):** ریپو را Fork کنید و در **Settings ← Secrets and variables ← Actions** یک Secret با نام
  `DEBUG_KEYSTORE_BASE64` بسازید (روش ساخت کلید در بخش انگلیسی بالا آمده است). با هر Push روی `main` برنامه ساخته می‌شود
  و فایل APK در Release با نام `latest-debug` قرار می‌گیرد. اگر Secret تعریف نشود، بیلد کار می‌کند اما هر نسخه باید جداگانه نصب شود،
  چون امضای موقت هر بار فرق می‌کند.

### نکات CSV

ستون‌ها هنگام ورود **با نام** خوانده می‌شوند، پس می‌توانید ستون‌ها را کم یا زیاد کنید. در ستون‌های چندمقداری، موارد با ` ## ` و اجزای هر مورد با `|`
جدا می‌شوند (مثلاً پرسنل: `نام|سمت|موبایل|تلفن|داخلی|ایمیل|آقا یا خانم`). وندورهای تکراری (نام، شناسه ملی یا موبایل یکسان) نادیده گرفته می‌شوند و
صفرِ اولِ شماره‌هایی که اکسل حذف کرده، خودکار برگردانده می‌شود.

### مشارکت

گزارش مشکل (Issue) و درخواست ادغام (Pull Request) خوشحالمان می‌کند. متن‌های رابط کاربری را فارسی بنویسید و ترجمه‌ی انگلیسی را به
`util/Translations.kt` اضافه کنید؛ هر تغییر در ساختار دیتابیس نیاز به Migration صریح دارد.
