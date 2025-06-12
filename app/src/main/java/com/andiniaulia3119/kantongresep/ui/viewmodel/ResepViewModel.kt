package com.andiniaulia3119.kantongresep.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andiniaulia3119.kantongresep.model.Resep
import com.andiniaulia3119.kantongresep.model.ResepCreate
import com.andiniaulia3119.kantongresep.network.Api
import com.andiniaulia3119.kantongresep.repository.ResepRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResepViewModel(private val repository: ResepRepository) : ViewModel() {

    private val _resepList = MutableStateFlow<List<Resep>>(emptyList())
    val resepList: StateFlow<List<Resep>> = _resepList

    private val _status = MutableStateFlow(Api.ApiStatus.LOADING)
    val status: StateFlow<Api.ApiStatus> = _status

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun clearMessage() {
        _message.value = null
    }


    fun fetchResep(email: String) {
        viewModelScope.launch {
            _status.value = Api.ApiStatus.LOADING
            try {
                val data = repository.getResep(email)
                _resepList.value = data
                _status.value = Api.ApiStatus.SUCCESS
            } catch (e: Exception) {
                _status.value = Api.ApiStatus.FAILED
            }
        }
    }

    fun deleteResep(id: Int, email: String) {
        viewModelScope.launch {
            try {
                repository.deleteResep(id, email)
                fetchResep(email)
                _message.value = "Resep berhasil dihapus!"
            } catch (e: Exception) {
                _message.value = "Gagal menghapus resep: ${e.message}"
            }
        }
    }

    fun uploadAndCreateResep(
        nama: String,
        deskripsi: String,
        kategori: String,
        bitmap: Bitmap,
        userEmail: String
    ) {
        viewModelScope.launch {
            try {
                val response = repository.uploadImage(bitmap)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        val resep = ResepCreate(
                            userEmail = userEmail,
                            nama = nama,
                            deskripsi = deskripsi,
                            kategori = kategori,
                            imageId = data.id,
                            deleteHash = data.deleteHash
                        )
                        repository.addResep(resep)
                        fetchResep(userEmail)
                        _message.value = "Resep berhasil ditambahkan!"
                    } else {
                        _message.value = "Gagal mengunggah gambar!"
                    }
                } else {
                    _message.value = "Upload gagal: ${response.message()}"
                }
            } catch (e: Exception) {
                _message.value = "Error saat upload: ${e.message}"
            }
        }
    }

    fun updateResep(
        id: Int,
        nama: String,
        deskripsi: String,
        kategori: String,
        bitmap: Bitmap,
        userEmail: String
    ) {
        viewModelScope.launch {
            try {
                val response = repository.uploadImage(bitmap)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        val updatedResep = ResepCreate(
                            userEmail = userEmail,
                            nama = nama,
                            deskripsi = deskripsi,
                            kategori = kategori,
                            imageId = data.id,
                            deleteHash = data.deleteHash
                        )
                        repository.updateResep(id, updatedResep)
                        fetchResep(userEmail)
                        _message.value = "Resep berhasil diperbarui!"
                    } else {
                        _message.value = "Gagal mengunggah gambar!"
                    }
                } else {
                    _message.value = "Upload gagal: ${response.message()}"
                }
            } catch (e: Exception) {
                _message.value = "Gagal update: ${e.message}"
            }
        }
    }

}
