package com.kosdiam.epoweredmove.views.poi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.adapters.PlugAdapter
import com.kosdiam.epoweredmove.adapters.PlugTypeSpinnerAdapter
import com.kosdiam.epoweredmove.adapters.ReviewAdapter
import com.kosdiam.epoweredmove.models.*
import com.kosdiam.epoweredmove.models.enums.MessageType
import com.kosdiam.epoweredmove.services.*
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class PoiActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var poi: Poi

    private lateinit var scrollView: ScrollView
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var gMap: GoogleMap
    private lateinit var transparentMap: ImageView
    private lateinit var chargingStationImage: ImageView
    private lateinit var poiParkingText: TextView
    private lateinit var poiIlluminationText: TextView
    private lateinit var poiWCText: TextView
    private lateinit var poiShopText: TextView
    private lateinit var poiFoodText: TextView
    private lateinit var poiPhoneText: TextView
    private lateinit var poiPaymentMethodText: TextView
    private lateinit var chargingStationPriceText: TextView
    private lateinit var chargingStationAutoAcceptText: TextView
    private lateinit var chargingStationBarcodeText: TextView
    private lateinit var chargingStationApiText: TextView
    private lateinit var reservationBtn: ImageView
    private lateinit var ratingsBtn: ImageView
    private lateinit var plugsBtn: ImageView
    private lateinit var progressBarView: View

    private lateinit var firebaseStorage: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mSharedPreferences: SharedPreferences
    private var PREF_FIREBASE_TOKEN: String? = null
    private var PREF_SELECTED_VEHICLE: String? = null
    private var chargingStationPlugs = mutableListOf<Plug>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poi)
        transparentMap = findViewById(R.id.image_transparent)
        scrollView = findViewById(R.id.scroll_layout)
        chargingStationImage = findViewById(R.id.charging_station_image)
        poiParkingText = findViewById(R.id.poi_parking)
        poiIlluminationText = findViewById(R.id.poi_illumination)
        poiWCText = findViewById(R.id.poi_wc)
        poiShopText = findViewById(R.id.poi_shops)
        poiFoodText = findViewById(R.id.poi_food)
        poiPhoneText = findViewById(R.id.poi_phone)
        poiPaymentMethodText = findViewById(R.id.payment_methods)
        chargingStationPriceText = findViewById(R.id.charging_station_kwh_price)
        chargingStationAutoAcceptText = findViewById(R.id.charging_station_auto_accept)
        chargingStationBarcodeText = findViewById(R.id.charging_station_barcode_enabled)
        chargingStationApiText = findViewById(R.id.charging_station_api_enabled)
        reservationBtn = findViewById(R.id.reservation_btn)
        ratingsBtn = findViewById(R.id.ratings_btn)
        plugsBtn = findViewById(R.id.plugs_btn)
        progressBarView = findViewById(R.id.progress_bar)

        firebaseStorage = FirebaseStorage.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()

        PREF_FIREBASE_TOKEN= getString(R.string.shared_pref_firebase_token)
        PREF_SELECTED_VEHICLE= getString(R.string.shared_pref_selected_vehicle)
        mSharedPreferences = getSharedPreferences(getString(R.string.app_name_shared_prefs), Context.MODE_PRIVATE)
        //set map
        mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        //created a transparent imageview over map in order to not move vertically while user scrolls over map
        transparentMap.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event?.action){
                    MotionEvent.ACTION_DOWN -> {
                        // Disallow ScrollView to intercept touch events
                        scrollView.requestDisallowInterceptTouchEvent(true)
                        // Disable touch on transparent view
                        return false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        scrollView.requestDisallowInterceptTouchEvent(true)
                        return false
                    }
                    MotionEvent.ACTION_UP -> {
                        scrollView.requestDisallowInterceptTouchEvent(false)
                        return true
                    }
                    else -> return true
                }
            }
        })

        reservationBtn.setOnClickListener {
            checkAddingReservation()
        }

        plugsBtn.setOnClickListener {
            showPlugs()
        }

        ratingsBtn.setOnClickListener {
            fetchReviews()
        }

        if(intent.extras != null &&
            intent.extras!!.containsKey(getString(R.string.poi_extra)) &&
            intent.extras?.get(getString(R.string.poi_extra)) != null){
            poi = intent.extras?.get(getString(R.string.poi_extra)) as Poi

            if(this::gMap.isInitialized){
                zoomAtCoordinates(LatLng(poi.latitude.toDouble(), poi.longitude.toDouble()))
            }

            if(firebaseAuth.currentUser != null){
                ratingsBtn.visibility = View.VISIBLE
                reservationBtn.visibility = View.VISIBLE
            }

            if(intent.extras!!.containsKey(getString(R.string.reservation_source_extra)) &&
                intent.extras?.get(getString(R.string.reservation_source_extra)) as Boolean){
                showMessage(MessageType.CLOSEST_CHARGING_STATION_FOUND)
            }

            getChargingStationImage()
            setPoi()
            fetchPlugs()
        }
        else{
            finish()
        }
    }

    private fun checkAddingReservation() {
        if(chargingStationPlugs.isEmpty()){
            showMessage(MessageType.NO_PLUGS_FOUND)
            return
        }
        if(mSharedPreferences.getString(PREF_SELECTED_VEHICLE, null).isNullOrEmpty()){
            showMessage(MessageType.ERROR_NO_VEHICLE_SELECTED)
            return
        }

        val selectedVehicleId = mSharedPreferences.getString(PREF_SELECTED_VEHICLE, null)!!
        //check if current charging station has appropriate plug for selected vehicle
        val vehicleService = VehicleService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
        val callVehicle = vehicleService.vehicleService.getVehicle(selectedVehicleId)
        callVehicle.enqueue(object : retrofit2.Callback<Vehicle> {
            override fun onResponse(call: Call<Vehicle>, response: Response<Vehicle>) {
                if(response.body() != null){
                    val vehicleFound = response.body()!!
                    val plugTypeService = PlugTypeService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
                    val callPlugType = plugTypeService.plugTypeService.getPlugTypesByChargingStation(poi.chargingStationId)
                    callPlugType.enqueue(object : retrofit2.Callback<List<PlugType>> {
                        override fun onResponse(call: Call<List<PlugType>>, response: Response<List<PlugType>>) {
                            if(response.body() != null && response.body()!!.isNotEmpty()){
                                if(response.body()!!.any { plugType ->  plugType.id == vehicleFound.plugTypeId }){
                                    addReservation(vehicleFound)
                                }
                                else {
                                    showMessage(MessageType.ERROR_NO_PLUG_FOUND_FOR_SELECTED_VEHICLE)
                                    return
                                }
                            }
                            else{
                                showMessage(MessageType.ERROR_FETCHING_PLUG_TYPES)
                                return
                            }
                        }
                        override fun onFailure(call: Call<List<PlugType>>, t: Throwable) {
                            showMessage(MessageType.ERROR_FETCHING_PLUG_TYPES)
                            return
                        }
                    })
                }
                else{
                    showMessage(MessageType.ERROR_FETCHING_PLUG_TYPES)
                    return
                }
            }
            override fun onFailure(call: Call<Vehicle>, t: Throwable) {
                showMessage(MessageType.ERROR_FETCHING_PLUG_TYPES)
                return
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun addReservation(vehicle: Vehicle) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_add_reservation)
        dialog.window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val popupSelectDateButton = dialog.findViewById<Button>(R.id.new_reservation_select_date)
        val popupSelectedDateText = dialog.findViewById<TextView>(R.id.new_reservation_selected_date)
        val popupSelectTimeButton = dialog.findViewById<Button>(R.id.new_reservation_select_start_time)
        val popupSelectedTimeText = dialog.findViewById<TextView>(R.id.new_reservation_selected_start_time)
        val popupDuration = dialog.findViewById<EditText>(R.id.new_reservation_duration)
        val popupSubmitBtn = dialog.findViewById<Button>(R.id.new_reservation_save)

        val selectedDateStart = Calendar.getInstance()

        popupSelectDateButton.setOnClickListener {
            val cal = Calendar.getInstance()
            val mYear = cal.get(Calendar.YEAR)
            val mMonth = cal.get(Calendar.MONTH)
            val mDay = cal.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                selectedDateStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                selectedDateStart.set(Calendar.MONTH, monthOfYear)
                selectedDateStart.set(Calendar.YEAR, year)
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                popupSelectedDateText.text = sdf.format(selectedDateStart.time)
                popupSelectedDateText.visibility = View.VISIBLE

            }, mYear, mMonth, mDay)
            dpd.datePicker.minDate = cal.timeInMillis
            dpd.show()
        }

        popupSelectTimeButton.setOnClickListener {
            val cal = Calendar.getInstance()
            val mHour = cal.get(Calendar.HOUR_OF_DAY)
            val mMinute = cal.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this, { _, hourOfDay, minute ->
                selectedDateStart.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDateStart.set(Calendar.MINUTE, minute)
                val sdf = SimpleDateFormat("HH:mm")
                popupSelectedTimeText.text = sdf.format(selectedDateStart.time)
                popupSelectedTimeText.visibility = View.VISIBLE
            }, mHour, mMinute, true)
            tpd.show()
        }

        popupSubmitBtn.setOnClickListener {
            setProgressBarVisibility(View.VISIBLE)
            if(TextUtils.isEmpty(popupSelectedDateText.text) ||
                TextUtils.isEmpty(popupSelectedTimeText.text) ||
                TextUtils.isEmpty(popupDuration.text)){
                showMessage(MessageType.ERROR_ADDING_RESERVATION_MISSING_FIELDS)
                return@setOnClickListener
            }

            val newReservation = Reservation()
            newReservation.timestamp = Calendar.getInstance().timeInMillis
            newReservation.userId = firebaseAuth.currentUser!!.uid
            newReservation.plugId = chargingStationPlugs.find { plug -> plug.plugTypeId ==  vehicle.plugTypeId}!!.id
            newReservation.vehicleId = vehicle.id
            newReservation.accepted = false
            newReservation.timeStart = selectedDateStart.timeInMillis
            selectedDateStart.add(Calendar.HOUR_OF_DAY, popupDuration.text.toString().toInt())
            newReservation.timeEnd = selectedDateStart.timeInMillis
            newReservation.cancelled = false


            val reservationService = ReservationService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
            val call = reservationService.reservationService.createReservation(newReservation)
            call.enqueue(object : retrofit2.Callback<Reservation> {
                override fun onResponse(call: Call<Reservation>, response: Response<Reservation>) {
                    if(response.body() != null){
                        showMessage(MessageType.RESERVATION_ADDED)
                        setProgressBarVisibility(View.GONE)
                        dialog.dismiss()
                    }
                    else{
                        showMessage(MessageType.ERROR_ADDING_RESERVATION_TIME_OVERLAP)
                    }
                }
                override fun onFailure(call: Call<Reservation>, t: Throwable) {
                    showMessage(MessageType.ERROR_ADDING_RESERVATION)
                }
            })
        }

        dialog.show()
    }

    private fun fetchReviews() {
        var reviewsFound = listOf<Review>()
        val reviewService = ReviewService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
        val call = reviewService.reviewService.getReviewsByChargingStation(poi.chargingStationId)
        call.enqueue(object : retrofit2.Callback<List<Review>> {
            override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>) {
                if(response.body() != null && response.body()!!.isNotEmpty()){
                    reviewsFound = response.body()!!
                }
                openReviewsDialog(reviewsFound.toMutableList())
            }

            override fun onFailure(call: Call<List<Review>>, t: Throwable) {
                showMessage(MessageType.ERROR_FETCHING_REVIEWS)
            }

        })
    }

    private fun openReviewsDialog(reviews: MutableList<Review>) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_show_reviews)
        dialog.window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val popupRecyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view)
        val popupReviewComment = dialog.findViewById<EditText>(R.id.review_comment)
        val popupReviewRating = dialog.findViewById<RatingBar>(R.id.review_rating)
        val popupReviewAddBtn = dialog.findViewById<ImageView>(R.id.add_review_btn)

        popupRecyclerView.layoutManager = LinearLayoutManager(this)
        val reviewsAdapter = ReviewAdapter(reviews, firebaseAuth.currentUser!!.uid)
        popupRecyclerView.adapter = reviewsAdapter

        popupReviewAddBtn.setOnClickListener {
            setProgressBarVisibility(View.VISIBLE)
            if(popupReviewRating.rating != 0f){
                val newReview = Review()
                newReview.rating = popupReviewRating.rating.toInt()
                newReview.chargingStationId = poi.chargingStationId
                newReview.comments = if(!TextUtils.isEmpty(popupReviewComment.text)) popupReviewComment.text.toString() else ""
                newReview.userId = firebaseAuth.currentUser!!.uid
                newReview.userObj.name = "reviewed by you"
                newReview.timestamp = Calendar.getInstance().timeInMillis

                val reviewService = ReviewService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
                val call = reviewService.reviewService.createReview(newReview)
                call.enqueue(object : retrofit2.Callback<Review> {
                    override fun onResponse(call: Call<Review>, response: Response<Review>) {
                        if(response.body() != null){
                            //reset
                            popupReviewComment.text.clear()
                            popupReviewRating.rating = 0f

                            reviews.add(0, newReview)
                            reviewsAdapter.notifyItemChanged(0)
                            setProgressBarVisibility(View.GONE)
                        }
                        else{
                            showMessage(MessageType.ERROR_ADDING_REVIEW)
                        }
                    }

                    override fun onFailure(call: Call<Review>, t: Throwable) {
                        showMessage(MessageType.ERROR_ADDING_REVIEW)
                    }

                })
            }
            else{
                setProgressBarVisibility(View.GONE)
            }
        }
        dialog.show()
    }

    private fun fetchPlugs() {
        val plugService = PlugService()
        val call = plugService.plugService.getPlugsByChargingStation(poi.chargingStationId)
        call.enqueue(object : retrofit2.Callback<List<Plug>> {
            override fun onResponse(call: Call<List<Plug>>, response: Response<List<Plug>>) {
                if(response.body() != null && response.body()!!.isNotEmpty()){
                    chargingStationPlugs = response.body()!!.toMutableList()
                }
                setProgressBarVisibility(View.GONE)
            }

            override fun onFailure(call: Call<List<Plug>>, t: Throwable) {
                showMessage(MessageType.ERROR_FETCHING_PLUGS)
            }
        })
    }

    private fun showPlugs(){
        if(chargingStationPlugs.isEmpty()){
            showMessage(MessageType.NO_PLUGS_FOUND)
            return
        }
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_show_plugs)
        val popupRecyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view)

        popupRecyclerView.layoutManager = LinearLayoutManager(this)
        val plugsAdapter = PlugAdapter(chargingStationPlugs, this)
        popupRecyclerView.adapter = plugsAdapter
        plugsAdapter.setOnItemClickListener(object : PlugAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
              val currentPlug = chargingStationPlugs[position]
                val insideDialog = Dialog(this@PoiActivity)
                insideDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                insideDialog.setCancelable(true)
                insideDialog.setContentView(R.layout.popup_preview_plug)
                val popupPlugImage = insideDialog.findViewById<ImageView>(R.id.plug_image_preview)
                val popupAvailability = insideDialog.findViewById<TextView>(R.id.plug_availability_preview)
                val popupPower = insideDialog.findViewById<TextView>(R.id.plug_power_preview)
                val popupPhases = insideDialog.findViewById<TextView>(R.id.plug_phases_preview)
                val popupCurrent = insideDialog.findViewById<TextView>(R.id.plug_current_preview)
                val popupCompatibility= insideDialog.findViewById<TextView>(R.id.plug_compatibility_preview)
                val popupConnector= insideDialog.findViewById<TextView>(R.id.plug_connector_preview)
                val popupTypeLevel = insideDialog.findViewById<TextView>(R.id.plug_type_level_preview)
                val popupTeslaCompat = insideDialog.findViewById<TextView>(R.id.plug_tesla_compatible_preview)
                val popupDescription = insideDialog.findViewById<TextView>(R.id.plug_description_preview)

                Picasso.get().load(currentPlug.plugTypeObj.imageUri).into(popupPlugImage)
                popupAvailability.text = currentPlug.availability.getString(this@PoiActivity)
                popupPower.text = currentPlug.power.toString()
                popupPhases.text = currentPlug.phases.toString()
                popupCurrent.text = currentPlug.plugTypeObj.current.getString(this@PoiActivity)
                popupCompatibility.text = currentPlug.plugTypeObj.compatibility
                popupConnector.text = currentPlug.plugTypeObj.connector
                popupTypeLevel.text = currentPlug.plugTypeObj.typeLevel
                popupTeslaCompat.text = currentPlug.plugTypeObj.tesla.getString(this@PoiActivity)
                popupDescription.text = currentPlug.plugTypeObj.description

                insideDialog.show()
            }

        })

        chargingStationPlugs.forEach { plug ->
            firebaseStorage.child("plug-types").child(plug.plugTypeObj.imageId).downloadUrl.addOnCompleteListener{
                if(it.isSuccessful){
                    if(it.result != null){
                        val position = chargingStationPlugs.indexOf(plug)
                        plug.plugTypeObj.imageUri = it.result
                        plugsAdapter.notifyItemChanged(position)
                    }
                }
            }
        }

        dialog.show()

    }

    private fun setPoi() {
        if(this::poi.isInitialized){
            poiParkingText.text = if(poi.parking) "Yes" else "No"
            poiIlluminationText.text = if(poi.illumination) "Yes" else "No"
            poiWCText.text = if(poi.wc) "Yes" else "No"
            poiShopText.text = if(poi.shopping) "Yes" else "No"
            poiFoodText.text = if(poi.food) "Yes" else "No"
            poiPhoneText.text = poi.phone
            chargingStationPriceText.text = poi.chargingStationObj.pricePerKwh.toString()
            chargingStationAutoAcceptText.text = if(poi.chargingStationObj.autoAccept) "Yes" else "No"
            chargingStationBarcodeText.text = if(poi.chargingStationObj.barcodeEnabled) "Yes" else "No"
            chargingStationApiText.text = if(poi.chargingStationObj.apiEnabled) "Yes" else "No"

            val paymentMethods = StringBuilder()
            poi.paymentMethodsObj.forEach { paymentMethod ->
                if(poi.paymentMethodsObj.indexOf(paymentMethod) != 0){
                    paymentMethods.append(", ")
                }
                paymentMethods.append(paymentMethod.description.getString(this))
            }
            poiPaymentMethodText.text = paymentMethods.toString()
        }
    }

    private fun getChargingStationImage() {
        if(poi.chargingStationObj.imageId != null){
            firebaseStorage.child("charging-stations").child(poi.chargingStationObj.imageId!!).downloadUrl.addOnCompleteListener{
                if(it.isSuccessful){
                    if(it.result != null){
                        Picasso.get()
                            .load(it.result)
                            .placeholder(R.drawable.charging_station_default)
                            .into(chargingStationImage)
                    }
                }
                else{
                    showMessage(MessageType.DOWNLOAD_IMAGE_ERROR)
                }
            }
        }
    }

    private fun zoomAtCoordinates(coordinates: LatLng){
        gMap.clear()
        gMap.addMarker(MarkerOptions().position(coordinates).icon(bitmapDescriptorFromVector(R.drawable.location_marker_svgrepo_com)))
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 14.5f))
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(this, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        if(this::poi.isInitialized){
            zoomAtCoordinates(LatLng(poi.latitude.toDouble(), poi.longitude.toDouble()))
        }
    }

    private fun showMessage(messageResponse: MessageType){
        Toast.makeText(this, messageResponse.getString(this), Toast.LENGTH_LONG).show()
        setProgressBarVisibility(View.GONE)
    }

    fun setProgressBarVisibility(newState: Int){
        progressBarView.visibility = newState
    }
}