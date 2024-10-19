package com.example.ingressoscom.presentation.view.util.main

import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import timber.log.Timber

class PermissionHandler(
    private val activity: Activity,
    private val functionsAux: FunctionsAux
) {

    fun handlePermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LocationHelper.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.d("Permissão de localização concedida pelo usuário.")
                functionsAux.fetchAndDisplayAddress()
            } else {
                Toast.makeText(activity, "Permissão de localização negada.", Toast.LENGTH_SHORT).show()
                Timber.d("Permissão de localização negada pelo usuário.")
            }
        }
    }
}
