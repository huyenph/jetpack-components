package com.utildev.pagingnetworksample.data

import androidx.lifecycle.MutableLiveData
import com.utildev.pagingnetworksample.api.GithubService
import com.utildev.pagingnetworksample.api.searchRepos
import com.utildev.pagingnetworksample.db.GithubLocalCache
import com.utildev.pagingnetworksample.model.RepoSearchResult

class GithubRepository(
    private val service: GithubService,
    private val cache: GithubLocalCache
) {
    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

    // LiveData of network errors.
    private val networkErrors = MutableLiveData<String>()

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

    /**
     * Search repositories whose names match the query.
     */
    fun search(query: String): RepoSearchResult {
        lastRequestedPage = 1
        requestAndSaveData(query)

        // Get data from the local cache
        val data = cache.reposByName(query)

        return RepoSearchResult(data, networkErrors)
    }

    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        searchRepos(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, { repos ->
            cache.insert(repos) {
                lastRequestedPage++
                isRequestInProgress = false
            }
        }, { error ->
            networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }

    fun requestMore(query: String) {
        requestAndSaveData(query)
    }
}