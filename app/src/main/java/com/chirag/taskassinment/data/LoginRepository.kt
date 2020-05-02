package com.chirag.taskassinment.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.chirag.taskassinment.R
import com.chirag.taskassinment.TaskAssignment
import com.chirag.taskassinment.data.mockservice.MockRestClient
import com.chirag.taskassinment.data.model.ErrorResponse
import com.chirag.taskassinment.data.model.Result
import com.chirag.taskassinment.data.model.Token
import com.chirag.taskassinment.utility.PASSWORD_KEY
import com.chirag.taskassinment.utility.USERNAME_KEY
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository {

    private var loginMutableLiveData: MutableLiveData<Result<Any>> = MutableLiveData()

    fun login(username: String, password: String): MutableLiveData<Result<Any>> {
        try {
            val requestBody = getLoginRequestBody(username, password)
            MockRestClient.getMockRestService().login(requestBody)
                .enqueue(object : retrofit2.Callback<Token> {
                    override fun onResponse(
                        call: Call<Token>,
                        response: Response<Token>
                    ) {
                        if (response.code() == 200) {
                            loginMutableLiveData.postValue(Result.Success(response.body() as Token))
                        } else {
                            val errorResponse = Gson().fromJson(
                                response.errorBody()!!.string(),
                                ErrorResponse::class.java
                            )
                            loginMutableLiveData.postValue(Result.Error(errorResponse))
                        }
                    }

                    override fun onFailure(call: Call<Token>, t: Throwable) {
                        loginMutableLiveData.postValue(Result.Failure(Exception(t.message)))
                    }
                })

        } catch (e: Throwable) {
            e.printStackTrace()
            loginMutableLiveData.value = Result.Failure(IOException("Error logging in", e))
        }

        return loginMutableLiveData
    }

    private fun getLoginRequestBody(username: String, password: String): HashMap<String, String> {
        val postBody = HashMap<String, String>()
        postBody[USERNAME_KEY] = username
        postBody[PASSWORD_KEY] = password
        return postBody
    }
}
