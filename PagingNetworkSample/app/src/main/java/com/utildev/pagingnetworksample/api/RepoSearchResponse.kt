package com.utildev.pagingnetworksample.api

import com.google.gson.annotations.SerializedName
import com.utildev.pagingnetworksample.model.Repo

data class RepoSearchResponse(
    @SerializedName("total_count") val total: Int = 0,
    @SerializedName("items") val items: List<Repo>
)