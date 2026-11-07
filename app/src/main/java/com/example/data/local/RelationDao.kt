package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.BankAccount
import com.example.data.model.Category
import com.example.data.model.Tag
import com.example.data.model.VendorCategoryCrossRef
import com.example.data.model.VendorPhone
import com.example.data.model.VendorTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationDao {
    // Categories for vendor
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendorCategories(crossRefs: List<VendorCategoryCrossRef>)

    @Query("DELETE FROM vendor_categories WHERE vendorId = :vendorId")
    suspend fun deleteVendorCategoriesByVendorId(vendorId: Long)

    @Query("""
        SELECT c.* FROM categories c
        INNER JOIN vendor_categories vc ON c.id = vc.categoryId
        WHERE vc.vendorId = :vendorId
    """)
    fun getCategoriesForVendor(vendorId: Long): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM vendor_categories WHERE categoryId = :categoryId")
    suspend fun getVendorCountForCategoryId(categoryId: Long): Int

    // Tags
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: Tag): Long

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun findTagByName(name: String): Tag?

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendorTags(crossRefs: List<VendorTagCrossRef>)

    @Query("DELETE FROM vendor_tags WHERE vendorId = :vendorId")
    suspend fun deleteVendorTagsByVendorId(vendorId: Long)

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN vendor_tags vt ON t.id = vt.tagId
        WHERE vt.vendorId = :vendorId
    """)
    fun getTagsForVendor(vendorId: Long): Flow<List<Tag>>

    // Vendor Phones
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendorPhones(phones: List<VendorPhone>)

    @Query("DELETE FROM vendor_phones WHERE vendorId = :vendorId")
    suspend fun deleteVendorPhonesByVendorId(vendorId: Long)

    @Query("SELECT * FROM vendor_phones WHERE vendorId = :vendorId")
    fun getPhonesForVendor(vendorId: Long): Flow<List<VendorPhone>>

    @Query("SELECT * FROM vendor_phones WHERE vendorId = :vendorId")
    suspend fun getPhonesForVendorSync(vendorId: Long): List<VendorPhone>

    // Bank Accounts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccounts(accounts: List<BankAccount>)

    @Query("DELETE FROM bank_accounts WHERE vendorId = :vendorId")
    suspend fun deleteBankAccountsByVendorId(vendorId: Long)

    @Query("SELECT * FROM bank_accounts WHERE vendorId = :vendorId")
    fun getBankAccountsForVendor(vendorId: Long): Flow<List<BankAccount>>

    @Query("SELECT * FROM bank_accounts WHERE vendorId = :vendorId")
    suspend fun getBankAccountsForVendorSync(vendorId: Long): List<BankAccount>
}
