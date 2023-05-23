package com.kosdiam.epoweredmove.views.menu

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.adapters.PlugAdapter
import com.kosdiam.epoweredmove.adapters.PlugTypeSpinnerAdapter
import com.kosdiam.epoweredmove.adapters.ReviewAdapter
import com.kosdiam.epoweredmove.adapters.VehicleAdapter
import com.kosdiam.epoweredmove.helpers.Validators
import com.kosdiam.epoweredmove.models.PlugType
import com.kosdiam.epoweredmove.models.Poi
import com.kosdiam.epoweredmove.models.Review
import com.kosdiam.epoweredmove.models.Vehicle
import com.kosdiam.epoweredmove.models.enums.EvType
import com.kosdiam.epoweredmove.models.enums.MessageType
import com.kosdiam.epoweredmove.models.enums.VehicleType
import com.kosdiam.epoweredmove.services.PlugTypeService
import com.kosdiam.epoweredmove.services.PoiService
import com.kosdiam.epoweredmove.services.ReviewService
import com.kosdiam.epoweredmove.services.VehicleService
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class GarageFragment : Fragment() {
    private lateinit var selectedVehicleCardView: CardView
    private lateinit var selectedVehicleBrand: TextView
    private lateinit var selectedVehicleModel: TextView
    private lateinit var selectedVehicleYear: TextView
    private lateinit var selectedVehicleType: TextView
    private lateinit var selectedVehicleEvType: TextView
    private lateinit var selectedVehicleRemove: ImageView
    private lateinit var addVehicle: ImageView
    private lateinit var garageRecyclerView: RecyclerView

    private lateinit var parentActivity: MenuActivity

    private lateinit var mSharedPreferences: SharedPreferences
    private var PREF_SELECTED_VEHICLE: String? = null
    private var PREF_FIREBASE_TOKEN: String? = null

    private var vehicles = mutableListOf<Vehicle>()
    private var selectedVehicle: Vehicle? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_garage, container, false)
        selectedVehicleCardView = view.findViewById(R.id.selected_vehicle_card_view)
        selectedVehicleBrand = view.findViewById(R.id.selected_vehicle_brand)
        selectedVehicleModel = view.findViewById(R.id.selected_vehicle_model)
        selectedVehicleYear = view.findViewById(R.id.selected_vehicle_year)
        selectedVehicleType = view.findViewById(R.id.selected_vehicle_type)
        selectedVehicleEvType = view.findViewById(R.id.selected_vehicle_ev_type)
        selectedVehicleRemove = view.findViewById(R.id.remove_selected_vehicle)
        garageRecyclerView = view.findViewById(R.id.vehicles_recycler_view)
        addVehicle = view.findViewById(R.id.add_vehicle)

        if(activity!=null){
            parentActivity = activity as MenuActivity
        }

        PREF_SELECTED_VEHICLE= getString(R.string.shared_pref_selected_vehicle)
        PREF_FIREBASE_TOKEN= getString(R.string.shared_pref_firebase_token)
        mSharedPreferences = parentActivity.getSharedPreferences(getString(R.string.app_name_shared_prefs), Context.MODE_PRIVATE)

        selectedVehicleRemove.setOnClickListener {
            removeSelectedVehicle()
        }

        addVehicle.setOnClickListener {
            addVehicleDialog()
        }

        fetchVehicles()

        return view
    }

    private fun fetchVehicles() {
        val vehicleService = VehicleService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
        val call = vehicleService.vehicleService.getVehiclesByUser(parentActivity.getFirebaseAuth().currentUser!!.uid)
        call.enqueue(object : retrofit2.Callback<List<Vehicle>> {
            override fun onResponse(call: Call<List<Vehicle>>, response: Response<List<Vehicle>>) {
                if(response.body() != null && response.body()!!.isNotEmpty()){
                    vehicles.addAll(response.body()!!)
                }
                setVehicles()
            }

            override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {
                parentActivity.showMessage(MessageType.ERROR_FETCHING_VEHICLES)
            }

        })
    }

    private fun setVehicles() {
        garageRecyclerView.layoutManager = LinearLayoutManager(parentActivity)
        val vehiclesAdapter = VehicleAdapter(vehicles, parentActivity)
        garageRecyclerView.adapter = vehiclesAdapter

        vehiclesAdapter.setOnItemClickListener(object : VehicleAdapter.OnItemClickListener{
            override fun onItemViewClick(position: Int) {
                if(vehicles.isNotEmpty() && vehicles.lastIndex >= position){
                    showVehicleDialog(vehicles[position])
                }
            }

            override fun onItemSelectClick(position: Int) {
                if(vehicles.isNotEmpty() && vehicles.lastIndex >= position){
                    addSelectedVehicle(vehicles[position])
                }
            }

            override fun onItemEditClick(position: Int) {
                if(vehicles.isNotEmpty() && vehicles.lastIndex >= position){
                    editVehicleDialog(vehicles[position])
                }
            }

            override fun onItemDeleteClick(position: Int) {
                if(vehicles.isNotEmpty() && vehicles.lastIndex >= position){
                    deleteVehicleDialog(vehicles[position])
                }
            }

        })

        val selectedVehicleId = mSharedPreferences.getString(PREF_SELECTED_VEHICLE, null)
        if(selectedVehicleId != null && vehicles.any { vehicle -> vehicle.id == selectedVehicleId }){
            addSelectedVehicle(vehicles.find { vehicle -> vehicle.id == selectedVehicleId }!!)
        }
    }

    private fun addVehicleDialog() {
        val dialog = Dialog(parentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_add_vehicle)
        dialog.window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val popupBrand = dialog.findViewById<EditText>(R.id.new_vehicle_brand)
        val popupModel = dialog.findViewById<EditText>(R.id.new_vehicle_model)
        val popupYear = dialog.findViewById<EditText>(R.id.new_vehicle_year)
        val popupType = dialog.findViewById<Spinner>(R.id.new_vehicle_type_spinner)
        val popupEvType = dialog.findViewById<Spinner>(R.id.new_vehicle_ev_type_spinner)
        val popupBatterySize = dialog.findViewById<EditText>(R.id.new_vehicle_battery_size)
        val popupAvgConsumption = dialog.findViewById<EditText>(R.id.new_vehicle_avg_consumption)
        val popupPlugType = dialog.findViewById<Spinner>(R.id.new_vehicle_plug_type_spinner)
        val popupSaveBtn = dialog.findViewById<Button>(R.id.new_vehicle_save)

        val plugTypesFound = mutableListOf<PlugType>()
        val plugTypeService = PlugTypeService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
        val call = plugTypeService.plugTypeService.getPlugTypes()
        call.enqueue(object : retrofit2.Callback<List<PlugType>> {
            override fun onResponse(call: Call<List<PlugType>>, response: Response<List<PlugType>>) {
                if(response.body() != null && response.body()!!.isNotEmpty()){
                    plugTypesFound.addAll(response.body()!!.toMutableList())
                    val adapter = PlugTypeSpinnerAdapter(requireContext(), response.body()!!.toMutableList(), parentActivity.getStorageReference())
                    popupPlugType.adapter = adapter
                    popupPlugType.setSelection(0)
                }
                else{
                    dialog.dismiss()
                    parentActivity.showMessage(MessageType.ERROR_FETCHING_PLUG_TYPES)
                }
            }
            override fun onFailure(call: Call<List<PlugType>>, t: Throwable) {
                dialog.dismiss()
                parentActivity.showMessage(MessageType.ERROR_FETCHING_PLUG_TYPES)
            }
        })

        val vehicleTypes = resources.getStringArray(R.array.vehicle_types)
        val vehicleTypesAdapter = ArrayAdapter(parentActivity, R.layout.spinner_item_text, vehicleTypes)
        popupType.adapter = vehicleTypesAdapter

        val evTypes = resources.getStringArray(R.array.ev_types)
        val evTypesAdapter = ArrayAdapter(parentActivity, R.layout.spinner_item_text, evTypes)
        popupEvType.adapter = evTypesAdapter

        popupSaveBtn.setOnClickListener {
            val validators = Validators()
            popupBrand.error = null
            popupModel.error = null
            popupYear.error = null
            popupBatterySize.error = null
            popupAvgConsumption.error = null
            if(!validators.validateText(popupBrand.text, parentActivity)){
                popupBrand.error = parentActivity.getString(R.string.not_valid_text)
                return@setOnClickListener
            }
            if(!validators.validateText(popupModel.text, parentActivity)){
                popupModel.error = parentActivity.getString(R.string.not_valid_text)
                return@setOnClickListener
            }
            if(!validators.validateText(popupYear.text, parentActivity)){
                popupYear.error = parentActivity.getString(R.string.not_valid_text)
                return@setOnClickListener
            }
            if(!validators.validateText(popupBatterySize.text, parentActivity)){
                popupBatterySize.error = parentActivity.getString(R.string.not_valid_text)
                return@setOnClickListener
            }
            if(!validators.validateText(popupAvgConsumption.text, parentActivity)){
                popupAvgConsumption.error = parentActivity.getString(R.string.not_valid_text)
                return@setOnClickListener
            }

            val newVehicle = Vehicle()
            newVehicle.timestamp = Calendar.getInstance().timeInMillis
            newVehicle.avgConsumption =  popupAvgConsumption.text.toString().toFloat()
            newVehicle.model = popupModel.text.toString()
            newVehicle.brand = popupBrand.text.toString()
            newVehicle.evType = EvType.values()[popupEvType.selectedItemPosition]
            newVehicle.plugTypeId = plugTypesFound[popupPlugType.selectedItemPosition].id
            newVehicle.vehicleType = VehicleType.values()[popupType.selectedItemPosition]
            newVehicle.releaseYear = popupYear.text.toString().toInt()
            newVehicle.usableBatterySize = popupBatterySize.text.toString().toFloat()
            newVehicle.userId = parentActivity.getFirebaseAuth().currentUser!!.uid

            val vehicleService = VehicleService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
            val call = vehicleService.vehicleService.createVehicle(newVehicle)
            call.enqueue(object : retrofit2.Callback<Vehicle> {
                override fun onResponse(call: Call<Vehicle>, response: Response<Vehicle>) {
                    if(response.body() != null){
                        vehicles.add(0, response.body()!!)
                        garageRecyclerView.adapter!!.notifyItemInserted(0)
                        dialog.dismiss()
                    }
                    else{
                        parentActivity.showMessage(MessageType.ERROR_ADDING_VEHICLE)
                    }
                }
                override fun onFailure(call: Call<Vehicle>, t: Throwable) {
                    parentActivity.showMessage(MessageType.ERROR_ADDING_VEHICLE)
                }
            })
        }

        dialog.show()
    }

    private fun editVehicleDialog(vehicle: Vehicle) {
        val dialog = Dialog(parentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_add_vehicle)
        dialog.window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val popupTitle = dialog.findViewById<TextView>(R.id.new_vehicle_title)
        val popupBrand = dialog.findViewById<EditText>(R.id.new_vehicle_brand)
        val popupModel = dialog.findViewById<EditText>(R.id.new_vehicle_model)
        val popupYear = dialog.findViewById<EditText>(R.id.new_vehicle_year)
        val popupType = dialog.findViewById<Spinner>(R.id.new_vehicle_type_spinner)
        val popupEvType = dialog.findViewById<Spinner>(R.id.new_vehicle_ev_type_spinner)
        val popupBatterySize = dialog.findViewById<EditText>(R.id.new_vehicle_battery_size)
        val popupAvgConsumption = dialog.findViewById<EditText>(R.id.new_vehicle_avg_consumption)
        val popupPlugType = dialog.findViewById<Spinner>(R.id.new_vehicle_plug_type_spinner)
        val popupSaveBtn = dialog.findViewById<Button>(R.id.new_vehicle_save)

        popupTitle.setText("Edit Vehicle")
        val plugTypesFound = mutableListOf<PlugType>()
        val plugTypeService = PlugTypeService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
        val call = plugTypeService.plugTypeService.getPlugTypes()
        call.enqueue(object : retrofit2.Callback<List<PlugType>> {
            override fun onResponse(call: Call<List<PlugType>>, response: Response<List<PlugType>>) {
                if(response.body() != null && response.body()!!.isNotEmpty()){
                    plugTypesFound.addAll(response.body()!!.toMutableList())
                    val adapter = PlugTypeSpinnerAdapter(requireContext(), response.body()!!.toMutableList(), parentActivity.getStorageReference())
                    popupPlugType.adapter = adapter
                    popupPlugType.setSelection(plugTypesFound.indexOf(plugTypesFound.find {
                            plugType -> plugType.id == vehicle.plugTypeObj.id
                    }))
                }
                else{
                    dialog.dismiss()
                    parentActivity.showMessage(MessageType.ERROR_FETCHING_PLUG_TYPES)
                }
            }
            override fun onFailure(call: Call<List<PlugType>>, t: Throwable) {
                dialog.dismiss()
                parentActivity.showMessage(MessageType.ERROR_FETCHING_PLUG_TYPES)
            }
        })

        val vehicleTypes = resources.getStringArray(R.array.vehicle_types)
        val vehicleTypesAdapter = ArrayAdapter(parentActivity, R.layout.spinner_item_text, vehicleTypes)
        popupType.adapter = vehicleTypesAdapter

        val evTypes = resources.getStringArray(R.array.ev_types)
        val evTypesAdapter = ArrayAdapter(parentActivity, R.layout.spinner_item_text, evTypes)
        popupEvType.adapter = evTypesAdapter

        popupAvgConsumption.setText(vehicle.avgConsumption.toString())
        popupModel.setText(vehicle.model)
        popupBrand.setText(vehicle.brand)
        popupYear.setText(vehicle.releaseYear.toString())
        popupBatterySize.setText(vehicle.usableBatterySize.toString())
        popupType.setSelection(VehicleType.valueOf(vehicle.vehicleType.name).ordinal)
        popupEvType.setSelection(EvType.valueOf(vehicle.evType.name).ordinal)


        popupSaveBtn.setOnClickListener {
            val validators = Validators()
            popupBrand.error = null
            popupModel.error = null
            popupYear.error = null
            popupBatterySize.error = null
            popupAvgConsumption.error = null
            if(!validators.validateText(popupBrand.text, parentActivity)){
                popupBrand.error = parentActivity.getString(R.string.not_valid_text)
                return@setOnClickListener
            }
            if(!validators.validateText(popupModel.text, parentActivity)){
                popupModel.error = parentActivity.getString(R.string.not_valid_text)
                return@setOnClickListener
            }
            if(!validators.validateText(popupYear.text, parentActivity)){
                popupYear.error = parentActivity.getString(R.string.not_valid_text)
                return@setOnClickListener
            }
            if(!validators.validateText(popupBatterySize.text, parentActivity)){
                popupBatterySize.error = parentActivity.getString(R.string.not_valid_text)
                return@setOnClickListener
            }
            if(!validators.validateText(popupAvgConsumption.text, parentActivity)){
                popupAvgConsumption.error = parentActivity.getString(R.string.not_valid_text)
                return@setOnClickListener
            }

            val editedVehicle = Vehicle()
            editedVehicle.id = vehicle.id
            editedVehicle.userId = vehicle.userId
            editedVehicle.userObj = vehicle.userObj
            editedVehicle.avgConsumption =  popupAvgConsumption.text.toString().toFloat()
            editedVehicle.model = popupModel.text.toString()
            editedVehicle.brand = popupBrand.text.toString()
            editedVehicle.evType = EvType.values()[popupEvType.selectedItemPosition]
            editedVehicle.plugTypeId = plugTypesFound[popupPlugType.selectedItemPosition].id
            editedVehicle.plugTypeObj = plugTypesFound[popupPlugType.selectedItemPosition]
            editedVehicle.vehicleType = VehicleType.values()[popupType.selectedItemPosition]
            editedVehicle.releaseYear = popupYear.text.toString().toInt()
            editedVehicle.usableBatterySize = popupBatterySize.text.toString().toFloat()

            val vehicleService = VehicleService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
            val call = vehicleService.vehicleService.updateVehicle(editedVehicle)
            call.enqueue(object : retrofit2.Callback<Vehicle> {
                override fun onResponse(call: Call<Vehicle>, response: Response<Vehicle>) {
                    if(response.body() != null){
                        val vehiclePosition = vehicles.indexOf(vehicle)
                        vehicles[vehiclePosition] = editedVehicle
                        garageRecyclerView.adapter!!.notifyItemChanged(vehiclePosition)
                        dialog.dismiss()
                    }
                    else{
                        parentActivity.showMessage(MessageType.ERROR_EDITING_VEHICLE)
                    }
                }
                override fun onFailure(call: Call<Vehicle>, t: Throwable) {
                    parentActivity.showMessage(MessageType.ERROR_EDITING_VEHICLE)
                }
            })
        }

        dialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun showVehicleDialog(vehicle: Vehicle) {
        val dialog = Dialog(parentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_show_vehicle)
        dialog.window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val popupBrand = dialog.findViewById<TextView>(R.id.viewed_vehicle_brand)
        val popupModel = dialog.findViewById<TextView>(R.id.viewed_vehicle_model)
        val popupYear = dialog.findViewById<TextView>(R.id.viewed_vehicle_year)
        val popupType = dialog.findViewById<TextView>(R.id.viewed_vehicle_type)
        val popupEvType = dialog.findViewById<TextView>(R.id.viewed_vehicle_ev_type)
        val popupBatterySize = dialog.findViewById<TextView>(R.id.viewed_vehicle_battery_size)
        val popupAvgConsumption = dialog.findViewById<TextView>(R.id.viewed_vehicle_avg_consumption)
        val popupInputDate = dialog.findViewById<TextView>(R.id.viewed_vehicle_input_date)
        val popupPlugConnector = dialog.findViewById<TextView>(R.id.viewed_vehicle_plug_connector)
        val popupPlugCurrent = dialog.findViewById<TextView>(R.id.viewed_vehicle_plug_current)
        val popupPlugTypeLevel = dialog.findViewById<TextView>(R.id.viewed_vehicle_plug_type_level)
        val popupPlugCompatibility = dialog.findViewById<TextView>(R.id.viewed_vehicle_plug_compatibility)
        val popupPlugTesla = dialog.findViewById<TextView>(R.id.viewed_vehicle_plug_tesla)
        val popupPlugImage = dialog.findViewById<ImageView>(R.id.viewed_vehicle_plug_image)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        popupBrand.text = vehicle.brand
        popupModel.text = vehicle.model
        popupYear.text = vehicle.releaseYear.toString()
        popupType.text = vehicle.vehicleType.getString(parentActivity)
        popupEvType.text = vehicle.evType.getString(parentActivity)
        popupBatterySize.text = vehicle.usableBatterySize.toString()
        popupAvgConsumption.text = vehicle.avgConsumption.toString()
        popupInputDate.text = sdf.format(vehicle.timestamp)
        popupPlugConnector.text = vehicle.plugTypeObj.connector
        popupPlugCurrent.text = vehicle.plugTypeObj.current.getString(parentActivity)
        popupPlugTypeLevel.text = vehicle.plugTypeObj.typeLevel
        popupPlugCompatibility.text = vehicle.plugTypeObj.compatibility
        popupPlugTesla.text = vehicle.plugTypeObj.tesla.getString(parentActivity)

        parentActivity.getStorageReference().child("plug-types").child(vehicle.plugTypeObj.imageId).downloadUrl.addOnCompleteListener{
            if(it.isSuccessful){
                if(it.result != null){
                    Picasso.get().load(it.result).into(popupPlugImage)
                }
            }
        }


        dialog.show()
    }

    private fun deleteVehicleDialog(vehicle: Vehicle) {
        val dialog = AlertDialog.Builder(parentActivity, R.style.AlertDialogCustom)
        dialog.setPositiveButton(parentActivity.getString(R.string.yes)) { di, _ ->
            di.dismiss()
            parentActivity.setProgressBarVisibility(View.VISIBLE)

            val vehicleService = VehicleService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
            val call = vehicleService.vehicleService.deleteVehicle(vehicle.id)
            call.enqueue(object : retrofit2.Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.body() != null){
                        if(response.body()!!){
                            val vehiclePosition = vehicles.indexOf(vehicle)
                            vehicles.remove(vehicle)
                            garageRecyclerView.adapter!!.notifyItemRemoved(vehiclePosition)
                        }
                        else{
                            parentActivity.showMessage(MessageType.ERROR_DELETING_VEHICLE)
                        }
                        parentActivity.setProgressBarVisibility(View.GONE)
                    }
                    else{
                        parentActivity.showMessage(MessageType.ERROR_DELETING_VEHICLE)
                        parentActivity.setProgressBarVisibility(View.GONE)
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    parentActivity.showMessage(MessageType.ERROR_DELETING_VEHICLE)
                }

            })

        }
        dialog.setNegativeButton(parentActivity.getString(R.string.no)) { di, _ ->
            di.dismiss()
        }
        val alert = dialog.create()
        val typedValue = TypedValue()
        parentActivity.theme.resolveAttribute(R.attr.dialog_color, typedValue, true)
        alert.setTitle(parentActivity.getString(R.string.delete_vehicle_title))
        alert.setMessage(parentActivity.getString(R.string.delete_vehicle_question))
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(parentActivity, R.color.red))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(parentActivity, R.color.green))
    }

    private fun addSelectedVehicle(selectedVehicle: Vehicle) {
        val vehiclePosition = vehicles.indexOf(selectedVehicle)
        vehicles.remove(selectedVehicle)
        garageRecyclerView.adapter!!.notifyItemRemoved(vehiclePosition)

        if(this.selectedVehicle != null){
            vehicles.add(0, this.selectedVehicle!!)
            garageRecyclerView.adapter!!.notifyItemRemoved(0)
        }
        this.selectedVehicle = selectedVehicle

        selectedVehicleBrand.text = selectedVehicle.brand
        selectedVehicleModel.text = selectedVehicle.model
        selectedVehicleYear.text = selectedVehicle.releaseYear.toString()
        selectedVehicleType.text = selectedVehicle.vehicleType.getString(parentActivity)
        selectedVehicleEvType.text = selectedVehicle.evType.getString(parentActivity)

        selectedVehicleCardView.visibility = View.VISIBLE

        mSharedPreferences.edit {
            putString(PREF_SELECTED_VEHICLE , selectedVehicle.id)
        }
    }

    private fun removeSelectedVehicle(){
        if(selectedVehicle != null){
            vehicles.add(0, selectedVehicle!!)
            garageRecyclerView.adapter!!.notifyItemInserted(0)
            selectedVehicleCardView.visibility = View.GONE
            selectedVehicle = null
            mSharedPreferences.edit {
                putString(PREF_SELECTED_VEHICLE , null)
            }
        }
    }


}