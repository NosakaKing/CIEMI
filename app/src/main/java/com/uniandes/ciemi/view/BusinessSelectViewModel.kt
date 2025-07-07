package com.uniandes.ciemi.view

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.uniandes.ciemi.model.SelectBusiness

class BusinessSelectViewModel : ViewModel() {

    var negocioSeleccionado = mutableStateOf<SelectBusiness?>(null)
        private set

    fun setNegocio(negocio: SelectBusiness) {
        negocioSeleccionado.value = negocio
    }
}
