package com.barcodereader.data.local.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.barcodereader.data.local.database.AppDatabase
import com.barcodereader.data.local.database.entity.ScanHistoryEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Legacy Storage Migrator
 *
 * Migrates data from SharedPreferences to Room database.
 * Runs once on first launch after Room integration.
 */
class LegacyStorageMigrator @Inject constructor() {

    private val gson = Gson()

    /**
     * Migrate from SharedPreferences to Room
     * Returns true if migration was successful
     */
    suspend fun migrate(context: Context, database: AppDatabase): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val prefs = getSharedPreferences(context)

                // Check if we already migrated
                if (prefs.getBoolean("migrated_to_room", false)) {
                    return@withContext true
                }

                // Try to migrate from "history" key (HistoryStorage format)
                val migrated = migrateFromHistoryKey(prefs, database)

                // Try to migrate from "items" key (BarcodeRepository format)
                if (!migrated) {
                    migrateFromItemsKey(prefs, database)
                }

                // Mark as migrated
                prefs.edit().putBoolean("migrated_to_room", true).apply()

                true
            } catch (e: Exception) {
                false
            }
        }
    }

    private suspend fun migrateFromHistoryKey(
        prefs: SharedPreferences,
        database: AppDatabase
    ): Boolean {
        return try {
            val json = prefs.getString("history", "[]") ?: "[]"
            val type = object : TypeToken<List<LegacyScanHistory>>() {}.type
            val legacyScans: List<LegacyScanHistory> = gson.fromJson(json, type) ?: emptyList()

            if (legacyScans.isNotEmpty()) {
                val entities = legacyScans.map { it.toEntity() }
                database.scanHistoryDao().insertAll(entities)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun migrateFromItemsKey(
        prefs: SharedPreferences,
        database: AppDatabase
    ): Boolean {
        return try {
            val json = prefs.getString("items", "[]") ?: "[]"
            val type = object : TypeToken<List<LegacyBarcodeItem>>() {}.type
            val legacyItems: List<LegacyBarcodeItem> = gson.fromJson(json, type) ?: emptyList()

            if (legacyItems.isNotEmpty()) {
                val entities = legacyItems.map { it.toEntity() }
                database.scanHistoryDao().insertAll(entities)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("barcode_history", Context.MODE_PRIVATE)
    }

    /**
     * Legacy ScanHistory format for migration
     */
    private data class LegacyScanHistory(
        val id: Long = System.currentTimeMillis(),
        val content: String = "",
        val format: String = "",
        val type: String = "UNKNOWN",
        val timestamp: Long = System.currentTimeMillis(),
        val isFavorite: Boolean = false
    ) {
        fun toEntity(): ScanHistoryEntity {
            return ScanHistoryEntity(
                id = id,
                content = content,
                format = format,
                type = type,
                timestamp = timestamp,
                isFavorite = isFavorite
            )
        }
    }

    /**
     * Legacy BarcodeItem format for migration
     */
    private data class LegacyBarcodeItem(
        val id: Long = System.currentTimeMillis(),
        val content: String = "",
        val format: String = "",
        val timestamp: Long = System.currentTimeMillis(),
        val isFavorite: Boolean = false
    ) {
        fun toEntity(): ScanHistoryEntity {
            return ScanHistoryEntity(
                id = id,
                content = content,
                format = format,
                type = "UNKNOWN",
                timestamp = timestamp,
                isFavorite = isFavorite
            )
        }
    }
}