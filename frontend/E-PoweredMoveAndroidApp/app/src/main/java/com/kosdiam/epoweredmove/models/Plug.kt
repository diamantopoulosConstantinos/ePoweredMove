package com.kosdiam.epoweredmove.models

import android.net.Uri
import android.os.Parcelable
import com.kosdiam.epoweredmove.models.enums.Availability
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Plug(
    val id: String,
    val availability: Availability,
    val power: Float,
    val phases: Int,
    val chargingStationId: String,
    val chargingStationObj: ChargingStation,
    val timestamp: Long,
    val plugTypeId: String,
    val plugTypeObj: PlugType
) : Parcelable{
    constructor(): this("", Availability.AVAILABLE, 0f, 0, "", ChargingStation(), 0, "", PlugType())
}
