package com.mobileapp.imagepickerdemo.imagepicker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat

val Context.checkPermissionForReadExternalStorage
get() = checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

val Context.checkPermissionForWriteExternalStorage
get() = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

val Context.hasCameraAccess
get() = checkPermission(Manifest.permission.CAMERA)

private fun Context.checkPermission(permission : String) = ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED



