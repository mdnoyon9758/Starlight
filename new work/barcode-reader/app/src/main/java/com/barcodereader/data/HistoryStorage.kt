package com.barcodereader.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("barcode_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getHistory(): List<ScanHistory> {
        val json = prefs.getString("history", "[]") ?: "[]"
        val type = object : TypeToken<List<ScanHistory>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addEntry(entry: ScanHistory) {
        val list = getHistory().toMutableList()
        list.add(0, entry)
        // Keep max 200 entries
        val trimmed = if (list.size > 200) list.take(200) else list
        saveHistory(trimmed)
    }

    fun deleteEntry(id: Long) {
        val list = getHistory().toMutableList()
        list.removeAll { it.id == id }
        saveHistory(list)
    }

    fun clearAll() {
        saveHistory(emptyList())
    }

    fun toggleFavorite(id: Long) {
        val list = getHistory().toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index >= 0) {
            list[index] = list[index].copy(isFavorite = !list[index].isFavorite)
            saveHistory(list)
        }
    }

    fun getFavorites(): List<ScanHistory> = getHistory().filter { it.isFavorite }

    fun search(query: String): List<ScanHistory> {
        if (query.isBlank()) return getHistory()
        return getHistory().filter {
            it.content.contains(query, ignoreCase = true) ||
            it.format.contains(query, ignoreCase = true) ||
            it.type.contains(query, ignoreCase = true)
        }
    }

    private fun saveHistory(list: List<ScanHistory>) {
        prefs.edit().putString("history", gson.toJson(list)).apply()
    }

    /**
     * Convenience method used by the UI layer to retrieve the full list of entries.
     * It simply forwards to [getHistory] which returns the persisted list.
     */
    fun getAllEntries(): List<ScanHistory> = getHistory()
}