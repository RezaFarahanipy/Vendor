package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ActivityType
import com.example.data.model.Category
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Vendor

@Database(
    entities = [
        Vendor::class,
        Category::class,
        Product::class,
        ContactPerson::class,
        ActivityType::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vendorDao(): VendorDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun contactPersonDao(): ContactPersonDao
    abstract fun activityTypeDao(): ActivityTypeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vendorlist_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
