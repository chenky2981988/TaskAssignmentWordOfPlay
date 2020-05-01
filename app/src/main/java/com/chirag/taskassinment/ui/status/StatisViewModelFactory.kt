package com.chirag.taskassinment.ui.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chirag.taskassinment.data.StatusRepository


/**
 * Created by Chirag Sidhiwala on 1/5/20.
 */

class StatusViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatusActivityViewModel::class.java)) {
            return StatusActivityViewModel(
                statusRepository = StatusRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}