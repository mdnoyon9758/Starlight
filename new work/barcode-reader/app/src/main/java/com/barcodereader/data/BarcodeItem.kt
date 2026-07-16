package com.barcodereader.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class BarcodeItem(
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val format: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    fun toJson(): String = Gson().toJson(this)

    companion object {
        fun fromJson(json: String): BarcodeItem = Gson().fromJson(json, BarcodeItem::class.java)
    }
}