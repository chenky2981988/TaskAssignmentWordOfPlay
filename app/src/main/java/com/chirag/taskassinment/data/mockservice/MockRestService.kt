package com.chirag.taskassinment.data.mockservice

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


/**
 * Created by Chirag Sidhiwala on 28/4/20.
 */
interface MockRestService {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )

    @POST("login")
    fun login(@Body body: HashMap<String, String>): Call<JsonObject>
}