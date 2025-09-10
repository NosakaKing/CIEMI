package com.uniandes.ciemi.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import java.io.File
import java.io.FileOutputStream

object PdfUtils {

    fun downloadAndOpenPDF(
        context: Context,
        url: String,
        fileName: String,
        headers: Map<String, String> = emptyMap()
    ) {
        val rq = Volley.newRequestQueue(context)

        val pdfRequest = object : Request<ByteArray>(
            Method.GET,
            url,
            Response.ErrorListener { error ->
                Toast.makeText(context, "Error al descargar PDF: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return headers.toMutableMap()
            }

            override fun parseNetworkResponse(response: com.android.volley.NetworkResponse): Response<ByteArray> {
                return Response.success(response.data, null)
            }

            override fun deliverResponse(response: ByteArray) {
                try {
                    val file = File(context.getExternalFilesDir(null), fileName)
                    FileOutputStream(file).use { it.write(response) }

                    val uri = FileProvider.getUriForFile(
                        context,
                        context.packageName + ".provider",
                        file
                    )

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al abrir PDF", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }

        rq.add(pdfRequest)
    }
}
