package com.chirag.taskassinment.ui.status

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.chirag.taskassinment.TaskAssignment
import com.chirag.taskassinment.data.StatusRepository
import com.chirag.taskassinment.data.model.StatusItem
import com.danikula.videocache.CacheListener
import java.io.File


/**
 * Created by Chirag Sidhiwala on 30/4/20.
 */
class StatusActivityViewModel(private val statusRepository: StatusRepository) : ViewModel(),
    CacheListener {

    companion object {
        private val TAG = StatusActivityViewModel::class.java.simpleName
    }

    fun getStatusList(): LiveData<List<StatusItem>> {
        return statusRepository.getStatusList()
    }

    fun getProxyVideoUrl(url: String): String {
        val httpProxyCacheServer = TaskAssignment.httpProxyCacheServer
        httpProxyCacheServer.registerCacheListener(this, url)
        val proxyUrl = httpProxyCacheServer.getProxyUrl(url)
        Log.d(TAG, "getProxy URL : $proxyUrl")
        return proxyUrl
    }

    override fun onCacheAvailable(cacheFile: File?, url: String?, percentsAvailable: Int) {
        Log.d(TAG, "cached File Name ${cacheFile?.name}")
        Log.d(TAG, "cached File URL $url")
        Log.d(TAG, "cached File percentsAvailable $percentsAvailable")
    }

}