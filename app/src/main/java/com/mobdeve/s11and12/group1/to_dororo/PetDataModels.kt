package com.mobdeve.s11and12.group1.to_dororo

import androidx.annotation.DrawableRes

// Data class for PetShop
data class PetShopItem(
    val type: String = "",
    val imageResId: Int = 0,
    val price: Int = 0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "type" to type,
            "imageResId" to imageResId,
            "price" to price
        )
    }
}

// Data class for PetGallery
data class PetGalleryItem(
    val type: String,
    @DrawableRes val babyImage: Int,
    @DrawableRes val teenImage: Int,
    @DrawableRes val adultImage: Int
)
