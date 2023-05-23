package com.kosdiam.epoweredmove.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Poi(
    val id: String,
    val latitude: Float,
    val longitude: Float,
    val parking: Boolean,
    val illumination: Boolean,
    val wc: Boolean,
    val shopping: Boolean,
    val food: Boolean,
    val phone: String,
    val userId: String,
    val userObj: User,
    val chargingStationId: String,
    val chargingStationObj: ChargingStation,
    val paymentMethodsObj: List<PaymentMethod>,
    val timestamp: Long,
    val availableSelectedVehicle: Boolean?,
) : Parcelable
