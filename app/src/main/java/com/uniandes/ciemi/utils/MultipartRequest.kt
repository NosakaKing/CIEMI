package com.uniandes.ciemi.utils

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter

class MultipartRequest(
    method: Int,
    url: String,
    private val headers: MutableMap<String, String>,
    private val params: MutableMap<String, String>,
    private val responseListener: Response.Listener<String>,
    private val errorListener: Response.ErrorListener
) : Request<String>(method, url, errorListener) {

    private val boundary = "apiclient-" + System.currentTimeMillis()
    private val mimeType = "multipart/form-data;boundary=$boundary"

    override fun getBodyContentType(): String = mimeType

    override fun getHeaders(): MutableMap<String, String> = headers

    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        val writer = PrintWriter(OutputStreamWriter(bos, "UTF-8"), true)

        for ((key, value) in params) {
            writer.append("--$boundary").append("\r\n")
            writer.append("Content-Disposition: form-data; name=\"$key\"")
                .append("\r\n\r\n")
            writer.append(value).append("\r\n")
        }

        writer.append("--$boundary--").append("\r\n")
        writer.flush()
        return bos.toByteArray()
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
        return try {
            val responseStr = String(response?.data ?: byteArrayOf(), charset("UTF-8"))
            Response.success(responseStr, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: String) {
        responseListener.onResponse(response)
    }
}
