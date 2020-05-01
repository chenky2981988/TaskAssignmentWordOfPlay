package com.chirag.taskassinment.data.mockservice

import com.chirag.taskassinment.TaskAssignment
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by Chirag Sidhiwala on 28/4/20.
 */
object MockRestClient {
    private val TAG = MockRestClient::class.java.simpleName

    private fun getRetrofit() : Retrofit {
           val retrofit = Retrofit.Builder()
                .baseUrl("http://mockapi")
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient().build())
                .build()
        return retrofit
    }

    fun getMockRestService(): MockRestService {
        return getRetrofit().create(MockRestService::class.java)
    }

}

private fun getHttpClient(): OkHttpClient.Builder {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.HEADERS
    logging.level = HttpLoggingInterceptor.Level.BODY
    val httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor(logging)
    httpClient.connectTimeout(5, TimeUnit.MINUTES)
    httpClient.writeTimeout(5, TimeUnit.MINUTES);
    httpClient.readTimeout(5, TimeUnit.MINUTES);
    httpClient.addInterceptor(MockInterceptor(TaskAssignment.instance.applicationContext))
    return httpClient;
}
