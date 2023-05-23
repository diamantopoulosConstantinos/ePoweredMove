package com.kosdiam.epoweredmove.helpers

import android.content.Context
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.location.LocationManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings


class SensorStatus {
    fun isNetOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities: NetworkCapabilities?
        //check android version and get internet connection status
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            capabilities = if(connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)!=null)
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            else null
        }
        else {
            return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.state == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.state == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)?.state == NetworkInfo.State.CONNECTED
        }
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }

    fun isGPSOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return connectivityManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    //check if vibration is enabled in smartphone
    fun messageVibrate(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (am.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> //ring + vibrate
                if (Settings.System.getInt(
                        context.contentResolver,
                        Settings.System.VIBRATE_WHEN_RINGING,
                        0) == 1)
                {
                    performVibration(context, longArrayOf(100, 70, 200, 70))
                }
            AudioManager.RINGER_MODE_VIBRATE -> performVibration(context, longArrayOf(100, 70, 200, 70))
        }
    }

    private fun performVibration(context: Context, msArray: LongArray) {
        val vibrator: Vibrator
        val vibratorManager: VibratorManager
        if (Build.VERSION.SDK_INT>=31) {
            vibratorManager = context.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        }
        else {
            vibrator =  context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createWaveform(msArray,-1))
        }
        else {
            vibrator.vibrate(msArray, -1)
        }
    }
}