package androidx.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.utildev.pagingnetworksample.repository.NetworkState

private fun getErrorMessage(report: PagingRequestHelper.Companion.StatusReport): String {
    return PagingRequestHelper.RequestType.values().mapNotNull {
        report.getErrorFor(it)?.message
    }.first()
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
    val liveData = MutableLiveData<NetworkState>()
    addListener { report ->
        when {
            report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
            report.hasError() -> liveData.postValue(
                NetworkState.error(
                    getErrorMessage(
                        report
                    )
                ))
            else -> liveData.postValue(NetworkState.LOADED)
        }
    }
    return liveData
}