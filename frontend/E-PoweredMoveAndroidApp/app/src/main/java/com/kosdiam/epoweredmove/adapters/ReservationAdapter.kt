package com.kosdiam.epoweredmove.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.Reservation
import com.kosdiam.epoweredmove.models.Review
import com.kosdiam.epoweredmove.models.Vehicle
import com.kosdiam.epoweredmove.models.enums.ReviewStatus
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ReservationAdapter(private val reservations: MutableList<Reservation>, private val context: Context): RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemViewClick(position: Int)
        fun onItemCancelClick(position: Int)
        fun onItemRouteInfoClick(position: Int)
        fun onItemMapClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        return ReservationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.reservation_item_view,
                parent,
                false
            ),
            mListener
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val currentReservation = reservations[position]
        val reservationAccepted = holder.itemView.findViewById<TextView>(R.id.reservation_accepted)
        val reservationDate = holder.itemView.findViewById<TextView>(R.id.reservation_date)
        val reservationStartTime = holder.itemView.findViewById<TextView>(R.id.reservation_start_time)
        val reservationEndTime = holder.itemView.findViewById<TextView>(R.id.reservation_end_time)
        val reservationVehicle = holder.itemView.findViewById<TextView>(R.id.reservation_vehicle)
        val reservationPlug = holder.itemView.findViewById<TextView>(R.id.reservation_plug)
        val reservationLayout = holder.itemView.findViewById<LinearLayout>(R.id.reservation_layout)
        val reservationCancelBtn = holder.itemView.findViewById<ImageView>(R.id.reservation_cancel)

        val sdfDate = SimpleDateFormat("dd/MM/yyyy")
        val sdfTime = SimpleDateFormat("HH:mm")
        if(currentReservation.cancelled){
            reservationCancelBtn.visibility = View.GONE
        }
        reservationAccepted.text = if(currentReservation.accepted) "Yes" else "No"
        reservationLayout.background =
            if(currentReservation.cancelled)
                ContextCompat.getDrawable(context, R.drawable.custom_border_red)
            else if(Calendar.getInstance().timeInMillis > currentReservation.timeEnd)
                ContextCompat.getDrawable(context, R.drawable.custom_border)
            else ContextCompat.getDrawable(context, R.drawable.custom_border_green)
        reservationDate.text = sdfDate.format(currentReservation.timeStart)
        reservationStartTime.text = sdfTime.format(currentReservation.timeStart)
        reservationEndTime.text = sdfTime.format(currentReservation.timeEnd)
        val vehicleDetails = currentReservation.vehicleObj.brand + " " + currentReservation.vehicleObj.model
        reservationVehicle.text = vehicleDetails
        reservationPlug.text = currentReservation.plugObj.plugTypeObj.connector
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    class ReservationViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        private val itemViewBtn: ImageView = itemView.findViewById(R.id.reservation_view)
        private val itemCancelBtn: ImageView = itemView.findViewById(R.id.reservation_cancel)
        private val itemRouteInfoBtn: ImageView = itemView.findViewById(R.id.reservation_route_info)
        private val itemMapBtn: ImageView = itemView.findViewById(R.id.reservation_map)
        //create listener for each btn
        init {
            itemViewBtn.setOnClickListener{
                listener.onItemViewClick(adapterPosition)
            }
            itemCancelBtn.setOnClickListener{
                listener.onItemCancelClick(adapterPosition)
            }
            itemRouteInfoBtn.setOnClickListener{
                listener.onItemRouteInfoClick(adapterPosition)
            }
            itemMapBtn.setOnClickListener{
                listener.onItemMapClick(adapterPosition)
            }
        }
    }

}