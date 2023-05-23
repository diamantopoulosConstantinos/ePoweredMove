package com.kosdiam.epoweredmove.models.enums

import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.interfaces.IEnumName

enum class VehicleType (override val stringResource: Int) : IEnumName {
    CAR(R.string.vehicle_type_car),
    MOTORBIKE(R.string.vehicle_type_motorbike),
    SCOOTER(R.string.vehicle_type_scooter),
    BICYCLE(R.string.vehicle_type_bicycle),
    OTHER(R.string.vehicle_type_other);
}