package com.kosdiam.epoweredmove.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RouteInfo(
    val destinationMeters: Long,
    val metersByVehicle: Long,
    val metersByFoot: Long,
) : Parcelable{
    constructor(): this(0L, 0L, 0L)
}
