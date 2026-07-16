package com.barcodereader.data.local.database.entity

import androidx.room.Entity

/**
 * Scan-Tag Cross Reference
 *
 * Represents the many-to-many relationship between scans and tags.
 * A scan can have multiple tags, and a tag can be applied to multiple scans.
 */
@Entity(
    tableName = "scan_tag_cross_ref",
    primaryKeys = ["scanId", "tagId"]
)
data class ScanTagCrossRef(
    /** ID of the scan */
    val scanId: Long,

    /** ID of the tag */
    val tagId: Long
)