package com.logger.networklogger.api

import android.content.Context
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class FileHelper {
    private lateinit var jsonObject1: JSONObject
    internal fun saveToFile(context: Context, request: Request, response: Response): Response {
        val jsonObject = JSONObject().apply {
            put("url", request.url)
            put("request_header", request.headers)
            put("request_body", stringifyRequestBody(request))
            put("request_method", request.method)
        }
        val responseBody = response.body
        var updatedResponse: Response = response
        if (responseBody != null) {
            val responseBodyString = responseBody.string()
            updatedResponse = response.newBuilder()
                .body(
                    ResponseBody.create(
                        responseBody.contentType(),
                        responseBodyString.toByteArray()
                    )
                )
                .build()

            val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
            val requestFormatted: String = dateFormat.format(response.sentRequestAtMillis)
            val responseFormatted: String =
                dateFormat.format(response.receivedResponseAtMillis)
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
            jsonObject.put("response_header", response.headers)
        }
        if (!responseBody?.contentType().toString().startsWith("image/")) {
            showNotification(
                context,
                (response.code.toString() + " " + request.url),
                request.method
            )
            val file =
                File(
                    (context.getExternalFilesDir("")?.absolutePath ?: "") + "/network_logs"
                )
            val fileWriter = if (file.exists()) {
                val data = loadDataFromFile(context)
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
                file.setLastModified(Calendar.getInstance().timeInMillis)
                FileWriter(file)
            }

            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(jsonObject1.toString())
            bufferedWriter.close()
        }
        return updatedResponse
    }
}