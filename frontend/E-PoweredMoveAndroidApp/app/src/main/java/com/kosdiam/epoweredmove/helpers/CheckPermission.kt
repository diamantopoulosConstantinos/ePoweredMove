package com.kosdiam.epoweredmove.helpers

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.kosdiam.epoweredmove.R

class CheckPermission {
    fun hasGPSPermission(activity: FragmentActivity, requestCode: Int = activity.resources.getInteger(R.integer.gps_permission_code)): Boolean{
        //check if user has permissions for gps tracking
        if(ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //if he doesn't have, request permissions
            ActivityCompat.requestPermissions(activity, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION), requestCode)
            //if user granted permissions
            return ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
        //if he has already granted permissions
        return true
    }

    fun hasInternalStorageReadPermission(activity: FragmentActivity, requestCode: Int = activity.resources.getInteger(R.integer.storage_permission_read_code)): Boolean{
        //check if user has permissions for internal storage reading
        if(ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //if he doesn't have, request permissions
            ActivityCompat.requestPermissions(activity, arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
            //if user granted permissions
            return ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        //if he has already granted permissions
        return true
    }

    fun hasInternalStorageWritePermission(activity: FragmentActivity, requestCode: Int = activity.resources.getInteger(R.integer.storage_permission_write_code)): Boolean{
        //check if user has permissions for internal storage reading
        if(ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //if he doesn't have, request permissions
            ActivityCompat.requestPermissions(activity, arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
            //if user granted permissions
            return ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        //if he has already granted permissions
        return true
    }

    fun hasCameraPermission(activity: FragmentActivity, requestCode: Int = activity.resources.getInteger(R.integer.camera_permission_code)): Boolean{
        //check if user has permissions for internal storage reading
        if(ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //if he doesn't have, request permissions
            ActivityCompat.requestPermissions(activity, arrayOf(
                android.Manifest.permission.CAMERA), requestCode)
            //if user granted permissions
            return ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        }
        //if he has already granted permissions
        return true
    }
}