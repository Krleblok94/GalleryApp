package com.example.galleryapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class NetworkBoundResource<ResultType, RequestType>(private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        // Initially set the status to loading without the data
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data -> // Here we can use "it" instead of data and newData, but that could be confusing
            // We remove the source here since the initial state of the data has been loaded, so we don't need to monitor
            // for emissions till we check if we need to fetch the new data from the network
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource) // Fetch from the network
            } else {
                // No need to fetch, just re-attach the initially loaded source since it's still fresh and monitor for changes
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        // Re-attach the last known results while we wait for the network to come back with the response
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }

        // In both cases (onResponse and onFailed) remove the source from the result since we will update it
        // based on the response
        createCall()?.enqueue(object : Callback<RequestType> {
            override fun onResponse(call: Call<RequestType>, response: Response<RequestType>) {
                result.removeSource(dbSource)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null || response.code() == 204) {
                        // The response is empty, so reload from disk whatever we had
                        appExecutors.mainThread().execute {
                            result.addSource(dbSource) { newData ->
                                setValue(Resource.success(newData))
                            }
                        }
                    } else {
                        // The response was a success, save the results in the database
                        appExecutors.diskIO().execute {
                            saveCallResult(body)
                            appExecutors.mainThread().execute {
                                result.addSource(loadFromDb()) { newData ->
                                    setValue(Resource.success(newData))
                                }
                            }
                        }
                    }
                } else {
                    // The response was not successful, get the error message
                    val msg = response.errorBody()?.string()
                    val errorMsg = if (msg.isNullOrEmpty()) response.message() else msg
                    handleFailedNetworkResponse(dbSource, errorMsg ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<RequestType>, t: Throwable) {
                result.removeSource(dbSource)
                handleFailedNetworkResponse(dbSource, t.message)
            }
        })
    }

    private fun handleFailedNetworkResponse(dbSource: LiveData<ResultType>, message: String?) {
        onFetchFailed()
        // If the fetch failed for whatever reason, just re-attach the last results from db and show an error message
        // to the user if available
        result.addSource(dbSource) { newData ->
            setValue(Resource.error(message, newData))
        }
    }

    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    // Methods to override when implementing this class
    protected open fun onFetchFailed() {}
    protected abstract fun saveCallResult(item: RequestType)
    protected abstract fun shouldFetch(data: ResultType?): Boolean
    protected abstract fun loadFromDb(): LiveData<ResultType>
    // We set the return type to nullable, since there will be some instances where we will always want to load from the DB,
    // so this way in the overridden methods we can just return null instead of implementing a network call which will never
    // be called
    protected abstract fun createCall(): Call<RequestType>?
}