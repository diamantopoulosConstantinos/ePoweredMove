package com.kosdiam.epoweredmove.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.Review
import com.kosdiam.epoweredmove.models.Vehicle
import com.kosdiam.epoweredmove.models.enums.ReviewStatus
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class VehicleAdapter(private val vehicles: MutableList<Vehicle>, private val context: Context): RecyclerView.Adapter<VehicleAdapter.ReviewViewHolder>() {
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemViewClick(position: Int)
        fun onItemSelectClick(position: Int)
        fun onItemEditClick(position: Int)
        fun onItemDeleteClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.vehicle_item_view,
                parent,
                false
            ),
            mListener
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentVehicle = vehicles[position]
        val vehicleBrand = holder.itemView.findViewById<TextView>(R.id.vehicle_brand)
        val vehicleModel = holder.itemView.findViewById<TextView>(R.id.vehicle_model)
        val vehicleYear = holder.itemView.findViewById<TextView>(R.id.vehicle_year)
        val vehicleType = holder.itemView.findViewById<TextView>(R.id.vehicle_type)
        val vehicleEvType = holder.itemView.findViewById<TextView>(R.id.vehicle_ev_type)

        vehicleBrand.text = currentVehicle.brand
        vehicleModel.text = currentVehicle.model
        vehicleYear.text = currentVehicle.releaseYear.toString()
        vehicleType.text = currentVehicle.vehicleType.getString(context)
        vehicleEvType.text = currentVehicle.evType.getString(context)
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }

    class ReviewViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        private val itemViewBtn: ImageView = itemView.findViewById(R.id.item_view)
        private val itemSelectBtn: ImageView = itemView.findViewById(R.id.item_select)
        private val itemEditBtn: ImageView = itemView.findViewById(R.id.item_edit)
        private val itemDeleteBtn: ImageView = itemView.findViewById(R.id.item_delete)
        //create listener for each btn
        init {
            itemView.setOnClickListener {
                listener.onItemViewClick(adapterPosition)
            }
            itemViewBtn.setOnClickListener{
                listener.onItemViewClick(adapterPosition)
            }

            itemSelectBtn.setOnClickListener{
                listener.onItemSelectClick(adapterPosition)
            }

            itemEditBtn.setOnClickListener{
                listener.onItemEditClick(adapterPosition)
            }

            itemDeleteBtn.setOnClickListener{
                listener.onItemDeleteClick(adapterPosition)
            }
        }
    }

}