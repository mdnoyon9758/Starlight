package com.barcodereader.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Folder Entity
 *
 * Represents a folder for organizing scan history items.
 * Users can create folders to categorize their scans.
 */
@Entity(
    tableName = "folders",
    indices = [
        Index(value = ["name"]),
        Index(value = ["createdAt"])
    ]
)
data class FolderEntity(
    /** Unique identifier */
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),

    /** Folder name */
    val name: String,

    /** Folder color (ARGB long value) */
    val color: Long = 0xFF6750A4L,

    /** Folder icon identifier */
    val icon: String = "folder",

    /** When this folder was created */
    val createdAt: Long = System.currentTimeMillis(),

    /** When this folder was last updated */
    val updatedAt: Long = System.currentTimeMillis()
)