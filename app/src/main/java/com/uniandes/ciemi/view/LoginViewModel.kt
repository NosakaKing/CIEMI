package com.uniandes.ciemi.view
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
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
        val url = "http://10.0.2.2:5002/api/v1/Account/authenticateByEmail"
        val datos = JSONObject()
        datos.put("email", email.value)
        datos.put("password", password.value)
        val rq = Volley.newRequestQueue(context)
        val js = JsonObjectRequest(
            Request.Method.POST, url, datos,
            { s->
                try {
                    val obj = (s)
                    if(obj.getBoolean("succeeded")) {
                        var array=obj.getJSONArray("data")
                        var data = array.getJSONObject(0)
                        message.value = obj.getString("message")

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
