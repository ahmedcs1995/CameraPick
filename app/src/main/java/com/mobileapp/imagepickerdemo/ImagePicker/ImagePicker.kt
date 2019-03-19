package com.mobileapp.imagepickerdemo.ImagePicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



class ImagePicker {

    //request codes
    companion object {
        var REQUEST_IMAGE = 100
        var REQUEST_GALLERY = 300

    }

    var filePath: String? = null
    private val TAG = "ImagePicker"

    fun pickImage(activity: Activity) {
        showPictureDialog(activity, object : PickCallBack {
            override fun act() {
                openGalleryIntent(activity)
            }

        }, object : PickCallBack {
            override fun act() {
                openCameraIntent(activity)
            }

        })
    }




    fun pickImage(fragment: Fragment) {
        showPictureDialog(fragment.context!!, object : PickCallBack {
            override fun act() {
                openGalleryIntent(fragment)
            }

        }, object : PickCallBack {
            override fun act() {
                openCameraIntent(fragment)
            }

        })
    }




    private fun getGalleryIntent(context: Context): Intent? {
        if (PermissionsUtils.checkPermissionForReadExternalStorage(context)) {
            return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        } else {
            Log.d(TAG,"Read permission is denied")
            return null
        }
    }


    private fun openGalleryIntent(activity: Activity) {
        val intent = getGalleryIntent(activity)
        if (intent != null) {
            activity.startActivityForResult(intent, REQUEST_GALLERY)
            return
        }

    }

    private fun openGalleryIntent(fragment: Fragment) {
        val intent = getGalleryIntent(fragment.context!!)
        if (intent != null) {
            fragment.startActivityForResult(intent, REQUEST_GALLERY)
        }
    }

    private fun openCameraIntent(activity: Activity) {
        val intent = getCameraIntent(activity)
        if (intent != null) {
            activity.startActivityForResult(intent, REQUEST_IMAGE)
        }
    }
    private fun openCameraIntent(fragment: Fragment) {
        val intent = getCameraIntent(fragment.context!!)
        if (intent != null) {
            fragment.startActivityForResult(intent, REQUEST_IMAGE)
        }
    }

    fun createImageFile(context: Context): File  {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        filePath = image.absolutePath
        return image
    }

    private fun getCameraIntent(context: Context?): Intent? {
        if (PermissionsUtils.hasCameraAccess(context!!) && PermissionsUtils.checkPermissionForReadExternalStorage(context) && PermissionsUtils.checkPermissionForWriteExternalStorage(context)) {
            val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (pictureIntent.resolveActivity(context.packageManager) != null) {

                var photoFile: File? = null
                try {
                    photoFile = createImageFile(context)
                } catch (e: IOException) {
                    e.printStackTrace()
                    return null
                }

                val photoUri = FileProvider.getUriForFile(context, context.packageName + ".provider", photoFile)
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                return pictureIntent
            }
        }
        Log.d(TAG,"Camera access is not available")
        return null
    }


    private fun showPictureDialog(context: Context, galleryPick: PickCallBack, cameraPick: PickCallBack) {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> galleryPick.act()
                1 -> cameraPick.act()
            }
        }
        pictureDialog.show()
    }

    fun pickImageFromCameraOnly(activity: Activity) {
        openCameraIntent(activity)
    }

    fun pickImageFromCameraOnly(activity: Activity, requestcode: Int) {
        REQUEST_IMAGE = requestcode
        openCameraIntent(activity)
    }

    fun pickImageFromGalleryOnly(activity: Activity, requestcode: Int) {
        REQUEST_GALLERY = requestcode
        openGalleryIntent(activity)
    }

    fun pickImageFromGalleryOnly(activity: Activity) {
        openGalleryIntent(activity)
    }

    fun getBitmapImage(requestCode: Int, activity: Activity,data: Intent?): Bitmap? {
        if (requestCode == REQUEST_IMAGE) {
            try {
                val selectedImage = Uri.fromFile(File(filePath))
                val bm = ImageUtils.decodeBitmap(activity, selectedImage)
                return ImageUtils.modifyOrientationForCamera(bm!!, filePath!!)
            } catch (ex: Exception) {
                Log.e(TAG, ex.message)
                return null
            }

        } else if (requestCode == REQUEST_GALLERY) {
            if(data == null) return null
            val selectedImage = data.data
            val bm = ImageUtils.decodeBitmap(activity, selectedImage)
            val rotation = ImageUtils.getRotationFromGallery(activity, selectedImage)
            return ImageUtils.rotate(bm, rotation)
        }
        return null
    }

}