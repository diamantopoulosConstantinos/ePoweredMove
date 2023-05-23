package com.kosdiam.epoweredmove.models.enums

import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.interfaces.IEnumName

enum class ReviewStatus (override val stringResource: Int) : IEnumName {
    ACTIVE(R.string.review_status_active),
    CANCELLED(R.string.review_status_cancelled);
}