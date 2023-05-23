package com.kosdiam.epoweredmove.models

import android.os.Parcelable
import com.kosdiam.epoweredmove.models.enums.EvType
import com.kosdiam.epoweredmove.models.enums.VehicleType
import kotlinx.android.parcel.Parcelize
import java.util.Calendar

@Parcelize
data class Vehicle(
    var id: String,
    var brand: String,
    var vehicleType: VehicleType,
    var evType: EvType,
    var model: String,
    var releaseYear: Int,
    var usableBatterySize: Float,
    var plugTypeId: String,
    var plugTypeObj: PlugType,
    var avgConsumption: Float,
    var userId: String,
    var userObj: User,
    var timestamp: Long,
) : Parcelable{
    constructor(): this("", "", VehicleType.CAR, EvType.BEV, "", 2023, 1f, "", PlugType(), 1f, "", User(), Calendar.getInstance()
        .timeInMillis)
}
