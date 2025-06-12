package com.andiniaulia3119.kantongresep.network

import com.andiniaulia3119.kantongresep.model.ImageUploadResponse
import com.andiniaulia3119.kantongresep.model.MessageResponse
import com.andiniaulia3119.kantongresep.model.Resep
import com.andiniaulia3119.kantongresep.model.ResepCreate
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://resep-api-849c0e113bf0.herokuapp.com"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @GET("recipes/")
    suspend fun getAllData(
        @Query("email") email: String
    ): List<Resep>

    @POST("recipes/")
    suspend fun addData(
        @Body data: ResepCreate
    ): MessageResponse

    @DELETE("recipes/{recipe_id}")
    suspend fun deleteData(
        @Path("recipe_id") id: Int,
        @Query("email") email: String
    ): MessageResponse

    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @PUT("resep/{id}")
    suspend fun updateResep(
        @Path("id") id: Int,
        @Body data: ResepCreate
    ): Response<Unit>


}

object Api {
    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    enum class ApiStatus { LOADING, SUCCESS, FAILED }
}

