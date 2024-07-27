package com.mobdeve.s11and12.group1.to_dororo

import androidx.annotation.DrawableRes

data class PetData(
    val type: String = "",
    var heartsPet: Int = 0,
    @DrawableRes var drawableResId: Int = R.drawable.adopt_a_pet,
    var maxHearts: Int = 3000,
    var stage: String = ""
) {
    companion object {
        fun fromDocument(doc: Map<String, Any>): PetData {
            val type = doc["type"] as String? ?: ""
            val heartsPet = (doc["hearts"] as Long? ?: 0).toInt()
            val maxHearts = when {
                heartsPet >= 5000 -> 5000
                heartsPet >= 3000 -> 5000
                else -> 3000
            }
            val stage = doc["stage"] as String? ?: "baby"
            val drawableResId = when (type) {
                "Cat" -> when (stage) {
                    "adult" -> R.drawable.cat_adult
                    "teen" -> R.drawable.cat_teen
                    else -> R.drawable.cat_baby
                }
                "Dog" -> when (stage) {
                    "adult" -> R.drawable.dog_adult
                    "teen" -> R.drawable.dog_teen
                    else -> R.drawable.dog_baby
                }
                "Parrot" -> when (stage) {
                    "adult" -> R.drawable.parrot_adult
                    "teen" -> R.drawable.parrot_teen
                    else -> R.drawable.parrot_baby
                }
                else -> R.drawable.adopt_a_pet
            }
            return PetData(type, heartsPet, drawableResId, maxHearts, stage)
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "type" to type,
            "heartsPet" to heartsPet,
            "drawableResId" to drawableResId,
            "maxHearts" to maxHearts,
            "stage" to stage
        )
    }
}
