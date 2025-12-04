package com.devux.finflow.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.devux.finflow.core.dao.BudgetDao
import com.devux.finflow.core.dao.CategoryDao
import com.devux.finflow.core.dao.GoalDao
import com.devux.finflow.core.dao.TransactionDao
import com.devux.finflow.core.dao.UserDao
import com.devux.finflow.core.db.AppDatabase
import com.devux.finflow.data.repository.budget.BudgetRepository
import com.devux.finflow.data.repository.budget.BudgetRepositoryImpl
import com.devux.finflow.data.repository.category.CategoryRepository
import com.devux.finflow.data.repository.category.CategoryRepositoryImpl
import com.devux.finflow.data.repository.goal.GoalRepository
import com.devux.finflow.data.repository.goal.GoalRepositoryImpl
import com.devux.finflow.data.repository.transaction.TransactionRepository
import com.devux.finflow.data.repository.transaction.TransactionRepositoryImpl
import com.devux.finflow.helper.PreferencesHelper
import com.devux.finflow.utils.TABLE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            TABLE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTransactionDao(appDatabase: AppDatabase): TransactionDao =
        appDatabase.transactionDao()

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao =
        appDatabase.categoryDao()

    @Provides
    fun provideBudgetDao(appDatabase: AppDatabase): BudgetDao =
        appDatabase.budgetDao()

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao =
        appDatabase.userDao()

    @Provides
    fun provideGoalDao(appDatabase: AppDatabase): GoalDao =
        appDatabase.goalDao()

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao
    ): CategoryRepository = CategoryRepositoryImpl(categoryDao)

    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao
    ): TransactionRepository = TransactionRepositoryImpl(transactionDao)

    @Provides
    @Singleton
    fun provideGoalRepository(
        goalDao: GoalDao
    ): GoalRepository = GoalRepositoryImpl(goalDao)

    @Provides
    @Singleton
    fun provideBudgetRepository(
        budgetDao: BudgetDao
    ): BudgetRepository = BudgetRepositoryImpl(budgetDao)

    @Provides
    @Singleton
    fun providePreferencesHelper(
        @ApplicationContext context: Context
    ): PreferencesHelper {
        return PreferencesHelper(context)
    }
}
