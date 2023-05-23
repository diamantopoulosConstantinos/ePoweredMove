package com.kosdiam.epoweredmove.views.menu

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.helpers.CheckPermission
import com.kosdiam.epoweredmove.helpers.ClusteredPoi
import com.kosdiam.epoweredmove.helpers.PoiClusterRenderer
import com.kosdiam.epoweredmove.helpers.SensorStatus
import com.kosdiam.epoweredmove.models.Poi
import com.kosdiam.epoweredmove.models.enums.MessageType
import com.kosdiam.epoweredmove.services.PoiService
import com.kosdiam.epoweredmove.views.poi.PoiActivity
import retrofit2.Call
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var gMap: GoogleMap
    private lateinit var currentLocationBtn: ImageView
    private lateinit var searchView : SearchView
    private lateinit var searchLocationLayout : LinearLayout

    private lateinit var parentActivity: MenuActivity

    val athensPosition: LatLng = LatLng(37.988124, 23.726494)

    var markerMap: MutableMap<ClusteredPoi, Poi> = mutableMapOf()
    private lateinit var clusterManager: ClusterManager<ClusteredPoi>

    private lateinit var locationManager: LocationManager
    private var locationListener: LocationListener? = null

    private lateinit var mSharedPreferences: SharedPreferences
    private var PREF_SELECTED_VEHICLE: String? = null
    private var currentLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        searchView = view.findViewById(R.id.search_view)
        currentLocationBtn = view.findViewById(R.id.current_location)
        searchLocationLayout = view.findViewById(R.id.search_location_layout)

        if(activity!=null){
            parentActivity = activity as MenuActivity
        }

        //hide keyboard when activity starts
        parentActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        PREF_SELECTED_VEHICLE= getString(R.string.shared_pref_selected_vehicle)
        mSharedPreferences = parentActivity.getSharedPreferences(getString(R.string.app_name_shared_prefs), Context.MODE_PRIVATE)

        //set map
        mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = parentActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        searchView.isSubmitButtonEnabled = true

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchLocation(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        currentLocationBtn.setOnClickListener {
            if(currentLocation == null){
                searchCurrentLocation()
            }
            else{
                zoomAtCoordinates(currentLocation!!)
            }
        }

        //reset search view
        searchView.setQuery("", false)
        searchLocationLayout.bringToFront()

        return view
    }

    @SuppressLint("MissingPermission")
    private fun searchCurrentLocation() {
        val checkPermission = CheckPermission()
        if(checkPermission.hasGPSPermission(parentActivity)){
            gMap.isMyLocationEnabled = true
            val sensorStatus = SensorStatus()
            if(sensorStatus.isGPSOnline(parentActivity)){
                locationListener = object : LocationListener {
                    override fun onLocationChanged(currentLocation: Location) {
                        this@MapFragment.currentLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
                        //locationManager.removeUpdates(locationListener!!)
                    }

                    override fun onLocationChanged(locations: MutableList<Location>) {
                        this@MapFragment.currentLocation = LatLng(locations[0].latitude, locations[0].longitude)
                        //locationManager.removeUpdates(locationListener!!)
                    }

                    override fun onProviderDisabled(provider: String) {
                        parentActivity.showMessage(MessageType.GPS_NOT_ENABLED)
                        //locationManager.removeUpdates(locationListener!!)
                    }

                    override fun onProviderEnabled(provider: String) {}

                    override fun onFlushComplete(requestCode: Int) {}

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, locationListener!!)
            }
            else{
                parentActivity.showMessage(MessageType.GPS_NOT_ENABLED)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            resources.getInteger(R.integer.gps_permission_code) -> {
                if(resultCode == Activity.RESULT_OK){
                    searchCurrentLocation()
                }
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        zoomToGreece()
        clusterManager = ClusterManager<ClusteredPoi>(parentActivity, gMap)
        gMap.setOnCameraIdleListener(clusterManager)
        gMap.setOnMarkerClickListener(clusterManager)
        searchCurrentLocation()
        clusterManager.setOnClusterItemClickListener {
            val poi = markerMap[it]
            if(poi != null){
                goToPoiActivity(poi)
            }
            true
        }
        setAllPois()
    }

    private fun goToPoiActivity(poi: Poi) {
        val intent = Intent(parentActivity, PoiActivity::class.java)
        intent.putExtra(parentActivity.getString(R.string.poi_extra), poi)
        startActivity(intent)
    }

    private fun zoomAtCoordinates(coordinates: LatLng){
        if(this::gMap.isInitialized){
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 14.5f))
        }
    }

    private fun searchLocation(locationInput: String?){
        if(locationInput.isNullOrEmpty()){
            parentActivity.showMessage(MessageType.NO_LOCATION_FOUND)
            return
        }
        val locationFound = getLocationFromText(locationInput)
        if(locationFound == null){
            parentActivity.showMessage(MessageType.NO_LOCATION_FOUND)
            return
        }
        zoomAtCoordinates(locationFound)
        searchView.setQuery("", false)
    }
    private fun getLocationFromText(locationInput: String): LatLng? {
        try{
            val geocoder = Geocoder(parentActivity)
            val addresses = geocoder.getFromLocationName(locationInput,1)
            if(addresses!=null && addresses.isNotEmpty()) {
                return LatLng(addresses[0].latitude, addresses[0].longitude)
            }
            else{
                parentActivity.showMessage(MessageType.NO_LOCATION_FOUND)
            }
        }
        catch (e: Exception) {
            parentActivity.showMessage(MessageType.GENERAL_ERROR)
        }
        return null
    }

    private fun addMarker(poi: Poi){
        //remove previous marker if already exists
        val existingClusteredPoi = clusterManager.algorithm.items.find { existingClusteredPoi -> existingClusteredPoi.getPoiId() == poi.id }
        if(existingClusteredPoi != null){
            clusterManager.algorithm.removeItem(existingClusteredPoi)
        }

        val clusteredPoi = ClusteredPoi(
            LatLng(poi.latitude.toDouble(), poi.longitude.toDouble()),
            "", "", poi.availableSelectedVehicle, poi.id
        )

        clusterManager.addItem(clusteredPoi)
        clusterManager.renderer = PoiClusterRenderer(parentActivity, gMap, clusterManager)
        markerMap[clusteredPoi] = poi
    }



    private fun zoomToGreece(){
        if(this::gMap.isInitialized) {
            val currentZoom = if (gMap.cameraPosition.zoom == 6f) {
                6.1f
            } else {
                6f
            }
            val cameraPosition =
                CameraPosition.builder().target(athensPosition).zoom(currentZoom)
                    .build()
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun setAllPois(){
        parentActivity.setProgressBarVisibility(View.VISIBLE)

        var selectedVehicleId = mSharedPreferences.getString(PREF_SELECTED_VEHICLE, null)
        if(parentActivity.getFirebaseAuth().currentUser == null){
            selectedVehicleId = null
        }

        val poiService = PoiService()
        val call = poiService.poiService.getPoisWithPlugAvailability(selectedVehicleId)
        call.enqueue(object : retrofit2.Callback<List<Poi>> {
            override fun onResponse(call: Call<List<Poi>>, response: Response<List<Poi>>) {
                if(response.body() != null && response.body()!!.isNotEmpty()){
                    gMap.clear()
                    clusterManager.clearItems()
                    response.body()!!.forEach { poi -> addMarker(poi) }
                    clusterManager.cluster()
                }
                parentActivity.setProgressBarVisibility(View.GONE)
            }

            override fun onFailure(call: Call<List<Poi>>, t: Throwable) {
                parentActivity.showMessage(MessageType.ERROR_FETCHING_POIS)
            }
        })
    }
}