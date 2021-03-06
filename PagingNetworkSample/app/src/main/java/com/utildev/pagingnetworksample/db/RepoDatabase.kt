package com.utildev.pagingnetworksample.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.utildev.pagingnetworksample.model.Repo

@Database(entities = [Repo::class], version = 1, exportSchema = false)
abstract class RepoDatabase : RoomDatabase() {
    abstract fun reposDao(): RepoDao

    companion object {
        @Volatile
        private var instance: RepoDatabase? = null

        fun getInstance(context: Context): RepoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, RepoDatabase::class.java, "Github.db")
                    .build()
            }

    }
}