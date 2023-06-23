/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package mx.edu.utxj.ddi.tarea8_200527.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import mx.edu.utxj.ddi.tarea8_200527.R
import mx.edu.utxj.ddi.tarea8_200527.presentation.theme.Tarea8_200527Theme

class MainActivity : ComponentActivity() {

    private lateinit var latitude: TextView
    private lateinit var longitude: TextView
    private lateinit var handler: Handler
    private lateinit var updateTimeRunnable: Runnable
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val latitudes = findViewById<TextView>(R.id.latitudes)
                    val longitudes = findViewById<TextView>(R.id.longitudes)

                    latitudes.text = "Latitud: $latitude"
                    longitudes.text = "Longitud: $longitude"
                }
            }
        }

        mHandler = Handler()
        mRunnable = Runnable { // Actualiza cada segundo
        }
}




    override fun onResume() {
        super.onResume();
        startLocationUpdates();
        mRunnable?.let { mHandler?.post(it) }
    }

    override fun onPause() {
        super.onPause();
        stopLocationUpdates();
        mRunnable?.let { mHandler?.removeCallbacks(it) }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest: LocationRequest = LocationRequest.create()
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest.setInterval(5000) // Intervalo en milisegundos para obtener actualizaciones de ubicación
            locationCallback?.let {
                fusedLocationClient!!.requestLocationUpdates(locationRequest,
                    it, null)
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient!!.removeLocationUpdates(it) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }
}