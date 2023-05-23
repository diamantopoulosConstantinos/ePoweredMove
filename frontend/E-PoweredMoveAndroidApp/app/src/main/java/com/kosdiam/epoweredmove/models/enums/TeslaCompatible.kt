package com.kosdiam.epoweredmove.models.enums

import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.interfaces.IEnumName

enum class TeslaCompatible (override val stringResource: Int) : IEnumName {
    YES(R.string.tesla_compatible_yes),
    NO(R.string.tesla_compatible_no);
}