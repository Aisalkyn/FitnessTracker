package neobis.fitnesstest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class MapsActivity : AppCompatActivity(),
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private  var mMap: GoogleMap? = null
    private val LOCATION_PERMISSION = 100
    private var mLocationRequest: LocationRequest? = null
    private var builder: LatLngBounds.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        checkLocationPermission()
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onConnected(p0: Bundle?) {
        redirect()
    }

    private fun redirect(){
        val mLastLocation = onLocation()
        Log.i("______________", "${mLastLocation?.latitude} ${mLastLocation?.longitude}")// LocationServices.FusedLocationApi.getLastLocation( mGoogleApiClient)
        if (mLastLocation != null) {
            moveCameraDirection()
            Toast.makeText(this, "lat"+ mLastLocation.latitude + "long"+mLastLocation.longitude , Toast.LENGTH_LONG).show()
        }
    }

    private fun onLocation() : LatLng? {
        if(checkLocationPermission()) {
            // instantiate the location manager, note you will need to request permissions in your manifest
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // get the last know location from your location manager.
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            // now get the lat/lon from the location and do something with it.
            return LatLng(location.latitude, location.longitude)

        }
        return null
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    private fun checkLocationPermission(): Boolean {
        mLocationRequest = LocationRequest()
        mLocationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
            return false
        }
        mMap?.isMyLocationEnabled = true
        return true
    }

    private fun moveCameraDirection() {
        if (mMap != null && builder != null) {
            try {
                val updatePosition = CameraUpdateFactory.newLatLngBounds(builder?.build(), 500, 500, 5)
                mMap?.animateCamera(updatePosition)
            } catch (e: Exception) {
                e.printStackTrace()
            }


        } else if (builder == null && mMap != null) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(42.88200, 74.58274), 10f))
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            LOCATION_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission request was granted, we set up the marker
                    redirect()
                } else {
                    // The permission request was denied, we make the user aware of why the location is not shown
                    Toast.makeText(this, "Since the permission wasn't granted we can't show the location", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}
