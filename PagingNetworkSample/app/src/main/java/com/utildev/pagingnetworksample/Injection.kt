package com.utildev.pagingnetworksample

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.utildev.pagingnetworksample.api.GithubService
import com.utildev.pagingnetworksample.data.GithubRepository
import com.utildev.pagingnetworksample.db.GithubLocalCache
import com.utildev.pagingnetworksample.db.RepoDatabase
import com.utildev.pagingnetworksample.ui.ViewModelFactory
import java.util.concurrent.Executors

object Injection {
    /**
     * Creates an instance of [GithubLocalCache] based on the database DAO.
     */
    private fun provideCache(context: Context): GithubLocalCache {
        val database = RepoDatabase.getInstance(context)
        return GithubLocalCache(database.reposDao(), Executors.newSingleThreadExecutor())
    }

    /**
     * Creates an instance of [GithubRepository] based on the [GithubService] and a
     * [GithubLocalCache]
     */
    private fun provideRepository(context: Context): GithubRepository {
        return GithubRepository(GithubService.create(), provideCache(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideRepository(context))
    }
}