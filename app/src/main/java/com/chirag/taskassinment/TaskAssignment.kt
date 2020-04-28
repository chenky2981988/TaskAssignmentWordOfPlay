package com.chirag.taskassinment

import android.app.Application


/**
 * Created by Chirag Sidhiwala on 28/4/20.
 */
class TaskAssignment: Application() {
    companion object {
        lateinit var instance: TaskAssignment
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}