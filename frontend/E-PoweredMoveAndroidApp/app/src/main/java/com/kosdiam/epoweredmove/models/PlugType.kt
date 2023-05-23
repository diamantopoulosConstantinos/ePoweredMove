package com.kosdiam.epoweredmove.models

import android.net.Uri
import android.os.Parcelable
import com.kosdiam.epoweredmove.models.enums.CurrentType
import com.kosdiam.epoweredmove.models.enums.TeslaCompatible
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlugType(
    val id: String,
    val connector: String,
    val current: CurrentType,
    val typeLevel: String,
    val description: String,
    val compatibility: String,
    val tesla: TeslaCompatible,
    val imageId: String,
    var imageUri: Uri?
) : Parcelable{
    constructor(): this("", "", CurrentType.AC, "", "", "", TeslaCompatible.NO, "", null)
}
