package com.eisenhower.matrix.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.eisenhower.matrix.data.db.AppDatabase
import com.eisenhower.matrix.data.db.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Расширение для создания DataStore как синглтона на уровне Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "eisenhower_prefs"
)

/**
 * Hilt-модуль для предоставления зависимостей уровня приложения.
 * @InstallIn(SingletonComponent) — объекты живут столько же, сколько приложение.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Предоставляет единственный экземпляр базы данных Room.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "eisenhower_db"
        ).build()
    }

    /**
     * Предоставляет DAO через базу данных.
     */
    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    /**
     * Предоставляет DataStore для настроек (тема, показ туториала).
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
