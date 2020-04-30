package com.chirag.taskassinment.custom_components

import android.view.View
import com.chirag.taskassinment.custom_components.views.CustomConstraintView


/**
 * Created by Chirag Sidhiwala on 30/4/20.
 */
interface StatusCallback {
    fun done()
    fun onNextCalled(view: View, customConstraintView: CustomConstraintView, index: Int)
}