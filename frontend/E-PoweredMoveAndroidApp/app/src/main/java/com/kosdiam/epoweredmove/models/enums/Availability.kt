package com.kosdiam.epoweredmove.models.enums

import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.interfaces.IEnumName

enum class Availability (override val stringResource: Int) : IEnumName {
    UNAVAILABLE(R.string.availability_unavailable),
    AVAILABLE(R.string.availability_available),
    INUSE(R.string.availability_inuse),
    UNKNOWN(R.string.availability_unknown);
}