package com.kosdiam.epoweredmove.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.kosdiam.epoweredmove.R

class PoiClusterRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<ClusteredPoi>
) : DefaultClusterRenderer<ClusteredPoi>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: ClusteredPoi, markerOptions: MarkerOptions) {
        if(item.isAvailable() == null){
            markerOptions.icon(bitmapDescriptorFromVector(R.drawable.default_marker_24))
        }
        else if(item.isAvailable()!!){
            markerOptions.icon(bitmapDescriptorFromVector(R.drawable.available_marker_24))
        }
        else{
            markerOptions.icon(bitmapDescriptorFromVector(R.drawable.non_available_marker_24))
        }
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}