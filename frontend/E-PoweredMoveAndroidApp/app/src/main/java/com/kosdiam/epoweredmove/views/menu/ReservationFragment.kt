package com.kosdiam.epoweredmove.views.menu

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.adapters.ReservationAdapter
import com.kosdiam.epoweredmove.helpers.CheckPermission
import com.kosdiam.epoweredmove.helpers.SensorStatus
import com.kosdiam.epoweredmove.models.Poi
import com.kosdiam.epoweredmove.models.Reservation
import com.kosdiam.epoweredmove.models.RouteInfo
import com.kosdiam.epoweredmove.models.enums.MessageType
import com.kosdiam.epoweredmove.models.enums.VehicleType
import com.kosdiam.epoweredmove.services.PoiService
import com.kosdiam.epoweredmove.services.ReservationService
import com.kosdiam.epoweredmove.services.VehicleService
import com.kosdiam.epoweredmove.views.poi.PoiActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class ReservationFragment : Fragment() {
    private lateinit var reservationsRecyclerView: RecyclerView
    private lateinit var findClosestChargingStationLayout: RelativeLayout
    private lateinit var findingClosestChargingStationLayout: RelativeLayout
    private lateinit var cancelFindingLocation: ImageView

    private lateinit var parentActivity: MenuActivity

    private lateinit var mSharedPreferences: SharedPreferences
    private var PREF_FIREBASE_TOKEN: String? = null
    private var PREF_SELECTED_VEHICLE: String? = null

    private var reservations = mutableListOf<Reservation>()

    private lateinit var locationManager: LocationManager
    private var locationListener: LocationListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_reservation, container, false)
        reservationsRecyclerView = view.findViewById(R.id.recycler_view)
        findClosestChargingStationLayout = view.findViewById(R.id.find_closest_charging_station)
        findingClosestChargingStationLayout = view.findViewById(R.id.finding_closest_charging_station)
        cancelFindingLocation = view.findViewById(R.id.finding_closest_charging_station_cancel)

        if(activity!=null){
            parentActivity = activity as MenuActivity
        }

        PREF_FIREBASE_TOKEN= getString(R.string.shared_pref_firebase_token)
        PREF_SELECTED_VEHICLE= getString(R.string.shared_pref_selected_vehicle)
        mSharedPreferences = parentActivity.getSharedPreferences(getString(R.string.app_name_shared_prefs), Context.MODE_PRIVATE)

        locationManager = parentActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        findClosestChargingStationLayout.setOnClickListener {
            findClosestPoi()
        }

        cancelFindingLocation.setOnClickListener {
            cancelFindingLocation()
        }

        fetchReservations()

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            resources.getInteger(R.integer.gps_permission_code) -> {
                if(resultCode == Activity.RESULT_OK){
                    val sensorStatus = SensorStatus()
                    if(sensorStatus.isGPSOnline(parentActivity)){
                        findClosestPoi()
                    }
                    else{
                        parentActivity.showMessage(MessageType.GPS_NOT_ENABLED)
                    }
                }
            }
        }
    }

    private fun cancelFindingLocation(){
        if(locationListener != null){
            locationManager.removeUpdates(locationListener!!)
            locationListener = null
        }
        findingClosestChargingStationLayout.visibility = View.GONE
        findClosestChargingStationLayout.visibility = View.VISIBLE
    }

    private fun findClosestPoi(){
        if(mSharedPreferences.getString(PREF_SELECTED_VEHICLE, null).isNullOrEmpty()){
            parentActivity.showMessage(MessageType.ERROR_NO_VEHICLE_SELECTED)
            return
        }

        val selectedVehicleId = mSharedPreferences.getString(PREF_SELECTED_VEHICLE, null)!!

        val checkPermission = CheckPermission()
        if(checkPermission.hasGPSPermission(parentActivity)){
            val sensorStatus = SensorStatus()
            if(sensorStatus.isGPSOnline(parentActivity)){
                val dialog = Dialog(parentActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.popup_select_battery_state)
                dialog.window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                val popupCurrentBatteryPercentage = dialog.findViewById<EditText>(R.id.current_battery_state)
                val popupSubmitBatteryState = dialog.findViewById<Button>(R.id.submit_battery_state)

                popupSubmitBatteryState.setOnClickListener {
                    popupCurrentBatteryPercentage.error = null
                    if(TextUtils.isEmpty(popupCurrentBatteryPercentage.text)){
                        popupCurrentBatteryPercentage.error = parentActivity.getString(R.string.not_valid_battery)
                    }
                    else{
                        startCurrentLocationSensor(selectedVehicleId, popupCurrentBatteryPercentage.text.toString().toInt())
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }
            else{
                parentActivity.showMessage(MessageType.GPS_NOT_ENABLED)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCurrentLocationSensor(selectedVehicleId: String, currentBatteryPercentage: Int, reservation: Reservation? = null) {
        findClosestChargingStationLayout.visibility = View.GONE
        findingClosestChargingStationLayout.visibility = View.VISIBLE

        locationListener = object : LocationListener {
            override fun onLocationChanged(currentLocation: Location) {
                if(reservation == null){
                    searchForPoi(LatLng(currentLocation.latitude, currentLocation.longitude), selectedVehicleId, currentBatteryPercentage)
                }else{
                    showSelectedPoiInfo(LatLng(currentLocation.latitude, currentLocation.longitude), currentBatteryPercentage, reservation)
                }
                cancelFindingLocation()
            }

            override fun onLocationChanged(locations: MutableList<Location>) {
                if(reservation == null){
                    searchForPoi(LatLng(locations[0].latitude, locations[0].longitude), selectedVehicleId, currentBatteryPercentage)
                }else{
                    showSelectedPoiInfo(LatLng(locations[0].latitude, locations[0].longitude), currentBatteryPercentage, reservation)
                }
                cancelFindingLocation()
            }

            override fun onProviderDisabled(provider: String) {
                cancelFindingLocation()
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onFlushComplete(requestCode: Int) {}

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, locationListener!!)
    }

    private fun showSelectedPoiInfo(latLng: LatLng, currentBatteryPercentage: Int, reservation: Reservation) {
        parentActivity.setProgressBarVisibility(View.VISIBLE)
        val reservationService = ReservationService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
        val call = reservationService.reservationService.getRouteInfoByReservation(reservation.id, currentBatteryPercentage, latLng.latitude.toFloat(), latLng.longitude.toFloat())
        call.enqueue(object : retrofit2.Callback<RouteInfo> {
            override fun onResponse(call: Call<RouteInfo>, response: Response<RouteInfo>) {
                if(response.body() != null){
                    parentActivity.setProgressBarVisibility(View.GONE)
                    val dialog = AlertDialog.Builder(parentActivity, R.style.AlertDialogCustom)
                    dialog.setPositiveButton(parentActivity.getString(R.string.ok)) { di, _ ->
                        di.dismiss()
                    }
                    val alert = dialog.create()
                    val typedValue = TypedValue()
                    parentActivity.theme.resolveAttribute(R.attr.dialog_color, typedValue, true)
                    alert.setTitle(parentActivity.getString(R.string.route_info_title))
                    val message = StringBuilder()
                    message.append(parentActivity.getString(R.string.route_info_message_1)); message.append(" ")
                    message.append(response.body()!!.destinationMeters.toString()); message.append(" ")
                    message.append(parentActivity.getString(R.string.route_info_message_2)); message.append(" ")
                    message.append(response.body()!!.metersByVehicle.toString()); message.append(" ")
                    message.append(parentActivity.getString(R.string.route_info_message_3)); message.append(" ")
                    message.append(response.body()!!.metersByFoot.toString()); message.append(" ")
                    message.append(parentActivity.getString(R.string.route_info_message_4))
                    alert.setMessage(message.toString())
                    alert.show()
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(parentActivity, R.color.blue))
                }
                else{
                    parentActivity.showMessage(MessageType.NO_CHARGING_STATIONS_FOUND)
                }
            }

            override fun onFailure(call: Call<RouteInfo>, t: Throwable) {
                parentActivity.showMessage(MessageType.ERROR_SEARCHING_CHARGING_STATION)
            }

        })
    }

    private fun searchForPoi(latLng: LatLng, selectedVehicleId: String, currentBatteryPercentage: Int) {
        parentActivity.setProgressBarVisibility(View.VISIBLE)
        val poiService = PoiService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
        val call = poiService.poiService.getPOIByLocation(latLng.latitude, latLng.longitude, selectedVehicleId)
        call.enqueue(object : retrofit2.Callback<Poi> {
            override fun onResponse(call: Call<Poi>, response: Response<Poi>) {
                if(response.body() != null){
                    parentActivity.setProgressBarVisibility(View.GONE)
                    goToPoiActivity(selectedVehicleId, latLng, response.body()!!, currentBatteryPercentage)
                }
                else{
                    parentActivity.showMessage(MessageType.NO_CHARGING_STATIONS_FOUND)
                }
            }

            override fun onFailure(call: Call<Poi>, t: Throwable) {
                parentActivity.showMessage(MessageType.ERROR_SEARCHING_CHARGING_STATION)
            }

        })
    }

    private fun goToPoiActivity(selectedVehicleId: String, currentLocation: LatLng, poi: Poi, currentBatteryPercentage: Int) {
        val vehicleService = VehicleService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
        val call = vehicleService.vehicleService.getRouteInfoByVehicle(
            selectedVehicleId, currentBatteryPercentage, currentLocation.latitude.toFloat(),
            currentLocation.longitude.toFloat(), poi.latitude, poi.longitude)
        call.enqueue(object : retrofit2.Callback<RouteInfo> {
            override fun onResponse(call: Call<RouteInfo>, response: Response<RouteInfo>) {
                if(response.body() != null){
                    val dialog = AlertDialog.Builder(parentActivity, R.style.AlertDialogCustom)
                    dialog.setPositiveButton(parentActivity.getString(R.string.yes)) { di, _ ->
                        di.dismiss()
                        val intent = Intent(parentActivity, PoiActivity::class.java)
                        intent.putExtra(parentActivity.getString(R.string.poi_extra), poi)
                        intent.putExtra(parentActivity.getString(R.string.reservation_source_extra), true)
                        startActivity(intent)
                    }
                    dialog.setNegativeButton(parentActivity.getString(R.string.no)) { di, _ ->
                        di.dismiss()
                    }
                    val alert = dialog.create()
                    val typedValue = TypedValue()
                    parentActivity.theme.resolveAttribute(R.attr.dialog_color, typedValue, true)
                    alert.setTitle(parentActivity.getString(R.string.route_info_title))
                    val message = StringBuilder()
                    message.append(parentActivity.getString(R.string.route_info_message_1)); message.append(" ")
                    message.append(response.body()!!.destinationMeters.toString()); message.append(" ")
                    message.append(parentActivity.getString(R.string.route_info_message_2)); message.append(" ")
                    message.append(response.body()!!.metersByVehicle.toString()); message.append(" ")
                    message.append(parentActivity.getString(R.string.route_info_message_3)); message.append(" ")
                    message.append(response.body()!!.metersByFoot.toString()); message.append(" ")
                    message.append(parentActivity.getString(R.string.route_info_message_4)); message.append(" ")
                    message.append(parentActivity.getString(R.string.route_info_question))
                    alert.setMessage(message.toString())
                    alert.show()
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(parentActivity, R.color.red))
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(parentActivity, R.color.green))
                }
                else{
                    parentActivity.showMessage(MessageType.ERROR_FETCHING_CLOSEST_ROUTE_INFO)
                }
            }

            override fun onFailure(call: Call<RouteInfo>, t: Throwable) {
                parentActivity.showMessage(MessageType.ERROR_FETCHING_CLOSEST_ROUTE_INFO)
            }

        })
    }

    private fun fetchReservations() {
        parentActivity.setProgressBarVisibility(View.VISIBLE)
        val reservationService = ReservationService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
        val call = reservationService.reservationService.getReservationsByOwner(parentActivity.getFirebaseAuth().currentUser!!.uid)
        call.enqueue(object : retrofit2.Callback<List<Reservation>> {
            override fun onResponse(call: Call<List<Reservation>>, response: Response<List<Reservation>>) {
                if(response.body() != null){
                    reservations = response.body()!!.toMutableList()
                    setReservations()
                }
                else{
                    parentActivity.showMessage(MessageType.ERROR_FETCHING_RESERVATIONS)
                }
            }

            override fun onFailure(call: Call<List<Reservation>>, t: Throwable) {
                parentActivity.showMessage(MessageType.ERROR_FETCHING_RESERVATIONS)
            }

        })
    }

    private fun setReservations() {
        reservationsRecyclerView.layoutManager = LinearLayoutManager(parentActivity)
        val reservationsAdapter = ReservationAdapter(reservations, parentActivity)
        reservationsRecyclerView.adapter = reservationsAdapter

        parentActivity.setProgressBarVisibility(View.GONE)
        reservationsAdapter.setOnItemClickListener(object : ReservationAdapter.OnItemClickListener{
            override fun onItemViewClick(position: Int) {
                if(reservations.isNotEmpty() && reservations.lastIndex >= position){
                    showReservation(reservations[position])
                }
            }

            override fun onItemCancelClick(position: Int) {
                if(reservations.isNotEmpty() && reservations.lastIndex >= position){
                    cancelReservation(reservations[position])
                }
            }

            override fun onItemRouteInfoClick(position: Int) {
                if(reservations.isNotEmpty() && reservations.lastIndex >= position){
                    getRouteInfo(reservations[position])
                }
            }

            override fun onItemMapClick(position: Int) {
                if(reservations.isNotEmpty() && reservations.lastIndex >= position){
                    launchGoogleMaps(reservations[position])
                }
            }
        })
    }

    private fun getRouteInfo(reservation: Reservation) {
        if(mSharedPreferences.getString(PREF_SELECTED_VEHICLE, null).isNullOrEmpty()){
            parentActivity.showMessage(MessageType.ERROR_NO_VEHICLE_SELECTED)
            return
        }

        val selectedVehicleId = mSharedPreferences.getString(PREF_SELECTED_VEHICLE, null)!!

        val checkPermission = CheckPermission()
        if(checkPermission.hasGPSPermission(parentActivity)){
            val sensorStatus = SensorStatus()
            if(sensorStatus.isGPSOnline(parentActivity)){
                val dialog = Dialog(parentActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.popup_select_battery_state)
                dialog.window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                val popupCurrentBatteryPercentage = dialog.findViewById<EditText>(R.id.current_battery_state)
                val popupSubmitBatteryState = dialog.findViewById<Button>(R.id.submit_battery_state)

                popupSubmitBatteryState.setOnClickListener {
                    popupCurrentBatteryPercentage.error = null
                    if(TextUtils.isEmpty(popupCurrentBatteryPercentage.text)){
                        popupCurrentBatteryPercentage.error = parentActivity.getString(R.string.not_valid_battery)
                    }
                    else{
                        startCurrentLocationSensor(selectedVehicleId, popupCurrentBatteryPercentage.text.toString().toInt(), reservation)
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }
            else{
                parentActivity.showMessage(MessageType.GPS_NOT_ENABLED)
            }
        }
    }

    private fun launchGoogleMaps(reservation: Reservation) {
        //https://stackoverflow.com/a/53428137
        //d for driving, w for walking, b for bicycling
        val vehicleType: String = when(reservation.vehicleObj.vehicleType){
            VehicleType.CAR -> "d"
            VehicleType.MOTORBIKE -> "d"
            VehicleType.BICYCLE -> "b"
            VehicleType.SCOOTER -> "b"
            VehicleType.OTHER -> "d"
        }
        val gmmIntentUri = Uri.parse("google.navigation:q="+reservation.poiLatitude+","+reservation.poiLongitude + "&mode=" + vehicleType)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun cancelReservation(reservation: Reservation) {
        val dialog = AlertDialog.Builder(parentActivity, R.style.AlertDialogCustom)
        dialog.setPositiveButton(parentActivity.getString(R.string.yes)) { di, _ ->
            di.dismiss()
            parentActivity.setProgressBarVisibility(View.VISIBLE)
            val reservationIndex = reservations.indexOf(reservation)
            reservation.cancelled = true
            val reservationService = ReservationService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
            val call = reservationService.reservationService.updateReservation(reservation)
            call.enqueue(object : retrofit2.Callback<Reservation> {
                override fun onResponse(call: Call<Reservation>, response: Response<Reservation>) {
                    if(response.body() != null){
                        reservations[reservationIndex] = response.body()!!
                        reservationsRecyclerView.adapter!!.notifyItemChanged(reservationIndex)
                        parentActivity.setProgressBarVisibility(View.GONE)
                    }
                    else{
                        parentActivity.showMessage(MessageType.ERROR_CANCELLING_RESERVATION)
                    }
                }

                override fun onFailure(call: Call<Reservation>, t: Throwable) {
                    parentActivity.showMessage(MessageType.ERROR_CANCELLING_RESERVATION)
                }

            })

        }
        dialog.setNegativeButton(parentActivity.getString(R.string.no)) { di, _ ->
            di.dismiss()
        }
        val alert = dialog.create()
        val typedValue = TypedValue()
        parentActivity.theme.resolveAttribute(R.attr.dialog_color, typedValue, true)
        alert.setTitle(parentActivity.getString(R.string.cancel_reservation_title))
        alert.setMessage(parentActivity.getString(R.string.cancel_reservation_question))
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(parentActivity, R.color.red))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(parentActivity, R.color.green))
    }

    @SuppressLint("SimpleDateFormat")
    private fun showReservation(reservation: Reservation) {
        val dialog = Dialog(parentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_show_reservation)
        dialog.window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val popupAccepted= dialog.findViewById<TextView>(R.id.viewed_reservation_accepted)
        val popupCancelled = dialog.findViewById<TextView>(R.id.viewed_reservation_cancelled)
        val popupDate = dialog.findViewById<TextView>(R.id.viewed_reservation_date)
        val popupTimeStart = dialog.findViewById<TextView>(R.id.viewed_reservation_time_start)
        val popupTimeEnd = dialog.findViewById<TextView>(R.id.viewed_reservation_time_end)
        val popupVehicle = dialog.findViewById<TextView>(R.id.viewed_reservation_vehicle)
        val popupSubmitDate = dialog.findViewById<TextView>(R.id.viewed_reservation_submit_date)
        val popupPlugConnector = dialog.findViewById<TextView>(R.id.viewed_reservation_plug_connector)
        val popupPlugCurrent = dialog.findViewById<TextView>(R.id.viewed_reservation_plug_current)
        val popupPlugTypeLevel = dialog.findViewById<TextView>(R.id.viewed_reservation_plug_type_level)
        val popupPlugCompatibility = dialog.findViewById<TextView>(R.id.viewed_reservation_plug_compatibility)
        val popupPlugTesla = dialog.findViewById<TextView>(R.id.viewed_reservation_plug_tesla)
        val popupPlugImage = dialog.findViewById<ImageView>(R.id.viewed_reservation_plug_image)

        val sdfDate = SimpleDateFormat("dd/MM/yyyy")
        val sdfTime = SimpleDateFormat("HH:mm")
        popupAccepted.text = if(reservation.accepted) "Yes" else "No"
        popupCancelled.text = if(reservation.cancelled) "Yes" else "No"
        popupDate.text = sdfDate.format(reservation.timeStart)
        popupTimeStart.text = sdfTime.format(reservation.timeStart)
        popupTimeEnd.text = sdfTime.format(reservation.timeEnd)
        val vehicleDetails = reservation.vehicleObj.brand + " " + reservation.vehicleObj.model
        popupVehicle.text = vehicleDetails
        popupSubmitDate.text = sdfDate.format(reservation.timestamp)
        popupPlugConnector.text = reservation.plugObj.plugTypeObj.connector
        popupPlugCurrent.text = reservation.plugObj.plugTypeObj.current.getString(parentActivity)
        popupPlugTypeLevel.text = reservation.plugObj.plugTypeObj.typeLevel
        popupPlugCompatibility.text = reservation.plugObj.plugTypeObj.compatibility
        popupPlugTesla.text = reservation.plugObj.plugTypeObj.tesla.getString(parentActivity)

        parentActivity.getStorageReference().child("plug-types").child(reservation.plugObj.plugTypeObj.imageId).downloadUrl.addOnCompleteListener{
            if(it.isSuccessful){
                if(it.result != null){
                    Picasso.get().load(it.result).into(popupPlugImage)
                }
            }
        }
        dialog.show()
    }

}