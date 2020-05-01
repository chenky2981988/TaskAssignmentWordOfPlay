package com.chirag.taskassinment.data

import androidx.lifecycle.MutableLiveData
import com.chirag.taskassinment.data.model.StatusItem
import com.chirag.taskassinment.data.model.StatusItemList


/**
 * Created by Chirag Sidhiwala on 1/5/20.
 */
class StatusRepository {

    private var listOfStatusItem = MutableLiveData<List<StatusItem>>()

    init {
        listOfStatusItem = MutableLiveData()
    }

    fun getStatusList(): MutableLiveData<List<StatusItem>> {
        listOfStatusItem.value = StatusItemList.listOfStatusItem() // Can be network call here
        return listOfStatusItem
    }
}