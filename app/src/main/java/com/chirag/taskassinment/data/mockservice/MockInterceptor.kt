package com.chirag.taskassinment.data.mockservice

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okio.Buffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI


/**
 * Created by Chirag Sidhiwala on 28/4/20.
 */
class MockInterceptor(private val context: Context) : Interceptor {
    private var mContentType = "application/json"


    companion object {
        val FILE_EXTENSION = ".json"
        val TAG = MockInterceptor::class.java.simpleName
    }

    fun setContentType(contentType: String): MockInterceptor {
        this.mContentType = contentType
        return this
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val listSuggestionFileName = ArrayList<String>()
        val method: String = chain.request().method().toLowerCase()

        lateinit var response: Response
        val uri: URI = chain.request().url().uri()
        Log.d(TAG, "--> Request url: [" + method.toUpperCase() + "]" + uri.toString());

        val defaultFileName: String = getFileName(chain)

        //create file name with http method
        //eg: getLogin.json
        listSuggestionFileName.add(method.plus(toUppperCaseFirstLetter(defaultFileName)))

        //eg: login.json
        listSuggestionFileName.add(defaultFileName);

        if (!TextUtils.isEmpty(defaultFileName)) {
            Log.d(TAG, "Read data from file: $defaultFileName")

            try {
                val inputStream = context.assets.open(defaultFileName)
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val responseStringBuilder = StringBuilder()
                val text: List<String> = bufferedReader.readLines()
                for (line in text) {
                    responseStringBuilder.append(line)
                }
                Log.d(TAG, "Response: $responseStringBuilder")
                val builder = Response.Builder()
                builder.request(chain.request())
                builder.protocol(Protocol.HTTP_1_0)
                builder.addHeader("content-type", mContentType)
                builder.body(
                    ResponseBody.create(
                        MediaType.parse(mContentType),
                        responseStringBuilder.substring(0)
                    )
                )
                when (defaultFileName) {
                    "success_response.json" -> builder.code(200)
                    "invalid_credentials_response.json" -> builder.code(401)
                    else -> builder.code(400)
                }

                builder.message(responseStringBuilder.toString())
                response = builder.build()
            } catch (exception: IOException) {
                Log.e(TAG, exception.message, exception);
            }
        } else {
            for (file in listSuggestionFileName) {
                Log.e(TAG, "File not exist: " + getFilePath(uri, file))
            }
            response = chain.proceed(chain.request())
        }
        Log.d(TAG, "<-- END [$method.toUpperCase() ] $uri.toString()");
        return response;
    }

    private fun getFileName(chain: Interceptor.Chain): String {
        val method = chain.request().method()
        if (method.equals("POST")) {
            var fileName: String = ""
            val lastSegment = chain.request().url().pathSegments()
                .get(chain.request().url().pathSegments().size - 1)

            val requestBody = bodyToString(chain.request().body())
            Log.d(TAG, "Request Body : $requestBody")
            val jsonObject: JsonObject =
                Gson().fromJson(requestBody, JsonObject::class.java)
            fileName =
                if (lastSegment == "login" && jsonObject.has("username") && jsonObject.has("password")) {
                    val userName = jsonObject.get("username").asString
                    val password = jsonObject.get("password").asString
                    if (userName == "test@worldofplay.in" && password == "Worldofplay@2020")
                        "success_response"
                    else
                        "invalid_credentials_response"
                } else
                    "bad_request_response"

            /*  if (lastSegment.equals("login")) {
                  fileName = "success_response"
              } else {
                  fileName = lastSegment
              }*/
            Log.d(TAG, "FileName is $fileName$FILE_EXTENSION")
            return "$fileName$FILE_EXTENSION"
        }
        return "none$FILE_EXTENSION";
    }

    private fun getFilePath(uri: URI, fileName: String): String {
        var path = ""
        if (uri.path.lastIndexOf('/') != uri.path.length - 1) {
            path = uri.path.substring(0, uri.path.lastIndexOf('/') + 1)
        } else {
            path = uri.path
        }

        return "$uri.host$path$fileName"
    }

    private fun toUppperCaseFirstLetter(str: String): String {
        return "$str.substring(0, 1).toUpperCase()$str.substring(1)"
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
}