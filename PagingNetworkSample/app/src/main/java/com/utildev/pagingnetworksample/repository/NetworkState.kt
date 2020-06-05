package com.utildev.pagingnetworksample.repository

enum class Status {
    RUNNING, SUCCESS, FAILED
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status, val msg: String? = null
) {
    companion object {
        val LOADED = NetworkState(status = Status.SUCCESS)
        val LOADING = NetworkState(status = Status.RUNNING)
        fun error(msg: String?) = NetworkState(status = Status.FAILED, msg = msg)
    }
}