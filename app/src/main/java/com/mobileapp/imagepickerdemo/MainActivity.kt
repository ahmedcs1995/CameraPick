package com.mobileapp.imagepickerdemo

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mobileapp.imagepickerdemo.ImagePicker.ImagePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.file.Files.size
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager



class MainActivity : AppCompatActivity() {


    companion object {
        const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 5
    }
    lateinit var imagePick: ImagePicker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ask the permission
        checkOrAskForMultipleRunTimePermission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        imagePick = ImagePicker()
        pick_btn.setOnClickListener {
            imagePick.pickImage(this)
        }
        camera_pick_btn.setOnClickListener {
            imagePick.pickImageFromCameraOnly(this)
        }

        gallery_pick_btn.setOnClickListener {
            imagePick.pickImageFromGalleryOnly(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val bitmap = imagePick.getBitmapImage(requestCode, this, data)
            if (bitmap != null) {
                image_vew.setImageBitmap(bitmap)
            }
        }
    }

    fun checkOrAskForMultipleRunTimePermission(vararg permission: String): Boolean {
        val permissionsNeeded = ArrayList<String>()

        for (getPermission in permission) {
            val askPermission = ActivityCompat.checkSelfPermission(applicationContext,
                    getPermission)

            if (askPermission != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(getPermission)
            }

        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toTypedArray(),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
            return false
        }
        return true

    }
}

