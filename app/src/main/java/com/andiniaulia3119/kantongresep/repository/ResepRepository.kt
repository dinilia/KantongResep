package com.andiniaulia3119.kantongresep.repository

import android.graphics.Bitmap
import com.andiniaulia3119.kantongresep.model.ImageUploadResponse
import com.andiniaulia3119.kantongresep.model.MessageResponse
import com.andiniaulia3119.kantongresep.model.Resep
import com.andiniaulia3119.kantongresep.model.ResepCreate
import com.andiniaulia3119.kantongresep.network.Api
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class ResepRepository {

    suspend fun getResep(email: String): List<Resep> =
        Api.service.getAllData(email)

    suspend fun addResep(data: ResepCreate): MessageResponse =
        Api.service.addData(data)

    suspend fun deleteResep(id: Int, email: String): MessageResponse =
        Api.service.deleteData(id, email)

    // 🔥 Tambahan untuk upload gambar
    suspend fun uploadImage(bitmap: Bitmap): retrofit2.Response<ImageUploadResponse> {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        val requestBody: RequestBody =
            byteArray.toRequestBody("image/png".toMediaTypeOrNull())
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("image", "image.png", requestBody)

        return Api.service.uploadImage(body)
    }

    suspend fun updateResep(id: Int, data: ResepCreate): Boolean {
        val response = Api.service.updateResep(id, data)
        return response.isSuccessful
    }
}

