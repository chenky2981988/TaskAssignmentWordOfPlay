package com.chirag.taskassinment.custom_components


/**
 * Created by Chirag Sidhiwala on 30/4/20.
 */
interface ProgressWatcher {
    fun onProgressEnd(indexEnded: Int)
}