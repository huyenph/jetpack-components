package com.utildev.pagingnetworksample.repository.inDB

import androidx.paging.PagedList
import com.utildev.pagingnetworksample.api.RedditApi
import com.utildev.pagingnetworksample.model.ListingResponse
import com.utildev.pagingnetworksample.model.RedditPost
import java.util.concurrent.Executor

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
class SubredditBoundaryCallback(
    private val subredditName: String,
    private val redditApi: RedditApi,
    private val handleResponse: (String, ListingResponse?) -> Unit,
    private val ioExecutor: Executor,
    private val networkPageSize: Int
) : PagedList.BoundaryCallback<RedditPost>() {

}