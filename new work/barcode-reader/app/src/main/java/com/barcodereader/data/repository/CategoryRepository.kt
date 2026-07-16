package com.barcodereader.data.repository

import com.barcodereader.data.local.database.dao.CategoryDao
import com.barcodereader.data.local.database.entity.CategoryEntity
import com.barcodereader.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Category Repository
 *
 * Handles category operations for classifying scans.
 */
@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): CategoryEntity? = withContext(ioDispatcher) {
        categoryDao.getById(id)
    }

    suspend fun insertCategory(category: CategoryEntity): Long = withContext(ioDispatcher) {
        categoryDao.insert(category)
    }

    suspend fun updateCategory(category: CategoryEntity) = withContext(ioDispatcher) {
        categoryDao.update(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) = withContext(ioDispatcher) {
        categoryDao.delete(category)
    }

    suspend fun deleteCategoryById(id: Long) = withContext(ioDispatcher) {
        categoryDao.deleteById(id)
    }

    fun searchCategories(query: String): Flow<List<CategoryEntity>> = categoryDao.search(query)
}