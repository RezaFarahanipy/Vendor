package com.example.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ActivityType
import com.example.data.model.BankAccount
import com.example.data.model.BankAccountUtils
import com.example.data.model.Category
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Tag
import com.example.data.model.Vendor
import com.example.data.model.VendorCategoryCrossRef
import com.example.data.model.VendorPhone
import com.example.data.model.VendorTagCrossRef
import com.example.util.PersianUtils

@Database(
    entities = [
        Vendor::class,
        Category::class,
        Product::class,
        ContactPerson::class,
        ActivityType::class,
        VendorCategoryCrossRef::class,
        Tag::class,
        VendorTagCrossRef::class,
        VendorPhone::class,
        BankAccount::class
    ],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vendorDao(): VendorDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun contactPersonDao(): ContactPersonDao
    abstract fun activityTypeDao(): ActivityTypeDao
    abstract fun relationDao(): RelationDao

    companion object {
        private const val TAG = "AppDatabase"

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.i(TAG, "Starting MIGRATION_3_4...")

                // 1. Add searchIndex and activityTypeId to vendors
                db.execSQL("ALTER TABLE vendors ADD COLUMN searchIndex TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE vendors ADD COLUMN activityTypeId INTEGER DEFAULT NULL")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_vendors_searchIndex ON vendors(searchIndex)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_vendors_activityTypeId ON vendors(activityTypeId)")

                // 2. Create bank_accounts table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS bank_accounts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        vendorId INTEGER NOT NULL,
                        bankName TEXT NOT NULL,
                        accountNumber TEXT NOT NULL,
                        iban TEXT NOT NULL,
                        branch TEXT NOT NULL,
                        FOREIGN KEY(vendorId) REFERENCES vendors(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_bank_accounts_vendorId ON bank_accounts(vendorId)")

                // 3. Create vendor_categories cross-ref table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS vendor_categories (
                        vendorId INTEGER NOT NULL,
                        categoryId INTEGER NOT NULL,
                        PRIMARY KEY(vendorId, categoryId),
                        FOREIGN KEY(vendorId) REFERENCES vendors(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(categoryId) REFERENCES categories(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_vendor_categories_vendorId ON vendor_categories(vendorId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_vendor_categories_categoryId ON vendor_categories(categoryId)")

                // 4. Create tags table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS tags (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL
                    )
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_tags_name ON tags(name)")

                // 5. Create vendor_tags cross-ref table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS vendor_tags (
                        vendorId INTEGER NOT NULL,
                        tagId INTEGER NOT NULL,
                        PRIMARY KEY(vendorId, tagId),
                        FOREIGN KEY(vendorId) REFERENCES vendors(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(tagId) REFERENCES tags(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_vendor_tags_vendorId ON vendor_tags(vendorId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_vendor_tags_tagId ON vendor_tags(tagId)")

                // 6. Create vendor_phones table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS vendor_phones (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        vendorId INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        number TEXT NOT NULL,
                        label TEXT NOT NULL,
                        FOREIGN KEY(vendorId) REFERENCES vendors(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_vendor_phones_vendorId ON vendor_phones(vendorId)")

                // 7. Ensure contact_persons and products indices exist
                db.execSQL("CREATE INDEX IF NOT EXISTS index_contact_persons_vendorId ON contact_persons(vendorId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_products_vendorId ON products(vendorId)")

                // 8. Recreate contact_persons to link Foreign Key CASCADE
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS contact_persons_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        vendorId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        role TEXT NOT NULL,
                        mobile TEXT NOT NULL,
                        phone TEXT NOT NULL,
                        extension TEXT NOT NULL,
                        email TEXT NOT NULL,
                        FOREIGN KEY(vendorId) REFERENCES vendors(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO contact_persons_new (id, vendorId, name, role, mobile, phone, extension, email)
                    SELECT id, vendorId, name, role, mobile, phone, extension, email FROM contact_persons
                """.trimIndent())
                db.execSQL("DROP TABLE contact_persons")
                db.execSQL("ALTER TABLE contact_persons_new RENAME TO contact_persons")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_contact_persons_vendorId ON contact_persons(vendorId)")

                // 9. Recreate products to link Foreign Key CASCADE
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS products_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        vendorId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        brand TEXT NOT NULL,
                        model TEXT NOT NULL,
                        technicalCode TEXT NOT NULL,
                        specifications TEXT NOT NULL,
                        description TEXT NOT NULL,
                        category TEXT NOT NULL,
                        imageDrawable TEXT NOT NULL,
                        catalogPdfUrl TEXT NOT NULL,
                        isFavorite INTEGER NOT NULL,
                        FOREIGN KEY(vendorId) REFERENCES vendors(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO products_new (id, vendorId, name, brand, model, technicalCode, specifications, description, category, imageDrawable, catalogPdfUrl, isFavorite)
                    SELECT id, vendorId, name, brand, model, technicalCode, specifications, description, category, imageDrawable, catalogPdfUrl, isFavorite FROM products
                """.trimIndent())
                db.execSQL("DROP TABLE products")
                db.execSQL("ALTER TABLE products_new RENAME TO products")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_products_vendorId ON products(vendorId)")

                // 10. Backfill data: searchIndex, bank_accounts, vendor_phones
                try {
                    val cursor = db.query("SELECT id, name, ceoName, city, categories, tags, phone, mobile, secondaryPhones, additionalMobiles, economicCode, nationalId, bankAccounts FROM vendors")
                    while (cursor.moveToNext()) {
                        val vId = cursor.getLong(0)
                        val vName = cursor.getString(1) ?: ""
                        val vCeo = cursor.getString(2) ?: ""
                        val vCity = cursor.getString(3) ?: ""
                        val vCats = cursor.getString(4) ?: ""
                        val vTags = cursor.getString(5) ?: ""
                        val vPhone = cursor.getString(6) ?: ""
                        val vMobile = cursor.getString(7) ?: ""
                        val vSecPhones = cursor.getString(8) ?: ""
                        val vAddMobiles = cursor.getString(9) ?: ""
                        val vEcon = cursor.getString(10) ?: ""
                        val vNatId = cursor.getString(11) ?: ""
                        val vBankRaw = cursor.getString(12) ?: ""

                        // Generate search index
                        val searchIdx = PersianUtils.generateSearchIndex(
                            name = vName,
                            ceoName = vCeo,
                            city = vCity,
                            categories = vCats,
                            tags = vTags,
                            phone = vPhone,
                            mobile = vMobile,
                            secondaryPhones = vSecPhones,
                            additionalMobiles = vAddMobiles,
                            economicCode = vEcon,
                            nationalId = vNatId
                        )
                        db.execSQL("UPDATE vendors SET searchIndex = ? WHERE id = ?", arrayOf(searchIdx, vId))

                        // Backfill phone
                        if (vPhone.isNotBlank()) {
                            db.execSQL("INSERT INTO vendor_phones (vendorId, type, number, label) VALUES (?, 'LANDLINE', ?, 'تلفن ثابت')", arrayOf(vId, vPhone.trim()))
                        }
                        if (vMobile.isNotBlank()) {
                            db.execSQL("INSERT INTO vendor_phones (vendorId, type, number, label) VALUES (?, 'MOBILE', ?, 'موبایل اصلی')", arrayOf(vId, vMobile.trim()))
                        }

                        // Backfill bank accounts
                        val parsedAccounts = BankAccountUtils.parse(vBankRaw)
                        for (acc in parsedAccounts) {
                            db.execSQL(
                                "INSERT INTO bank_accounts (vendorId, bankName, accountNumber, iban, branch) VALUES (?, ?, ?, ?, ?)",
                                arrayOf(vId, acc.bankName, acc.accountNumber, acc.iban, acc.branch)
                            )
                        }
                    }
                    cursor.close()
                } catch (e: Exception) {
                    Log.w(TAG, "Notice during MIGRATION_3_4 data backfill: ${e.message}")
                }

                Log.i(TAG, "MIGRATION_3_4 completed successfully.")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "vendorlist_database"
                    )
                        .addMigrations(MIGRATION_3_4)
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onOpen(db: SupportSQLiteDatabase) {
                                super.onOpen(db)
                                Log.i(TAG, "AppDatabase opened successfully. Current schema version: ${db.version}")
                            }

                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                Log.i(TAG, "AppDatabase created fresh. Initial schema version: ${db.version}")
                            }
                        })
                        // Strictly NO fallbackToDestructiveMigration - explicit migrations required!
                        .build()
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    Log.e(TAG, "CRITICAL ERROR opening AppDatabase: ${e.message}. If schema changed, verify explicit Room Migration is registered.", e)
                    throw e
                }
            }
        }
    }
}
