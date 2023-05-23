package com.kosdiam.epoweredmove.models

import android.os.Parcelable
import com.kosdiam.epoweredmove.models.enums.PaymentType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethod(
    val id: String,
    val description: PaymentType,
    val poiId: String,
) : Parcelable
