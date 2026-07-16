package com.barcodereader.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Scan History Entity
 *
 * Represents a single scan record in the database.
 * Stores the scanned content, type, format, and metadata.
 */
@Entity(
    tableName = "scan_history",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["type"]),
        Index(value = ["folderId"]),
        Index(value = ["categoryId"]),
        Index(value = ["isFavorite"]),
        Index(value = ["isPinned"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class ScanHistoryEntity(
    /** Unique identifier (timestamp-based) */
    @PrimaryKey
    val id: Long,

    /** Scanned content (barcode value, URL, text, etc.) */
    val content: String,

    /** Barcode format (QR_CODE, CODE_128, EAN_13, etc.) */
    val format: String,

    /** Content type (URL, EMAIL, PHONE, PRODUCT, WIFI, TEXT, etc.) */
    val type: String,

    /** Timestamp when the scan occurred */
    val timestamp: Long,

    /** Whether this item is marked as favorite */
    val isFavorite: Boolean = false,

    /** Whether this item is pinned to top */
    val isPinned: Boolean = false,

    /** ID of the folder this item belongs to (nullable) */
    val folderId: Long? = null,

    /** ID of the category this item belongs to (nullable) */
    val categoryId: Long? = null,

    /** User notes for this scan */
    val notes: String? = null,

    /** Number of times this barcode has been scanned */
    val scanCount: Int = 1,

    /** When this record was last updated */
    val updatedAt: Long = System.currentTimeMillis()
)