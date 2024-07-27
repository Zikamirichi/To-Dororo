package com.mobdeve.s11and12.group1.to_dororo

import androidx.annotation.DrawableRes

data class PetData(
    val type: String = "",
    var hearts: Int = 0,
    @DrawableRes var drawableResId: Int = R.drawable.adopt_a_pet,
    var maxHearts: Int = 3000
) {
    companion object {
        fun fromDocument(doc: Map<String, Any>): PetData {
            val type = doc["type"] as String? ?: ""
            val hearts = (doc["hearts"] as Long? ?: 0).toInt()
            val maxHearts = when {
                hearts >= 5000 -> 5000
                hearts >= 3000 -> 5000
                else -> 3000
            }
            val drawableResId = when (type) {
                "Cat" -> when {
                    hearts >= 5000 -> R.drawable.cat_adult
                    hearts >= 3000 -> R.drawable.cat_teen
                    else -> R.drawable.cat_baby
                }
                "Dog" -> when {
                    hearts >= 5000 -> R.drawable.dog_adult
                    hearts >= 3000 -> R.drawable.dog_teen
                    else -> R.drawable.dog_baby
                }
                "Parrot" -> when {
                    hearts >= 5000 -> R.drawable.parrot_adult
                    hearts >= 3000 -> R.drawable.parrot_teen
                    else -> R.drawable.parrot_baby
                }
                else -> R.drawable.adopt_a_pet
            }
            return PetData(type, hearts, drawableResId, maxHearts)
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "type" to type,
            "hearts" to hearts,
            "drawableResId" to drawableResId,
            "maxHearts" to maxHearts
        )
    }
}
