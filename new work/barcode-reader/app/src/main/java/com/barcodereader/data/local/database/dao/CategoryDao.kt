package com.barcodereader.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.barcodereader.data.local.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Category DAO
 *
 * Data Access Object for category operations.
 * Provides CRUD operations for classifying scan history.
 */
@Dao
interface CategoryDao {

    // ==================== INSERT ====================

    /**
     * Insert a category
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    /**
     * Insert multiple categories
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    // ==================== UPDATE ====================

    /**
     * Update a category
     */
    @Update
    suspend fun update(category: CategoryEntity)

    // ==================== DELETE ====================

    /**
     * Delete a category
     */
    @Delete
    suspend fun delete(category: CategoryEntity)

    /**
     * Delete a category by ID
     */
    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Delete all categories
     */
    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    // ==================== QUERY ====================

    /**
     * Get all categories ordered by name
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Get all categories as list
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategoriesList(): List<CategoryEntity>

    /**
     * Get a category by ID
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    /**
     * Get a category by ID as Flow
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<CategoryEntity?>

    /**
     * Search categories by name
     */
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<CategoryEntity>>

    /**
     * Get category count
     */
    @Query("SELECT COUNT(*) FROM categories")
    fun getCategoryCount(): Flow<Int>

    /**
     * Get category with scan count
     */
    @Query("""
        SELECT c.*, COUNT(s.id) as scanCount 
        FROM categories c 
        LEFT JOIN scan_history s ON c.id = s.categoryId 
        GROUP BY c.id 
        ORDER BY c.name ASC
    """)
    fun getCategoriesWithCount(): Flow<List<CategoryWithCount>>
}

/**
 * Data class for category with scan count
 */
data class CategoryWithCount(
    val id: Long,
    val name: String,
    val color: Long,
    val icon: String,
    val createdAt: Long,
    val scanCount: Int
)