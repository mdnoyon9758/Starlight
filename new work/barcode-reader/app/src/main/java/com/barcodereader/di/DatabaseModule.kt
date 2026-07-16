package com.barcodereader.di

import android.content.Context
import com.barcodereader.data.local.database.AppDatabase
import com.barcodereader.data.local.database.dao.CategoryDao
import com.barcodereader.data.local.database.dao.FolderDao
import com.barcodereader.data.local.database.dao.ScanHistoryDao
import com.barcodereader.data.local.database.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database Module
 *
 * Provides Room database and DAO instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.create(context)
    }

    @Provides
    @Singleton
    fun provideScanHistoryDao(database: AppDatabase): ScanHistoryDao {
        return database.scanHistoryDao()
    }

    @Provides
    @Singleton
    fun provideFolderDao(database: AppDatabase): FolderDao {
        return database.folderDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideTagDao(database: AppDatabase): TagDao {
        return database.tagDao()
    }
}