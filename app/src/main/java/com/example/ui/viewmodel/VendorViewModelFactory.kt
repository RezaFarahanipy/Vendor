package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.preferences.AppSettingsDataStore
import com.example.data.repository.VendorRepository

class VendorViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    private val database by lazy { AppDatabase.getDatabase(application) }
    private val repository by lazy { VendorRepository(database) }
    private val settingsDataStore by lazy { AppSettingsDataStore(application) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(VendorViewModel::class.java) -> {
                VendorViewModel(application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
