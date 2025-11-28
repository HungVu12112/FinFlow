package com.devux.finflow.data.repository.category

import com.devux.finflow.core.dao.CategoryDao
import com.devux.finflow.data.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {

    override suspend fun insertCategory(category: CategoryEntity) {
        dao.insertCategory(category)
    }

    override suspend fun deleteCategory(category: CategoryEntity) {
        dao.deleteCategory(category)
    }

    override suspend fun updateCategory(category: CategoryEntity) {
        dao.updateCategory(category)
    }

    override fun getAllCategories(): Flow<List<CategoryEntity>> {
        return dao.getAllCategories()
    }
}
