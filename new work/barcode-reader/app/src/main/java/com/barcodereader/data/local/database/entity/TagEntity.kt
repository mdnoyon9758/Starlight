package com.barcodereader.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tag Entity
 *
 * Represents a tag for labeling scan history items.
 * Tags provide flexible, many-to-many classification.
 */
@Entity(
    tableName = "tags",
    indices = [
        Index(value = ["name"]),
        Index(value = ["createdAt"])
    ]
)
data class TagEntity(
    /** Unique identifier */
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),

    /** Tag name */
    val name: String,

    /** Tag color (ARGB long value) */
    val color: Long = 0xFF6750A4L,

    /** When this tag was created */
    val createdAt: Long = System.currentTimeMillis()
)