package com.barcodereader.data.repository

import com.barcodereader.data.local.database.dao.FolderDao
import com.barcodereader.data.local.database.entity.FolderEntity
import com.barcodereader.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Folder Repository
 *
 * Handles folder operations for organizing scans.
 */
@Singleton
class FolderRepository @Inject constructor(
    private val folderDao: FolderDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun getAllFolders(): Flow<List<FolderEntity>> = folderDao.getAllFolders()

    suspend fun getFolderById(id: Long): FolderEntity? = withContext(ioDispatcher) {
        folderDao.getById(id)
    }

    suspend fun insertFolder(folder: FolderEntity): Long = withContext(ioDispatcher) {
        folderDao.insert(folder)
    }

    suspend fun updateFolder(folder: FolderEntity) = withContext(ioDispatcher) {
        folderDao.update(folder)
    }

    suspend fun deleteFolder(folder: FolderEntity) = withContext(ioDispatcher) {
        folderDao.delete(folder)
    }

    suspend fun deleteFolderById(id: Long) = withContext(ioDispatcher) {
        folderDao.deleteById(id)
    }

    fun searchFolders(query: String): Flow<List<FolderEntity>> = folderDao.search(query)
}