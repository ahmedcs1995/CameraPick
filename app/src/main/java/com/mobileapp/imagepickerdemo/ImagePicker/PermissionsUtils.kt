package com.mobileapp.imagepickerdemo.ImagePicker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object PermissionsUtils {
    fun checkPermissionForReadExternalStorage(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }


    fun checkPermissionForWriteExternalStorage(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    fun hasCameraAccess(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = context.checkSelfPermission(Manifest.permission.CAMERA)
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }
}


