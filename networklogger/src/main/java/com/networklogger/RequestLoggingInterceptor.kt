package com.networklogger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar


class RequestLoggingInterceptor(val context: Context, val useSqlite:Boolean) : Interceptor {
    private lateinit var jsonObject1: JSONObject
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val overriddenBaseUrl = prefs.getString(
            "endPoints",
            null
        )
        return if (!request.url.pathSegments.contains(overriddenBaseUrl)) {
            if(!useSqlite) {
                val jsonObject = JSONObject().apply {
                    put("url", request.url)
                    put("request_header", request.headers)
                    put("request_body", stringifyRequestBody(request))
                    put("request_method", request.method)
                }


                var response = chain.proceed(request)
                val responseBody = response.body
                if (responseBody != null) {
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
                    jsonObject.put("cipher_suite", response.handshake?.cipherSuite)
                }
                jsonObject.put("response_header", response.headers)
                showNotification(context, (response.code.toString() + request.url), request.method)
                val file =
                    File((context.getExternalFilesDir("")?.absolutePath ?: "") + "/network_logs")
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
                    file.setLastModified(Calendar.getInstance().timeInMillis)
                    FileWriter(file)
                }

                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.write(jsonObject1.toString())
                bufferedWriter.close()
                response
            }
            else{

                val db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "networkLogger"

                ).build()
                val jsonObject = JSONObject().apply {
                    put("url", request.url)
                    put("request_header", request.headers)
                    put("request_body", stringifyRequestBody(request))
                    put("request_method", request.method)
                }


                var response = chain.proceed(request)
                val responseBody = response.body
                if (responseBody != null) {
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
                    jsonObject.put("cipher_suite", response.handshake?.cipherSuite)
                }
                jsonObject.put("response_header", response.headers)
                showNotification(context, (response.code.toString() + request.url), request.method)

                db.personDao()?.insertAll(Gson().fromJson(jsonObject.toString(),ApiDataModelEntity::class.java))
                response
            }
        } else
            chain.proceed(request)
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

    private fun showNotification(context: Context, url: String, method: String) {

        val channelId = "12345"
        val description = "logger Notification"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Set pending intent
        val intent = Intent(context, NetworkLoggerActivity::class.java)
        val contentIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Channel settings
        val notificationChannel =
            NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.enableVibration(true)
        notificationManager.createNotificationChannel(notificationChannel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(method)
            .setContentText(url)
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources, R.drawable
                        .ic_launcher_background
                )
            )
        notificationManager.notify(12345, builder.build())
    }
}