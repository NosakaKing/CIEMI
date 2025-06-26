package com.uniandes.ciemi.view
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.uniandes.ciemi.utils.Constants
import org.json.JSONException
import org.json.JSONObject


class LoginViewModel : ViewModel() {
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    private fun validate(): Boolean {
        if (email.value.isBlank()) {
            message.value = "El email está vacío"
            return false
        }
        if (password.value.isBlank()) {
            message.value = "La contraseña está vacía"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            message.value = "El email no es válido"
            return false
        }

        return true
    }

    fun login(context: Context) {
        if (!validate()) {
            return
        }
        val url = "${Constants.BASE_URL}/Account/authenticateByEmail"
        val datos = JSONObject()
        datos.put("email", email.value)
        datos.put("password", password.value)
        val rq = Volley.newRequestQueue(context)
        val js = JsonObjectRequest(
            Request.Method.POST, url, datos,
            { s->
                try {
                    val obj = JSONObject(s.toString())
                    if(obj.getBoolean("succeeded")) {
                        message.value = "Autentificación exitosa"
                        val data = obj.getJSONObject("data")
                        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("user_data", data.toString())
                        editor.apply()
                    }else {
                        message.value = obj.getString("message")

                    }

                }catch (e: JSONException) {
                    message.value = e.message

                }
            }, {volleyError-> message.value = volleyError.message})
        rq.add(js)
    }

    fun clearMessage() {
        message.value = null
    }
}
