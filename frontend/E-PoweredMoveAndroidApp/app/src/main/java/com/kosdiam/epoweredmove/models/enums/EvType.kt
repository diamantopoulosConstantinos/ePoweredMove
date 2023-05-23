package com.kosdiam.epoweredmove.models.enums

import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.interfaces.IEnumName

enum class EvType (override val stringResource: Int) : IEnumName {
    MHEV(R.string.ev_type_mhev),
    HEV(R.string.ev_type_hev),
    PHEV(R.string.ev_type_phev),
    BEV(R.string.ev_type_bev);
}