package com.utildev.paging.data

import android.util.Log
import androidx.paging.LivePagedListBuilder
import com.utildev.paging.api.GithubService
import com.utildev.paging.db.GithubLocalCache
import com.utildev.paging.model.RepoSearchResult

class GithubRepository(private val service: GithubService, private val cache: GithubLocalCache) {
    companion object {
        private const val DATABASE_PAGE_SIZE = 10
    }

    /**
     * Search repositories whose names match the query.
     */
    fun search(query: String): RepoSearchResult {
        Log.d("GithubRepository", "New query: $query")

        // Get data source factory from the local cache
        val dataSourceFactory = cache.reposByName(query)

        // Construct boundary callback
        val boundaryCallback = RepoBoundaryCallback(query, service, cache)
        val networkErrors = boundaryCallback.networkErrors

        // Get the paged list
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()

        // Get the network errors exposed by the boundary callback
        return RepoSearchResult(data, networkErrors)
    }
}

//// keep the last requested page. When the request is successful, increment the page number.
//private var lastRequestedPage = 1
//
//// LiveData of network errors.
//private val networkErrors = MutableLiveData<String>()
//
//// avoid triggering multiple requests in the same time
//private var isRequestInProgress = false
//
///**
// * Search repositories whose names match the query.
// */
//fun search(query: String): RepoSearchResult {
//    Log.d("GithubRepository", "New query: $query")
//    lastRequestedPage = 1
//    requestAndSaveData(query)
//
//    // Get data from the local cache
//    val data = cache.reposByName(query)
//
//    return RepoSearchResult(data, networkErrors)
//}
//
//fun requestMore(query: String) {
//    requestAndSaveData(query)
//}
//
//private fun requestAndSaveData(query: String) {
//    if (isRequestInProgress) return
//    isRequestInProgress = true
//    searchRepos(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, { repos ->
//        cache.insert(repos) {
//            lastRequestedPage++
//            isRequestInProgress = false
//        }
//    }, { error ->
//        networkErrors.postValue(error)
//        isRequestInProgress = false
//    })
//}