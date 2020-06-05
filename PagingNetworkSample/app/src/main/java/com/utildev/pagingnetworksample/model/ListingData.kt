package com.utildev.pagingnetworksample.model

class ListingData(
    val children: List<RedditChildrenResponse>,
    val after: String?,
    val before: String?
)