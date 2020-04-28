package com.mobileapp.imagepickerdemo.imagepicker

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.FileNotFoundException
import java.io.IOException


private const val MIN_WIDTH_QUALITY = 400 // min pixels
private const val MIN_HEIGHT_QUALITY = 400

fun rotate(bitmap: Bitmap?, degrees: Int): Bitmap? {
    var bitmap = bitmap
    if (bitmap != null && degrees != 0) {
        Matrix().let {
            it.postRotate(degrees.toFloat())
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap!!.width, bitmap!!.height, it, true)
        }
    }
    return bitmap
}

fun getRotationFromGallery(context: Context, imageUri: Uri): Int {
    var result = 0
    val columns = arrayOf(MediaStore.Images.Media.ORIENTATION)
    var cursor: Cursor? = null
    try {
        cursor = context.contentResolver.query(imageUri, columns, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val orientationColumnIndex = cursor.getColumnIndex(columns[0])
            result = cursor.getInt(orientationColumnIndex)
        }
    } catch (e: Exception) {
        //Do nothing
    } finally {
        cursor?.close()
    } //End of try-catch block
    return result
}

fun decodeBitmap(context: Context, theUri: Uri): Bitmap? {
    var outputBitmap: Bitmap? = null
    var fileDescriptor: AssetFileDescriptor? = null

    try {
        fileDescriptor = context.contentResolver.openAssetFileDescriptor(theUri, "r")

        // Get size of bitmap file
        val boundsOptions = BitmapFactory.Options()
        boundsOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor, null, boundsOptions)

        // Get desired sample size. Note that these must be powers-of-two.
        val sampleSizes = intArrayOf(8, 4, 2, 1)
        var selectedSampleSize = 1 // 1 by default (original image)

        for (sampleSize in sampleSizes) {
            selectedSampleSize = sampleSize
            val targetWidth = boundsOptions.outWidth / sampleSize
            val targetHeight = boundsOptions.outHeight / sampleSize
            if (targetWidth >= MIN_WIDTH_QUALITY && targetHeight >= MIN_HEIGHT_QUALITY) {
                break
            }
        }

        // Decode bitmap at desired size
        val decodeOptions = BitmapFactory.Options()
        decodeOptions.inSampleSize = selectedSampleSize
        outputBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor, null, decodeOptions)
        fileDescriptor.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return outputBitmap
}


fun Bitmap.modifyOrientationForCamera(imageAbsolutePath: String?): Bitmap? {
    val ei = ExifInterface(imageAbsolutePath)
    return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotate(this, 90)

        ExifInterface.ORIENTATION_ROTATE_180 -> rotate(this, 180)

        ExifInterface.ORIENTATION_ROTATE_270 -> rotate(this, 270)

        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(this, horizontal = true, vertical = false)

        ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(this, horizontal = false, vertical = true)

        else -> this
    }
}

fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap = Matrix().run {
        preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, this, true)
}






