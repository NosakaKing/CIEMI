package com.uniandes.ciemi.data.api

import android.content.Context
import com.uniandes.ciemi.data.interceptors.AuthInterceptor
import com.uniandes.ciemi.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var retrofit: Retrofit? = null

    fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl("${Constants.BASE_URL}/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getProductoApi(context: Context): ProductoApi {
        return getRetrofit(context).create(ProductoApi::class.java)
    }
}
