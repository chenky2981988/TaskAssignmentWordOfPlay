package com.chirag.taskassinment.data

import android.util.Log
import android.widget.Toast
import com.chirag.taskassinment.data.mockservice.MockRestClient
import com.chirag.taskassinment.data.model.LoggedInUser
import com.chirag.taskassinment.data.model.Token
import com.google.gson.JsonObject
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response
import java.io.IOException


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val postBody = HashMap<String, String>()
            postBody["username"] = username
            postBody["password"] = password
            MockRestClient.getMockRestService().login(postBody).enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.d("TAG","Response : ${response.message()}")
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("TAG", t.message.toString())
                }
            })

            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            e.printStackTrace()
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }


}

