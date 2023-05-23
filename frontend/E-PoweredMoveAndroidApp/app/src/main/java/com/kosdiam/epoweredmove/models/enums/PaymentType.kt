package com.kosdiam.epoweredmove.models.enums

import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.interfaces.IEnumName

enum class PaymentType (override val stringResource: Int) : IEnumName {
    APP(R.string.payment_type_app),
    PAYPAL(R.string.payment_type_paypal),
    EBANKING(R.string.payment_type_ebanking),
    LOCALLY(R.string.payment_type_locally);
}