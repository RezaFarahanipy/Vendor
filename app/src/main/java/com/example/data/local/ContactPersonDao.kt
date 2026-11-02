package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ContactPerson
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactPersonDao {
    @Query("SELECT * FROM contact_persons WHERE vendorId = :vendorId ORDER BY id ASC")
    fun getContactsByVendor(vendorId: Long): Flow<List<ContactPerson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactPerson): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactPerson>)

    @Update
    suspend fun updateContact(contact: ContactPerson)

    @Delete
    suspend fun deleteContact(contact: ContactPerson)

    @Query("DELETE FROM contact_persons WHERE id = :id")
    suspend fun deleteContactById(id: Long)
}
