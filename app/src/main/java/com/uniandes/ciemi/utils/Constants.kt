package com.uniandes.ciemi.utils

import android.content.Context
import com.uniandes.ciemi.model.SelectBusiness
import org.json.JSONObject

object Constants {
    const val BASE_URL = "http://10.0.2.2:5002/api/v1"

    fun getToken(context: Context): String {
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userDataString =
            sharedPref.getString("user_data", null) ?: throw Exception("Token no encontrado")
        val userData = JSONObject(userDataString)
        return userData.getString("jwToken")
    }

    fun getAuthHeaders(context: Context): MutableMap<String, String> {
        val token = getToken(context)
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $token"
        headers["Content-Type"] = "application/json"
        return headers
    }
    fun getUserData(context: Context): JSONObject {
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userDataString =
            sharedPref.getString("user_data", null) ?: throw Exception("Datos de usuario no encontrados")
        return JSONObject(userDataString)
    }

    fun getUserId(context: Context): String {
        return getUserData(context).getString("id")
    }

    fun getUserRole(context: Context): String {
        val userData = getUserData(context)
        val rolesArray = userData.optJSONArray("roles")
        return rolesArray?.optString(0) ?: "Sin rol"
    }

    fun getUserName(context: Context): String {
        return getUserData(context).getString("userName")
    }

    fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }


}
