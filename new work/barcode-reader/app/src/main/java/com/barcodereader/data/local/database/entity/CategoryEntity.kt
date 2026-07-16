package com.barcodereader.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Category Entity
 *
 * Represents a category for classifying scan history items.
 * Categories provide a way to group scans by purpose or type.
 */
@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name"]),
        Index(value = ["createdAt"])
    ]
)
data class CategoryEntity(
    /** Unique identifier */
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),

    /** Category name */
    val name: String,

    /** Category color (ARGB long value) */
    val color: Long = 0xFF6750A4L,

    /** Category icon identifier */
    val icon: String = "category",

    /** When this category was created */
    val createdAt: Long = System.currentTimeMillis()
)