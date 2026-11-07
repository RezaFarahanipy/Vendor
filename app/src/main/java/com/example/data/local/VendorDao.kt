package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Vendor
import kotlinx.coroutines.flow.Flow

@Dao
interface VendorDao {
    @Query("SELECT * FROM vendors ORDER BY createdAt DESC")
    fun getAllVendors(): Flow<List<Vendor>>

    @Query("SELECT * FROM vendors WHERE id = :id LIMIT 1")
    fun getVendorById(id: Long): Flow<Vendor?>

    @Query("SELECT * FROM vendors WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteVendors(): Flow<List<Vendor>>

    @Query("SELECT COUNT(*) FROM vendors")
    fun getVendorCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM vendors WHERE isFavorite = 1")
    fun getFavoriteCount(): Flow<Int>

    @Query("SELECT * FROM vendors WHERE lastContactDate IS NOT NULL ORDER BY lastContactDate DESC")
    fun getRecentContactedVendors(): Flow<List<Vendor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendor(vendor: Vendor): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendors(vendors: List<Vendor>)

    @Update
    suspend fun updateVendor(vendor: Vendor)

    @Delete
    suspend fun deleteVendor(vendor: Vendor)

    @Query("DELETE FROM vendors WHERE id = :id")
    suspend fun deleteVendorById(id: Long)

    @Query("UPDATE vendors SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE vendors SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String)

    @Query("UPDATE vendors SET lastContactDate = :timestamp WHERE id = :id")
    suspend fun recordContact(id: Long, timestamp: Long)

    @Query("SELECT * FROM vendors WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): Vendor?

    @Query("""
        SELECT * FROM vendors 
        WHERE searchIndex LIKE '%' || :query || '%' 
           OR name LIKE '%' || :query || '%' 
           OR phone LIKE '%' || :query || '%' 
           OR mobile LIKE '%' || :query || '%' 
        ORDER BY name ASC
    """)
    fun searchVendors(query: String): Flow<List<Vendor>>
}
