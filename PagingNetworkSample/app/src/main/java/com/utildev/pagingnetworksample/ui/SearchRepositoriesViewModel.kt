package com.utildev.pagingnetworksample.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.utildev.pagingnetworksample.data.GithubRepository
import com.utildev.pagingnetworksample.model.Repo
import com.utildev.pagingnetworksample.model.RepoSearchResult

class SearchRepositoriesViewModel(private val repository: GithubRepository) : ViewModel() {
    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()
    private val repoResult: LiveData<RepoSearchResult> = Transformations.map(queryLiveData) {
        repository.search(it)
    }

    val repos: LiveData<List<Repo>> = Transformations.switchMap(repoResult) { it.data }
    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult) { it.networkErrors }

    fun searchRepo(query: String) {
        queryLiveData.postValue(query)
    }

    private fun lastQueryValue(): String? = queryLiveData.value

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            val immutableQuery = lastQueryValue()
            if (immutableQuery != null) {
                repository.requestMore(immutableQuery)
            }
        }
    }
}