package com.utildev.pagingnetworksample.repository.inDB

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.toLiveData
import com.utildev.pagingnetworksample.api.RedditApi
import com.utildev.pagingnetworksample.db.RedditDatabase
import com.utildev.pagingnetworksample.model.ListingResponse
import com.utildev.pagingnetworksample.model.RedditPost
import com.utildev.pagingnetworksample.repository.Listing
import com.utildev.pagingnetworksample.repository.NetworkState
import com.utildev.pagingnetworksample.repository.RedditPostRepository
import com.utildev.pagingnetworksample.repository.Status
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class DBRedditPostRepository(
    val db: RedditDatabase,
    private val redditApi: RedditApi,
    private val ioExecutor: Executor,
    private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE
) : RedditPostRepository {
    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 10
    }

    /**
     * Returns a Listing for the given subreddit.
     */
    @MainThread
    override fun postsOfSubreddit(subreddit: String, pageSize: Int): Listing<RedditPost> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = SubredditBoundaryCallback(
            subredditName = subreddit,
            redditApi = redditApi,
            handleResponse = this::insertResultIntoDb,
            ioExecutor = ioExecutor,
            networkPageSize = networkPageSize
        )

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = refreshTrigger.switchMap {
            refresh(subreddit)
        }

        // We use toLiveData Kotlin extension function here, you could also use LivePagedListBuilder
        val livePagedList = db.postDao().postsBySubreddit(subreddit).toLiveData(
            pageSize = pageSize,
            boundaryCallback = boundaryCallback
        )

//        return Listing(
//            pagedList = livePagedList,
//
//            )
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private fun refresh(subredditName: String): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        redditApi.getTop(subredditName, networkPageSize)
            .enqueue(object : Callback<ListingResponse> {
                override fun onFailure(call: Call<ListingResponse>, t: Throwable) {
                    networkState.value = NetworkState.error(t.message)
                }

                override fun onResponse(
                    call: Call<ListingResponse>,
                    response: Response<ListingResponse>
                ) {
                    ioExecutor.execute {
                        db.runInTransaction {
                            db.postDao().deleteBySubreddit(subredditName)
                            insertResultIntoDb(subredditName, response.body())
                        }
                        networkState.value = NetworkState.LOADED
                    }
                }

            })
        return networkState
    }

    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private fun insertResultIntoDb(subredditName: String, body: ListingResponse?) {
        body!!.data.children.let { posts ->
            db.runInTransaction {
                val start = db.postDao().getNextIndexInSubreddit(subredditName)
                val items = posts.mapIndexed { index, child ->
                    child.data.indexInResponse = start + index
                    child.data
                }
                db.postDao().insert(items)
            }
        }
    }
}