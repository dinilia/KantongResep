package com.andiniaulia3119.kantongresep.model

data class ImageData(
    val id: String,
    val deleteHash: String
)

data class ImageUploadResponse(
    val success: Boolean,
    val data: ImageData
)

