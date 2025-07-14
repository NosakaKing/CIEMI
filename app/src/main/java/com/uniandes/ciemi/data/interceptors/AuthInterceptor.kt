package com.uniandes.ciemi.data.interceptors
import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context
import com.uniandes.ciemi.utils.Constants

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Obtenemos headers desde Constants
        val headers = Constants.getAuthHeaders(context)

        // Agregamos todos los headers al request
        for ((key, value) in headers) {
            requestBuilder.addHeader(key, value)
        }

        return chain.proceed(requestBuilder.build())
    }
}
