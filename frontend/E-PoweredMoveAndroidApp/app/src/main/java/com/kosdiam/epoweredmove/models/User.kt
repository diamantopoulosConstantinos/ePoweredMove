package com.kosdiam.epoweredmove.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var id: String,
    var name: String,
    var surname: String?,
    var email: String,
    var phone: String?,
    val password: String?
) : Parcelable{
     constructor(): this("", "", "", "", "", "")
}
