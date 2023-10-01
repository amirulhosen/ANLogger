package com.lib.networklogger.remote.di

import android.content.Context
import com.google.gson.GsonBuilder
import com.lib.networklogger.remote.LibServiceApi
import com.networklogger.RequestLoggingInterceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieHandler
import java.util.concurrent.TimeUnit

val serviceModule = module {
    // The Retrofit service using custom HTTP client instance as a singleton
    single { createHttpClient(get()) }
    single {
        createWebService<LibServiceApi>(
            okHttpClient = get(),
            baseUrl = "https://dummyjson.com"
        )
    }
}

/* Returns a custom OkHttpClient instance with interceptor. Used for building Retrofit service */
fun createHttpClient(context:Context): OkHttpClient {
    if (CookieHandler.getDefault() == null) CookieHandler.setDefault(java.net.CookieManager())

    val client = OkHttpClient.Builder()
    client.addInterceptor(ConnectivityInterceptor(context))
    client.addInterceptor(RequestLoggingInterceptor(context))
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    client.apply {
        connectTimeout(60, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
        cookieJar(JavaNetCookieJar(CookieHandler.getDefault()))
    }
    return client.build()
}

/* function to build our Retrofit service */
inline fun <reified T> createWebService(
    okHttpClient: OkHttpClient,
    baseUrl: String
): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .client(okHttpClient)
        .build()
    return retrofit.create(T::class.java)
}
