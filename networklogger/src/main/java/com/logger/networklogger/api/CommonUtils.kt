package com.logger.networklogger.api

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.logger.networklogger.data.entity.ApiDataModelEntity
import com.logger.networklogger.ui.list.NetworkLoggerActivity
import com.networklogger.R
import okhttp3.Request
import okio.Buffer
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException


internal fun suppressResponseIfneeded(
    request: Request,
    apiDataModelEntity: ApiDataModelEntity
): ApiDataModelEntity {
    val updatedJson: MutableMap<String, Any?> = mutableMapOf()
    val dat = Gson().getAdapter(object : TypeToken<Map<String, Any?>>() {})
    val matchedSuppressedKeyValye =
        SuppressApi.suppressApi.filter { it.first == request.url.pathSegments.last() }
    if (matchedSuppressedKeyValye.isNotEmpty()) {
        JSONObject().apply {
            dat.fromJson(stringifyRequestBody(request)).forEach { (t, u) ->
                if (matchedSuppressedKeyValye.find { it.second == t } == null) {
                    updatedJson[t] = u
                } else {
                    updatedJson[t] = u.toString().substring(0..5) + "******"
                }
            }
        }
        val gson = Gson().toJson(updatedJson)
        try {
            apiDataModelEntity.request_body =
                JSONObject(gson.toString()).getString("nameValuePairs").toString()
        } catch (e: Exception) {
            apiDataModelEntity.request_body = gson.toString()
        }
    } else
        apiDataModelEntity.request_body = stringifyRequestBody(request)
    return apiDataModelEntity
}

internal fun stringifyRequestBody(request: Request): String? {
    return try {
        val copy = request.newBuilder().build()
        val buffer = Buffer()
        copy.body?.writeTo(buffer)
        buffer.readUtf8()
    } catch (e: IOException) {
        "did not work"
    }
}


internal fun loadDataFromFile(context: Context): JSONObject {
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

internal fun showNotification(context: Context, url: String, method: String) {

    val channelId = "12345"
    val description = "logger Notification"
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Set pending intent
    val intent = Intent(context, NetworkLoggerActivity::class.java)
    val contentIntent =
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    // Channel settings
    val notificationChannel =
        NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_LOW)
    notificationManager.createNotificationChannels(listOf(notificationChannel))

    val builder = NotificationCompat.Builder(context, channelId)
        .setContentTitle(method)
        .setLocalOnly(true)
        .setContentText(url)
        .setAutoCancel(true)
        .setContentIntent(contentIntent)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
    notificationManager.notify(0, builder.build())
}