package com.chirag.taskassinment.data.model

import com.google.gson.JsonElement


/**
 * Created by Chirag Sidhiwala on 28/4/20.
 */
data class ResponseData(val data: JsonElement, val status: String?, val message: String?): ResponseStatus(status, message)