package com.utildev.pagingnetworksample.repository

import com.utildev.pagingnetworksample.model.RedditPost

/**
 * Common interface shared by the different repository implementations.
 * Note: this only exists for sample purposes - typically an app would implement a repo once, either
 * network+db, or network-only
 */
interface RedditPostRepository {
    enum class Type {
        IN_MEMORY_BY_ITEM,
        IN_MEMORY_BY_PAGE,
        DB
    }

    fun postsOfSubreddit(subreddit: String, pageSize: Int): Listing<RedditPost>
}