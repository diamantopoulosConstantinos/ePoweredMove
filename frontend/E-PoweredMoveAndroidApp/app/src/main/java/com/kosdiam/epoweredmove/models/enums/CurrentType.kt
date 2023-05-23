package com.kosdiam.epoweredmove.models.enums

import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.interfaces.IEnumName

enum class CurrentType (override val stringResource: Int) : IEnumName {
    AC(R.string.current_type_ac),
    DC(R.string.current_type_dc);
}