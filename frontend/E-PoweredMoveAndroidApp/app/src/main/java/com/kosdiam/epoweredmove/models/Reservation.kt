package com.kosdiam.epoweredmove.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Reservation(
    val id: String,
    var accepted: Boolean,
    var timeStart: Long,
    var timeEnd: Long,
    var vehicleId: String,
    var vehicleObj: Vehicle,
    var userId: String,
    var userObj: User,
    var plugId: String,
    var plugObj: Plug,
    var timestamp: Long,
    var cancelled: Boolean,
    var poiLatitude: Float,
    var poiLongitude: Float
) : Parcelable{
    constructor(): this("", true, 0, 0, "", Vehicle(), "", User(), "", Plug(), 0, false, 0F, 0F)
}
