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
    httpClient.addInterceptor(MockInterceptor(TaskAssignment.instance))
    return httpClient;
}


/*
try {
    retrofitHttp = new Retrofit.Builder()
        .baseUrl("http://mockapi")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient().build())
        .build();
} catch (Exception e) {
    e.printStackTrace();
}
}
return retrofitHttp.create(ApiInterface.class);
}

*/
/**
 * @return
 *//*

private static OkHttpClient.Builder httpClient() {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    // set your desired log level
    logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    // add your other interceptors …
    // add logging as last interceptor
    httpClient.addInterceptor(logging);
    httpClient.connectTimeout(5, TimeUnit.MINUTES);
    httpClient.writeTimeout(5, TimeUnit.MINUTES);
    httpClient.readTimeout(5, TimeUnit.MINUTES);
    httpClient.addInterceptor(new FakeInterceptor(App.getInstance()));
    return httpClient;
}*/
