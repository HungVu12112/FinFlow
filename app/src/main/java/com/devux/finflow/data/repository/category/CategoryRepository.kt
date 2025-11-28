package com.devux.finflow.data.repository.category

import com.devux.finflow.data.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun insertCategory(category: CategoryEntity)
    suspend fun deleteCategory(category: CategoryEntity)
    suspend fun updateCategory(category: CategoryEntity)
    fun getAllCategories(): Flow<List<CategoryEntity>>
}