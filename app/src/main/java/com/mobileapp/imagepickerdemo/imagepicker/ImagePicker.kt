package com.mobileapp.imagepickerdemo.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.util.Log
import com.mobileapp.imagepickerdemo.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ImagePicker {

    //request codes
    companion object {
        private const val TAG = "ImagePicker"
        //options
        private const val GALLERY_OPTION = 0
        private const val CAMERA_OPTION = 1


        private const val DATE_FORMAT = "yyyyMMdd_HHmmss"
    }

    private var filePath: String? = null


    var requestImage = 100
    private set
    var requestGallery = 300
    private set

    fun pickImage(activity: Activity) {
        activity.showPictureDialog({
            openGalleryIntent(activity)
        }, {
            openCameraIntent(activity)
        })
    }


    fun pickImage(fragment: Fragment) {
        fragment.context?.let {
            it.showPictureDialog({
                openGalleryIntent(fragment)
            }, {
                openCameraIntent(fragment)
            })
        }

    }


    private val Context.getGalleryIntent: Intent?
        get() {
            return if (checkPermissionForReadExternalStorage) {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            } else {
                Log.d(TAG, "Read permission is denied")
                null
            }
        }


    private fun openGalleryIntent(activity: Activity) {
        val intent = activity.getGalleryIntent
        if (intent != null) {
            activity.startActivityForResult(intent, requestGallery)
            return
        }

    }

    private fun openGalleryIntent(fragment: Fragment) {
        fragment.context?.let { con ->
            con.getGalleryIntent?.let {
                fragment.startActivityForResult(it, requestGallery)
            }
        }
    }

    private fun openCameraIntent(activity: Activity) {
        activity.getCameraIntent?.let {
            activity.startActivityForResult(it, requestImage)
        }
    }

    private fun openCameraIntent(fragment: Fragment) {
        fragment.context?.let { con ->
            con.getCameraIntent.let {
                fragment.startActivityForResult(it, requestImage)
            }
        }
    }

    private val getImageFileName : String
    get() = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date()).let { "IMG_${it}_"}

    private val Context.createImageFile: File
    get()  {
        return getExternalFilesDir(Environment.DIRECTORY_PICTURES).run {
            val image = File.createTempFile(getImageFileName, ".jpg", this)
            filePath = image.absolutePath
            image
        }

    }

    private val Context.getCameraIntent : Intent?
    get() {
        if (hasCameraAccess && checkPermissionForReadExternalStorage && checkPermissionForWriteExternalStorage) {
            val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (pictureIntent.resolveActivity(packageManager) != null) {

                var photoFile: File?
                try {
                    photoFile = createImageFile
                } catch (e: IOException) {
                    e.printStackTrace()
                    return null
                }

                return FileProvider.getUriForFile(this, "$packageName.provider", photoFile).run {
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this)
                    pictureIntent
                }
            }
        }
        Log.d(TAG, "Camera access is not available")
        return null
    }

    private val Context.getDialogItem: Array<String>
        get() = arrayOf(getString(R.string.gallery_option_text), getString(R.string.camera_option_text))

    private val Context.dialogTitle: String
        get() = getString(R.string.dialog_title)


    private fun Context.showPictureDialog(galleryPick: onImagePick, cameraPick: onImagePick) {
        AlertDialog.Builder(this).apply {
            setTitle(dialogTitle)
            setItems(getDialogItem) { _, which ->
                when (which) {
                    GALLERY_OPTION -> galleryPick()
                    CAMERA_OPTION -> cameraPick()
                }
            }
        }.show()
    }

    fun pickImageFromCameraOnly(activity: Activity) {
        openCameraIntent(activity)
    }

    fun pickImageFromCameraOnly(activity: Activity, requestCode: Int) {
        requestImage = requestCode
        openCameraIntent(activity)
    }

    fun pickImageFromGalleryOnly(activity: Activity, requestCode: Int) {
        requestGallery = requestCode
        openGalleryIntent(activity)
    }

    fun pickImageFromGalleryOnly(activity: Activity) {
        openGalleryIntent(activity)
    }

    fun getBitmapImage(requestCode: Int, activity: Activity, data: Intent?): Bitmap? {
        if (requestCode == requestImage) {
            return try {
                Uri.fromFile(File(filePath)).let { decodeBitmap(activity, it) }?.let { it.modifyOrientationForCamera(filePath) }
            } catch (ex: Exception) {
                Log.e(TAG, ex.message)
                null
            }

        } else if (requestCode == requestGallery) {
            if (data == null) return null
            val selectedImage = data.data
            val bm = decodeBitmap(activity, selectedImage)
            val rotation = getRotationFromGallery(activity, selectedImage)
            return rotate(bm, rotation)
        }
        return null
    }

}