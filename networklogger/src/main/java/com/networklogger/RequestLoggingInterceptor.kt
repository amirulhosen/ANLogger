package com.networklogger

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date


class RequestLoggingInterceptor(val context: Context) : Interceptor {
    private lateinit var jsonObject1: JSONObject
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val jsonObject = JSONObject().apply {
            put("url", request.url);
            put("request_header", request.headers)
            put("request_body", stringifyRequestBody(request))
            put("request_method", request.method)
        }

        var response = chain.proceed(request)
        val responseBody = response.body
        if(responseBody != null )
        {
            val responseBodyString = responseBody.string()
            response = response.newBuilder()
                .body(
                    ResponseBody.create(
                        responseBody.contentType(),
                        responseBodyString.toByteArray()
                    )
                )
                .build()

            val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
            val requestFormatted: String = dateFormat.format(response.sentRequestAtMillis)
            val responseFormatted: String = dateFormat.format(response.receivedResponseAtMillis)

            jsonObject.put("response_body", responseBodyString)
            jsonObject.put("status_code", response.code)
            jsonObject.put("request_time", requestFormatted)
            jsonObject.put("response_time", responseFormatted)
            jsonObject.put("protocol", response.protocol)
            jsonObject.put("is_ssl", response.request.isHttps)
            jsonObject.put("resquest_size", request.body?.contentLength())
            jsonObject.put("response_size", response.body?.contentLength())
            jsonObject.put("tls_version", response.handshake?.tlsVersion)
            jsonObject.put("cipher_suite", response.handshake?.cipherSuite)
            jsonObject.put("cipher_suite", response.handshake?.cipherSuite)
        }
        jsonObject.put("response_header", response.headers)

        val file = File((context.getExternalFilesDir("")?.absolutePath ?: "") + "/network_logs")
        val fileWriter = if (file.exists()) {
            val data = loadDataFromFile()
            val updatedJsonArray = data.getJSONArray("logs")
            updatedJsonArray.put(jsonObject)

            jsonObject1 = JSONObject()
            jsonObject1.put("logs", updatedJsonArray)

            FileWriter(file)
        } else {
            val jsonArray = JSONArray()
            jsonArray.put(jsonObject)
            jsonObject1 = JSONObject()
            jsonObject1.put("logs", jsonArray)
            FileWriter(file)
        }

        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.write(jsonObject1.toString())
        bufferedWriter.close()

        return response
    }

    private fun stringifyRequestBody(request: Request): String? {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "did not work"
        }
    }

    private fun loadDataFromFile(): JSONObject {
        val file = File((context.getExternalFilesDir("")?.absolutePath ?: "") + "/network_logs")
        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)
        val stringBuilder = StringBuilder()
        var line = bufferedReader.readLine()
        while (line != null) {
            stringBuilder.append(line).append("\n")
            line = bufferedReader.readLine()
        }
        bufferedReader.close()
        return JSONObject(stringBuilder.toString())
    }
}