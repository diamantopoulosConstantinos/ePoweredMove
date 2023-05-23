package com.kosdiam.epoweredmove.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.storage.StorageReference
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.PlugType
import com.squareup.picasso.Picasso

class PlugTypeSpinnerAdapter(internal  var context: Context, private var plugTypes: MutableList<PlugType>, private var firebaseStorage: StorageReference): BaseAdapter() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return plugTypes.size
    }

    override fun getItem(p0: Int): Any {
        return plugTypes[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var currentPlugType = plugTypes[p0]
        val view = inflater.inflate(R.layout.spinner_plug_type,null)
        val plugTypeImage = view.findViewById<ImageView>(R.id.plug_type_image)
        val plugTypeConnector = view.findViewById<TextView>(R.id.plug_type_connector)

        plugTypeConnector.text = plugTypes[p0].connector
        firebaseStorage.child("plug-types").child(plugTypes[p0].imageId).downloadUrl.addOnCompleteListener{
            if(it.isSuccessful){
                if(it.result != null){
                    Picasso.get().load(it.result).into(plugTypeImage)
                }
            }
        }
        return view
    }
}