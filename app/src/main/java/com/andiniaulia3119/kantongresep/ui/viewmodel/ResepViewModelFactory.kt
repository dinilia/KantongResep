package com.andiniaulia3119.kantongresep.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andiniaulia3119.kantongresep.repository.ResepRepository

class ResepViewModelFactory(
    private val repository: ResepRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResepViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResepViewModel(repository /*, imageId, deleteHash */) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

