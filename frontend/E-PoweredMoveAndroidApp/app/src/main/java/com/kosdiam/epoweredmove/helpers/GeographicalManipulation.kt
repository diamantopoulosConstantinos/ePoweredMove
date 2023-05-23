package com.kosdiam.epoweredmove.helpers

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.kosdiam.epoweredmove.models.Location

class GeographicalManipulation {
    fun areOverplapping(location1: Location, location2: Location ): Boolean {
        val distance = SphericalUtil.computeDistanceBetween(LatLng(location1.latitude, location1.longitude), LatLng(location2.latitude, location2.longitude))
        val range1 = if(location1.range.getDoubleByOrdinal() != null) location1.range.getDoubleByOrdinal()!! else 0.0
        val range2 = if(location2.range.getDoubleByOrdinal() != null) location2.range.getDoubleByOrdinal()!! else 0.0
        if(distance <= (range1 + range2)){
            return true
        }
        return false
    }
}