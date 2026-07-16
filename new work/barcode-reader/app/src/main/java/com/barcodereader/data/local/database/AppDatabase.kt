package com.barcodereader.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.barcodereader.data.local.database.converter.Converters
import com.barcodereader.data.local.database.dao.CategoryDao
import com.barcodereader.data.local.database.dao.FolderDao
import com.barcodereader.data.local.database.dao.ScanHistoryDao
import com.barcodereader.data.local.database.dao.TagDao
import com.barcodereader.data.local.database.entity.CategoryEntity
import com.barcodereader.data.local.database.entity.FolderEntity
import com.barcodereader.data.local.database.entity.ScanHistoryEntity
import com.barcodereader.data.local.database.entity.ScanTagCrossRef
import com.barcodereader.data.local.database.entity.TagEntity

/**
 * Room Database
 *
 * Main database for the application.
 * Contains all entities and provides DAO access.
 */
@Database(
    entities = [
        ScanHistoryEntity::class,
        FolderEntity::class,
        CategoryEntity::class,
        TagEntity::class,
        ScanTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Scan history DAO
     */
    abstract fun scanHistoryDao(): ScanHistoryDao

    /**
     * Folder DAO
     */
    abstract fun folderDao(): FolderDao

    /**
     * Category DAO
     */
    abstract fun categoryDao(): CategoryDao

    /**
     * Tag DAO
     */
    abstract fun tagDao(): TagDao

    companion object {
        const val DATABASE_NAME = "barcode_reader_db"

        /**
         * Create database instance
         */
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}