package com.kosdiam.epoweredmove.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChargingStation(
    val id: String,
    val pricePerKwh: Float,
    val autoAccept: Boolean,
    val barcodeEnabled: Boolean,
    val apiEnabled: Boolean,
    val imageId: String?,
) : Parcelable{
    constructor(): this("", 0f, true, true, true, "")
}
