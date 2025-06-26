package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class DashboardViewModel : ViewModel() {
    var userName = mutableStateOf("")
    var role = mutableStateOf("")
    val currentSection = mutableStateOf(DashboardSection.HOME)

    fun loadUserData(context: Context) {
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userDataString = sharedPref.getString("user_data", null)

        if (userDataString != null) {
            val userData = JSONObject(userDataString)
            userName.value = userData.optString("userName", "Usuario")
            role.value = userData.optJSONArray("roles")?.optString(0) ?: "Sin rol"
        }
    }
}
