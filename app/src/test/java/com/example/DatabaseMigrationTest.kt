package com.example

import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.local.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class DatabaseMigrationTest {

    @Test
    fun testAppDatabaseSchemaVersion4AndAllTables() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()

        val sqliteDb = db.openHelper.readableDatabase
        assertEquals(4, sqliteDb.version)

        val cursor = sqliteDb.query("SELECT name FROM sqlite_master WHERE type='table'")
        val tableNames = mutableListOf<String>()
        while (cursor.moveToNext()) {
            tableNames.add(cursor.getString(0))
        }
        cursor.close()

        assertTrue("Table 'vendors' must exist", tableNames.contains("vendors"))
        assertTrue("Table 'categories' must exist", tableNames.contains("categories"))
        assertTrue("Table 'products' must exist", tableNames.contains("products"))
        assertTrue("Table 'contact_persons' must exist", tableNames.contains("contact_persons"))
        assertTrue("Table 'activity_types' must exist", tableNames.contains("activity_types"))
        assertTrue("Table 'vendor_categories' must exist", tableNames.contains("vendor_categories"))
        assertTrue("Table 'tags' must exist", tableNames.contains("tags"))
        assertTrue("Table 'vendor_tags' must exist", tableNames.contains("vendor_tags"))
        assertTrue("Table 'vendor_phones' must exist", tableNames.contains("vendor_phones"))
        assertTrue("Table 'bank_accounts' must exist", tableNames.contains("bank_accounts"))

        // Verify searchIndex column in vendors table
        val pragmaCursor = sqliteDb.query("PRAGMA table_info(vendors)")
        val columnNames = mutableListOf<String>()
        while (pragmaCursor.moveToNext()) {
            columnNames.add(pragmaCursor.getString(1))
        }
        pragmaCursor.close()

        assertTrue("Column 'searchIndex' must exist in vendors", columnNames.contains("searchIndex"))
        assertTrue("Column 'activityTypeId' must exist in vendors", columnNames.contains("activityTypeId"))

        db.close()
    }
}
