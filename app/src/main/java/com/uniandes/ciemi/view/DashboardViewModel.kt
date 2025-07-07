package com.uniandes.ciemi.view

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.uniandes.ciemi.data.repository.BusinessRepository
import com.uniandes.ciemi.model.SelectBusiness
import com.uniandes.ciemi.utils.Constants

class DashboardViewModel : ViewModel() {

    var userName = mutableStateOf("")
    var role = mutableStateOf("")
    val currentSection = mutableStateOf(DashboardSection.HOME)
    val negocios = mutableStateListOf<SelectBusiness>()
    val negocioSeleccionado = mutableStateOf<SelectBusiness?>(null)

    fun loadUserData(context: Context) {
        userName.value = Constants.getUserName(context)
        role.value = Constants.getUserRole(context)
    }

    fun loadNegocios(context: Context) {
        BusinessRepository.loadBusiness(
            context = context,
            onSuccess = { lista ->
                negocios.clear()
                negocios.addAll(lista)

                if (role.value.contains("Vendedor", ignoreCase = true) && lista.isNotEmpty()) {
                    negocioSeleccionado.value = lista[0]
                }

                if (!role.value.contains("Vendedor", ignoreCase = true) && lista.isNotEmpty() && negocioSeleccionado.value == null) {
                    negocioSeleccionado.value = lista[0]
                }
            },
            onError = {
                println("Error cargando negocios: $it")
            }
        )
    }

    fun setNegocio(negocio: SelectBusiness) {
        negocioSeleccionado.value = negocio
    }
}
