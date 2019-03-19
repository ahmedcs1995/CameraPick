package com.mobileapp.imagepickerdemo.ImagePicker

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {

        val MIN_WIDTH_QUALITY = 400 // min pixels
        val MIN_HEIGHT_QUALITY = 400

        fun rotate(bitmap: Bitmap?, degrees: Int): Bitmap? {
            var bitmap = bitmap
            if (bitmap != null && degrees != 0) {
                val matrix = Matrix()
                matrix.postRotate(degrees.toFloat())
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
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
                BitmapFactory.decodeFileDescriptor(fileDescriptor!!.fileDescriptor, null, boundsOptions)

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


        fun modifyOrientationForCamera(bitmap: Bitmap, image_absolute_path: String): Bitmap? {
            val ei = ExifInterface(image_absolute_path)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            Log.e("orientation", "" + orientation)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return rotate(bitmap, 90)

                ExifInterface.ORIENTATION_ROTATE_180 -> return rotate(bitmap, 180)

                ExifInterface.ORIENTATION_ROTATE_270 -> return rotate(bitmap, 270)

                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> return flip(bitmap, true, false)

                ExifInterface.ORIENTATION_FLIP_VERTICAL -> return flip(bitmap, false, true)

                else -> return bitmap
            }
        }

        fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
            val matrix = Matrix()
            matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }


    }


