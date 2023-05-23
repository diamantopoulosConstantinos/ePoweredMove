package com.kosdiam.epoweredmove.models.interfaces

import android.content.Context

interface IEnumName {
    val stringResource: Int

    fun getString(context: Context): String{
        return context.getString(stringResource)
    }
}