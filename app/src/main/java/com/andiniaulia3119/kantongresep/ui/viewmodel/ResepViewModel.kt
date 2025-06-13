package com.andiniaulia3119.kantongresep.ui.viewmodel

import android.util.Log
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
            try {
                _resepList.value = repository.getResep(email)
                Log.d("ResepViewModel", "Data resep berhasil diambil: ${_resepList.value}") // Tambahkan ini
                _status.value = Api.ApiStatus.SUCCESS
            } catch (e: Exception) {
                _status.value = Api.ApiStatus.FAILED
                _message.value = "Gagal memuat data: ${e.message}"
            }
        }
    }

    fun deleteResep(id: Int, email: String) {
        viewModelScope.launch {
            try {
                repository.deleteResep(id)
                fetchResep(email)
                _message.value = "Resep berhasil dihapus!"
            } catch (e: Exception) {
                _message.value = "Gagal menghapus resep: ${e.message}"
            }
        }
    }

    fun addResep(
        nama: String,
        deskripsi: String,
        kategori: String,
        imageId: String,
        userEmail: String,
        deleteHash: String
    ) {
        viewModelScope.launch {
            android.util.Log.d(
                "ResepViewModel",
                "addResep: nama=$nama, deskripsi=$deskripsi, kategori=$kategori, imageId=$imageId, userEmail=$userEmail"
            )
            try {
                val data = ResepCreate(
                    nama = nama,
                    deskripsi = deskripsi,
                    kategori = kategori,
                    imageId = imageId,
                    userEmail = userEmail,
                    deleteHash = "null"
                )

                val moshi = com.squareup.moshi.Moshi.Builder()
                    .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                    .build()
                val adapter = moshi.adapter(ResepCreate::class.java)
                val json = adapter.toJson(data)
                Log.d("ResepViewModel", "JSON yang dikirim: $json")

                val response = repository.addResep(data)
                android.util.Log.d("ResepViewModel", "addResep success: $response")
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                android.util.Log.e("ResepViewModel", "Error addResep (HttpException): $errorBody")
            } catch (e: Exception) {
                android.util.Log.e("ResepViewModel", "Error addResep (Exception): ${e.message}", e)
            }
        }
    }

    fun updateResep(
        id: Int,
        nama: String,
        deskripsi: String,
        kategori: String,
        imageId: String,
        userEmail: String
    ) {
        viewModelScope.launch {
            try {
                if (imageId.isBlank()) {
                    _message.value = "Gambar tidak boleh kosong!"
                    Log.e("ResepViewModel", "updateResep: imageId kosong!")
                    return@launch
                }
                val updatedResep = ResepCreate(
                    userEmail = userEmail,
                    nama = nama,
                    deskripsi = deskripsi,
                    kategori = kategori,
                    imageId = imageId,
                    deleteHash = "null"
                )
                Log.d("ResepViewModel", "updateResep: id=$id, data=$updatedResep")
                val response = repository.updateResep(id, updatedResep)
                Log.d("ResepViewModel", "updateResep response: $response")
                if (response.resep != null) {
                    fetchResep(userEmail)
                    _message.value = "Resep berhasil diperbarui!"
                } else {
                    _message.value = "Gagal memperbarui resep!"
                }
            } catch (e: Exception) {
                Log.e("ResepViewModel", "Error updateResep", e)
                _message.value = "Gagal update: ${e.message}"
            }
        }
    }

}
