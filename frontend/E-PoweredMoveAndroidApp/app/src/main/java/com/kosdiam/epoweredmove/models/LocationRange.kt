package com.kosdiam.epoweredmove.models

import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.interfaces.IEnumName

enum class LocationRange(override val stringResource: Int) : IEnumName {
    NONE(R.string.location_no_range),
    M_100(R.string.location_100m),
    M_500(R.string.location_500m),
    KM_1(R.string.location_1km),
    KM_5(R.string.location_5km),
    KM_10(R.string.location_10km),
    KM_50(R.string.location_50km),
    UNKNOWN(R.string.location_unknown);

    fun getDoubleByOrdinal(): Double?{
        return when(this){
            NONE -> { 0.0}
            M_100 -> {100.0}
            M_500 -> {500.0}
            KM_1 -> {1000.0}
            KM_5 -> {5000.0}
            KM_10 -> {10000.0}
            KM_50 -> {50000.0}
            UNKNOWN -> {null}
        }
    }
}