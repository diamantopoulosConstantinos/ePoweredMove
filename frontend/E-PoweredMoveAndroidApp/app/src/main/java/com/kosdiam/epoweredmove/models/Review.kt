package com.kosdiam.epoweredmove.models

import android.os.Parcelable
import com.kosdiam.epoweredmove.models.enums.ReviewStatus
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Review(
    val id: String,
    var comments: String?,
    var rating: Int,
    val status: ReviewStatus,
    var timestamp: Long,
    var userId: String,
    val userObj: User,
    var chargingStationId: String,
    val chargingStationObj: ChargingStation
) : Parcelable{
    constructor(): this("", "", 0, ReviewStatus.ACTIVE, 0, "", User(), "", ChargingStation())
}
