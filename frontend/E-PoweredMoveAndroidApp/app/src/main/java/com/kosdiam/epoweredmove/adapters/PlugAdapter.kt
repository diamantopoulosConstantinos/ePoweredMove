package com.kosdiam.epoweredmove.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.Plug
import com.squareup.picasso.Picasso

class PlugAdapter(private val plugs: List<Plug>, private val context: Context): RecyclerView.Adapter<PlugAdapter.PlugViewHolder>() {
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlugViewHolder {
        return PlugViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.plug_item_view,
                parent,
                false
            ),
            mListener
        )
    }

    override fun onBindViewHolder(holder: PlugViewHolder, position: Int) {
        val currentPlug = plugs[position]
        val plugImage = holder.itemView.findViewById<ImageView>(R.id.plug_image)
        val availablePlug = holder.itemView.findViewById<TextView>(R.id.available_plug)
        val plugPower = holder.itemView.findViewById<TextView>(R.id.plug_power)
        val plugPhases = holder.itemView.findViewById<TextView>(R.id.plug_phases)

        availablePlug.text = currentPlug.availability.getString(context)
        plugPower.text = currentPlug.power.toString()
        plugPhases.text = currentPlug.phases.toString()

        if(currentPlug.plugTypeObj.imageUri != null){
            Picasso.get().load(currentPlug.plugTypeObj.imageUri).into(plugImage)
        }
    }

    override fun getItemCount(): Int {
        return plugs.size
    }

    class PlugViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        private val previewPlugBtn: ImageView = itemView.findViewById(R.id.preview_plug)

        //create listener for each preview btn
        init {
            previewPlugBtn.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

}