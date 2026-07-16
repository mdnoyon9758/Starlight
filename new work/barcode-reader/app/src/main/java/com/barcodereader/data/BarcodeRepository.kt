package com.barcodereader.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BarcodeRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("barcode_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getHistory(): List<BarcodeItem> {
        val json = prefs.getString("items", "[]") ?: "[]"
        val type = object : TypeToken<List<BarcodeItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addItem(item: BarcodeItem) {
        val items = getHistory().toMutableList()
        // Avoid duplicates of same content
        val existingIndex = items.indexOfFirst { it.content == item.content }
        if (existingIndex >= 0) {
            items.removeAt(existingIndex)
        }
        items.add(0, item)
        // Keep max 200 items
        val trimmed = if (items.size > 200) items.take(200) else items
        saveItems(trimmed)
    }

    fun deleteItem(id: Long) {
        val items = getHistory().toMutableList()
        items.removeAll { it.id == id }
        saveItems(items)
    }

    fun toggleFavorite(id: Long) {
        val items = getHistory().toMutableList()
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0) {
            items[index] = items[index].copy(isFavorite = !items[index].isFavorite)
            saveItems(items)
        }
    }

    fun clearHistory() {
        saveItems(emptyList())
    }

    fun search(query: String): List<BarcodeItem> {
        if (query.isBlank()) return getHistory()
        return getHistory().filter {
            it.content.contains(query, ignoreCase = true) ||
                    it.format.contains(query, ignoreCase = true)
        }
    }

    private fun saveItems(items: List<BarcodeItem>) {
        prefs.edit().putString("items", gson.toJson(items)).apply()
    }
}