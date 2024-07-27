package com.mobdeve.s11and12.group1.to_dororo

import androidx.annotation.DrawableRes

data class PetData(
    val id: String = "", // Unique ID from Firestore
    val type: String = "",
    var heartsPet: Int = 0,
    var drawableResId: Int = 0,
    var maxHearts: Int = 0,
    var stage: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "type" to type,
            "heartsPet" to heartsPet,
            "drawableResId" to drawableResId,
            "maxHearts" to maxHearts,
            "stage" to stage
        )
    }
}
