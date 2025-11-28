package com.devux.finflow.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devux.finflow.core.converter.LocalDateConverter
import com.devux.finflow.core.dao.BudgetDao
import com.devux.finflow.core.dao.CategoryDao
import com.devux.finflow.core.dao.TransactionDao
import com.devux.finflow.core.dao.UserDao
import com.devux.finflow.data.AccountEntity
import com.devux.finflow.data.BudgetEntity
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.utils.TABLE_NAME

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class, BudgetEntity::class, AccountEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun userDao(): UserDao

    companion object {
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, TABLE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}