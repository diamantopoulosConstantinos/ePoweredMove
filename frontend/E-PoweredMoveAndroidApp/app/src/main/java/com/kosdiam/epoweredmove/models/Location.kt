package com.kosdiam.epoweredmove.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    val latitude: Double,
    val longitude: Double,
    val range: LocationRange = LocationRange.NONE
): Parcelable {
    constructor() : this(0.0, 0.0, LocationRange.NONE)

    fun isSet(): Boolean {
        return latitude != 0.0 && longitude != 0.0
    }
}
