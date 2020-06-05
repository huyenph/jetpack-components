package com.utildev.pagingnetworksample.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.utildev.pagingnetworksample.model.RedditPost

/**
 * Database schema used by the DbRedditPostRepository
 */
@Database(entities = [RedditPost::class], version = 1, exportSchema = false)
abstract class RedditDatabase : RoomDatabase() {
    abstract fun postDao(): RedditPostDao

    companion object {
        @Volatile
        private var instance: RedditDatabase? = null

        fun getInstance(context: Context, useInMemory: Boolean): RedditDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDB(context, useInMemory).also { instance = it }
            }

        private fun buildDB(context: Context, useInMemory: Boolean): RedditDatabase {
            val dbBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, RedditDatabase::class.java)
            } else {
                Room.databaseBuilder(context, RedditDatabase::class.java, "reddit.db")
            }
            return dbBuilder.fallbackToDestructiveMigration().build()
        }
    }
}