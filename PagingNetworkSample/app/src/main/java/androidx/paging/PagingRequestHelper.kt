package androidx.paging

import android.os.Handler
import androidx.annotation.AnyThread
import androidx.annotation.GuardedBy
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A helper class for {@link androidx.paging.PagedList.BoundaryCallback BoundaryCallback}s and
 * {@link DataSource}s to help with tracking network requests.
 * <p>
 * It is designed to support 3 types of requests,
 * {@link RequestType#INITIAL INITIAL},
 * {@link RequestType#BEFORE BEFORE},
 * {@link RequestType#AFTER AFTER}
 * and runs only 1 request
 * for each of them via {@link #runIfNotRunning(RequestType, Request)}.
 * <p>
 * It tracks a {@link Status} and an {@code error} for each {@link RequestType}.
 * <p>
 * A sample usage of this class to limit requests looks like this:
 * <pre>
 * class PagingBoundaryCallback extends PagedList.BoundaryCallback&lt;MyItem> {
 *     // TODO replace with an executor from your application
 *     Executor executor = Executors.newSingleThreadExecutor();
 *     PagingRequestHelper helper = new PagingRequestHelper(executor);
 *     // imaginary API service, using Retrofit
 *     MyApi api;
 *
 *     {@literal @}Override
 *     public void onItemAtFrontLoaded({@literal @}NonNull MyItem itemAtFront) {
 *         helper.runIfNotRunning(PagingRequestHelper.RequestType.BEFORE,
 *                 helperCallback -> api.getTopBefore(itemAtFront.getName(), 10).enqueue(
 *                         new Callback&lt;ApiResponse>() {
 *                             {@literal @}Override
 *                             public void onResponse(Call&lt;ApiResponse> call,
 *                                     Response&lt;ApiResponse> response) {
 *                                 // TODO insert new records into database
 *                                 helperCallback.recordSuccess();
 *                             }
 *
 *                             {@literal @}Override
 *                             public void onFailure(Call&lt;ApiResponse> call, Throwable t) {
 *                                 helperCallback.recordFailure(t);
 *                             }
 *                         }));
 *     }
 *
 *     {@literal @}Override
 *     public void onItemAtEndLoaded({@literal @}NonNull MyItem itemAtEnd) {
 *         helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER,
 *                 helperCallback -> api.getTopBefore(itemAtEnd.getName(), 10).enqueue(
 *                         new Callback&lt;ApiResponse>() {
 *                             {@literal @}Override
 *                             public void onResponse(Call&lt;ApiResponse> call,
 *                                     Response&lt;ApiResponse> response) {
 *                                 // TODO insert new records into database
 *                                 helperCallback.recordSuccess();
 *                             }
 *
 *                             {@literal @}Override
 *                             public void onFailure(Call&lt;ApiResponse> call, Throwable t) {
 *                                 helperCallback.recordFailure(t);
 *                             }
 *                         }));
 *     }
 * }
 * </pre>
 * <p>
 * The helper provides an API to observe combined request status, which can be reported back to the
 * application based on your business rules.
 * <pre>
 * MutableLiveData&lt;PagingRequestHelper.Status> combined = new MutableLiveData&lt;>();
 * helper.addListener(status -> {
 *     // merge multiple states per request type into one, or dispatch separately depending on
 *     // your application logic.
 *     if (status.hasRunning()) {
 *         combined.postValue(PagingRequestHelper.Status.RUNNING);
 *     } else if (status.hasError()) {
 *         // can also obtain the error via {@link StatusReport#getErrorFor(RequestType)}
 *         combined.postValue(PagingRequestHelper.Status.FAILED);
 *     } else {
 *         combined.postValue(PagingRequestHelper.Status.SUCCESS);
 *     }
 * });
 * </pre>
 */
// THIS class is likely to be moved into the library in a future release. Feel free to copy it
// from this sample.
/**
 * Creates a PagingRequestHelper with the given {@link Executor} which is used to run
 * retry actions.
 *
 * @param retryService The {@link Executor} that can run the retry actions.
 */
class PagingRequestHelper(private val retryService: Executor) {
    private val lock = Object()

    @GuardedBy("lock")
    private val requestQueues = arrayOf(
        RequestQueue(RequestType.INITIAL),
        RequestQueue(RequestType.BEFORE),
        RequestQueue(RequestType.AFTER)
    )

    val listeners: CopyOnWriteArrayList<Listener> = CopyOnWriteArrayList()

    /**
     * Adds a new listener that will be notified when any request changes {@link Status state}.
     *
     * @param listener The listener that will be notified each time a request's status changes.
     * @return True if it is added, false otherwise (e.g. it already exists in the list).
     */
    @AnyThread
    fun addListener(listener: Listener) = listeners.add(listener)

    /**
     * Removes the given listener from the listeners list.
     *
     * @param listener The listener that will be removed.
     * @return True if the listener is removed, false otherwise (e.g. it never existed)
     */
    fun removeListener(listener: Listener) = listeners.remove(listener)

    /**
     * Runs the given {@link Request} if no other requests in the given request type is already
     * running.
     * <p>
     * If run, the request will be run in the current thread.
     *
     * @param type    The type of the request.
     * @param request The request to run.
     * @return True if the request is run, false otherwise.
     */
    @AnyThread
    fun runIfNotRunning(type: RequestType, request: Request): Boolean {
        val hasListeners = listeners.isNotEmpty()
        val report: StatusReport? = null
        synchronized(lock) {
            val queue = requestQueues[type.ordinal]
            if (queue.running != null) {
                return false
            }
            queue.running = request
            queue.status = Status.RUNNING
            queue.failed = null
            queue.lastError = null
            if (hasListeners) {

            }

        }
        if (report != null) {

        }
        val wrapper = RequestWrapper(request, this, type)
        wrapper.run()
        return true
    }

    @FunctionalInterface
    interface Request {
        /**
         * Should run the request and call the given {@link Callback} with the result of the
         * request.
         *
         * @param callback The callback that should be invoked with the result.
         */
        fun run(callback: Callback)

        class Callback(
            private val wrapper: RequestWrapper,
            private val helper: PagingRequestHelper
        ) {
            private val called = AtomicBoolean()

            /**
             * Call this method when the request succeeds and new data is fetched.
             */
            fun recordSuccess() {
                if (called.compareAndSet(false, true)) {

                } else {
                    throw IllegalStateException("Already called recordSuccess or recordFailure")
                }
            }

            /**
             * Call this method with the failure message and the request can be retried via
             * {@link #retryAllFailed()}.
             *
             * @param throwable The error that occured while carrying out the request.
             */
            fun recordFailure(@NonNull throwable: Throwable) {
                if (called.compareAndSet(false, true)) {

                } else {
                    throw IllegalStateException("Already called recordSuccess or recordFailure")
                }
            }
        }
    }

    /**
     * Listener interface to get notified by request status changes.
     */
    interface Listener {
        /**
         * Called when the status for any of the requests has changed.
         *
         * @param report The current status report that has all the information about the requests.
         */
        fun onStatusChange(report: StatusReport)
    }

    enum class Status {
        /**
         * There is current a running request.
         */
        RUNNING,

        /**
         * The last request has succeeded or no such requests have ever been run.
         */
        SUCCESS,

        /**
         * The last request has failed.
         */
        FAILED
    }

    enum class RequestType {
        /**
         * Corresponds to an initial request made to a [DataSource] or the empty state for
         * a [BoundaryCallback][androidx.paging.PagedList.BoundaryCallback].
         */
        INITIAL,

        /**
         * Corresponds to the `loadBefore` calls in [DataSource] or
         * `onItemAtFrontLoaded` in
         * [BoundaryCallback][androidx.paging.PagedList.BoundaryCallback].
         */
        BEFORE,

        /**
         * Corresponds to the `loadAfter` calls in [DataSource] or
         * `onItemAtEndLoaded` in
         * [BoundaryCallback][androidx.paging.PagedList.BoundaryCallback].
         */
        AFTER
    }

    class RequestQueue(requestType: RequestType) {
        var failed: RequestWrapper? = null
        var running: Request? = null
        var lastError: Throwable? = null
        var status: Status = Status.SUCCESS
    }

    companion object {
        /**
         * Data class that holds the information about the current status of the ongoing requests
         * using this helper.
         */
        class StatusReport(
            /**
             * Status of the latest request that were submitted with {@link RequestType#INITIAL}.
             */
            private val initial: Status,
            /**
             * Status of the latest request that were submitted with {@link RequestType#BEFORE}.
             */
            private val before: Status,
            /**
             * Status of the latest request that were submitted with {@link RequestType#AFTER}.
             */
            private val after: Status,
            private val errors: Array<Throwable>
        ) {
            /**
             * Convenience method to check if there are any running requests.
             *
             * @return True if there are any running requests, false otherwise.
             */
            fun hasRunning(): Boolean =
                initial == Status.RUNNING ||
                        before == Status.RUNNING ||
                        after == Status.RUNNING

            /**
             * Convenience method to check if there are any requests that resulted in an error.
             *
             * @return True if there are any requests that finished with error, false otherwise.
             */
            fun getErrorFor(@NonNull type: RequestType): Throwable? = errors[type.ordinal]

            override fun toString(): String =
                "StatusReport{" +
                        "initial = $initial, " +
                        "before = $before, " +
                        "after = $after, " +
                        "errors = $errors" +
                        "}"

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || javaClass != other.javaClass) return false
                val that = other as StatusReport
                if (that.initial != initial) return false
                if (that.before != before) return false
                if (that.after != after) return false
                // Probably incorrect - comparing Object[] arrays with Arrays.equals
                return that.errors.contentEquals(errors)
            }

            override fun hashCode(): Int {
                var result = initial.hashCode()
                result = 31 * result + before.hashCode()
                result = 31 * result + after.hashCode()
                result = 31 * result + errors.contentHashCode()
                return result
            }
        }

        class RequestWrapper(
            private val request: Request,
            private val helper: PagingRequestHelper,
            private val type: RequestType
        ) : Runnable {
            override fun run() = request.run(Request.Callback(this, helper = helper))

            fun retry(service: Executor) = service.execute()
        }
    }
}

