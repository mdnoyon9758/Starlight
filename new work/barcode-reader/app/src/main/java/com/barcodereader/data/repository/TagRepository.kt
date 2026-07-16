package com.barcodereader.data.repository

import com.barcodereader.data.local.database.dao.TagDao
import com.barcodereader.data.local.database.entity.TagEntity
import com.barcodereader.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tag Repository
 *
 * Handles tag operations for labeling scans.
 */
@Singleton
class TagRepository @Inject constructor(
    private val tagDao: TagDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAllTags()

    suspend fun getTagById(id: Long): TagEntity? = withContext(ioDispatcher) {
        tagDao.getById(id)
    }

    suspend fun insertTag(tag: TagEntity): Long = withContext(ioDispatcher) {
        tagDao.insert(tag)
    }

    suspend fun updateTag(tag: TagEntity) = withContext(ioDispatcher) {
        tagDao.update(tag)
    }

    suspend fun deleteTag(tag: TagEntity) = withContext(ioDispatcher) {
        tagDao.delete(tag)
    }

    suspend fun deleteTagById(id: Long) = withContext(ioDispatcher) {
        tagDao.deleteById(id)
    }

    fun searchTags(query: String): Flow<List<TagEntity>> = tagDao.search(query)

    fun getTagsForScan(scanId: Long): Flow<List<TagEntity>> = tagDao.getTagsForScan(scanId)

    suspend fun addTagToScan(scanId: Long, tagId: Long) = withContext(ioDispatcher) {
        tagDao.addTagToScan(scanId, tagId)
    }

    suspend fun removeTagFromScan(scanId: Long, tagId: Long) = withContext(ioDispatcher) {
        tagDao.removeTagFromScan(scanId, tagId)
    }
}