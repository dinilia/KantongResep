package com.andiniaulia3119.kantongresep.model

import com.squareup.moshi.Json

data class Resep(
    @Json(name = "recipe_id")
    val recipeId: Int,
    @Json(name = "user_email")
    val userEmail: String,
    val nama: String,
    val deskripsi: String,
    val kategori: String,
    @Json(name = "image_id")
    val imageId: String,
    @Json(name = "delete_hash")
    val deleteHash: String,
    @Json(name = "created_at")
    val createdAt: String
)

data class ResepCreate(
    @Json(name = "user_email")
    val userEmail: String,
    val nama: String,
    val deskripsi: String,
    val kategori: String,
    @Json(name = "image_id")
    val imageId: String,
    @Json(name = "delete_hash")
    val deleteHash: String = "null",
)

data class MessageResponse(
    val message: String,
    val resep: Resep? = null,
    val status: Boolean? = null
)

