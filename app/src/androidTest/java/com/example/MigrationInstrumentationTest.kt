package com.example

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.local.AppDatabase
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented Room migration test using MigrationTestHelper.
 * To execute on an Android device or emulator with adb:
 *   gradle :app:connectedAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class MigrationInstrumentationTest {

    private val TEST_DB = "migration-instrumented-test.db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun testCreateDatabaseAtVersion3() {
        // Create database at schema version 3 using the exported JSON schema
        val db = helper.createDatabase(TEST_DB, 3)
        assertNotNull(db)

        // Verify key tables exist
        val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table'")
        val tableNames = mutableListOf<String>()
        while (cursor.moveToNext()) {
            tableNames.add(cursor.getString(0))
        }
        cursor.close()

        assertTrue(tableNames.contains("vendors"))
        assertTrue(tableNames.contains("categories"))
        assertTrue(tableNames.contains("products"))
        assertTrue(tableNames.contains("contact_persons"))
        assertTrue(tableNames.contains("activity_types"))

        db.close()
    }
}
