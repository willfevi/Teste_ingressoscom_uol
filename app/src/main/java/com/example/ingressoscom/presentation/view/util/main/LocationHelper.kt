package com.example.ingressoscom.presentation.view.util.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location as AndroidLocation
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import timber.log.Timber
import java.util.Locale

class LocationHelper(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 100
    }

    fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    fun fetchUserLocation(
        onSuccess: (AndroidLocation) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Timber.d("Solicitando última localização conhecida...")
        fusedLocationClient.lastLocation.addOnSuccessListener { location: AndroidLocation? ->
            location?.let {
                Timber.d("Localização obtida com sucesso.")
                onSuccess(it)
            } ?: run {
                Timber.w("Localização não disponível.")
                onFailure(Exception("Localização não disponível."))
            }
        }.addOnFailureListener { exception ->
            Timber.e(exception, "Erro ao obter a localização.")
            onFailure(exception)
        }
    }

    fun getAddressFromLocation(location: AndroidLocation): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val street = address.thoroughfare ?: ""
                val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: ""
                Timber.d("Endereço obtido: $street, $city")
                "$street, $city"
            } else {
                Timber.w("Nenhum endereço encontrado para a localização fornecida.")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Erro ao obter o endereço.")
            null
        }
    }
}
