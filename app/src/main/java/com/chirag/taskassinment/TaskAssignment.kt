package com.chirag.taskassinment

import android.app.Application
import com.chirag.taskassinment.utility.getVideoCacheDir
import com.danikula.videocache.HttpProxyCacheServer


/**
 * Created by Chirag Sidhiwala on 28/4/20.
 */
class TaskAssignment : Application() {

    companion object {
        lateinit var instance: TaskAssignment
            private set
        lateinit var httpProxyCacheServer: HttpProxyCacheServer
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        httpProxyCacheServer = newProxy()
    }

    private fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer.Builder(this)
            .cacheDirectory(getVideoCacheDir(this))
            .build()
    }
}