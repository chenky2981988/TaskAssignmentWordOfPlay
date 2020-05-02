package com.chirag.taskassinment.data.mockservice

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.chirag.taskassinment.utility.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okio.Buffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * Created by Chirag Sidhiwala on 28/4/20.
 */
class MockInterceptor(private val context: Context) : Interceptor {
    private var mContentType = "application/json"

    companion object {
        val TAG = MockInterceptor::class.java.simpleName
    }

    fun setContentType(contentType: String): MockInterceptor {
        this.mContentType = contentType
        return this
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        lateinit var response: Response

        val responseFileName: String = getFileName(chain)

        if (!TextUtils.isEmpty(responseFileName)) {

            try {
                val assetFileName = responseFileName.plus(FILE_EXTENSION)
                val inputStream = context.assets.open(assetFileName)
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val responseStringBuilder = StringBuilder()
                val text: List<String> = bufferedReader.readLines()
                for (line in text) {
                    responseStringBuilder.append(line)
                }

                val builder = Response.Builder()
                builder.request(chain.request())
                builder.protocol(Protocol.HTTP_1_0)
                builder.addHeader(CONTENT_TYPE, mContentType)
                builder.body(
                    ResponseBody.create(
                        MediaType.parse(mContentType),
                        responseStringBuilder.substring(0)
                    )
                )
                when (responseFileName) {
                    SUCCESS_RESPONSE_FILE -> builder.code(200)
                    INVALID_CREDENTIALS_FILE -> builder.code(401)
                    else -> builder.code(400)
                }

                builder.message(responseStringBuilder.toString())
                response = builder.build()
            } catch (exception: IOException) {
                Log.e(TAG, exception.message, exception);
            }
        } else {
//            for (file in listSuggestionFileName) {
//                Log.e(TAG, "File not exist: " + getFilePath(uri, file))
//            }
            response = chain.proceed(chain.request())
        }
        return response;
    }

    private fun getFileName(chain: Interceptor.Chain): String {
        val method = chain.request().method()
        return if (method == POST_KEY) {
            var fileName = ""
            val lastSegment =
                chain.request().url().pathSegments()[chain.request().url().pathSegments().size - 1]

            val requestBody = bodyToString(chain.request().body())
            val jsonObject =
                Gson().fromJson(requestBody, JsonObject::class.java)

            fileName = getResponseFileName(lastSegment, jsonObject)
            fileName
        } else "none"
    }

    private fun bodyToString(request: RequestBody?): String? {
        return try {
            val buffer = Buffer()
            if (request != null) request.writeTo(buffer) else return ""
            buffer.readUtf8()
        } catch (e: IOException) {
            "did not work"
        }
    }

    private fun getResponseFileName(lastSegment: String, requestBodyJson: JsonObject): String {
        return if (lastSegment == LOGIN_KEY && requestBodyJson.has(USERNAME_KEY) && requestBodyJson.has(
                PASSWORD_KEY
            )
        ) {
            val userName = requestBodyJson.get(USERNAME_KEY).asString
            val password = requestBodyJson.get(PASSWORD_KEY).asString
            return if (userName == TEST_USERNAME && password == TEST_PASSWORD)
                SUCCESS_RESPONSE_FILE
            else
                INVALID_CREDENTIALS_FILE
        } else
            BAD_REQUEST_FILE
    }
}