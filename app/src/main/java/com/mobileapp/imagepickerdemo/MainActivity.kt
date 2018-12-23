package com.mobileapp.imagepickerdemo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mobileapp.imagepickerdemo.ImagePicker.ImagePicker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    lateinit var imagePick: ImagePicker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imagePick = ImagePicker()

        pick_btn.setOnClickListener {
            imagePick.pickImage(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            //  Bitmap bitmap = ImagePicker.getImageFromResult(getActivity(), requestCode, resultCode, intent);
            val bitmap = imagePick.getBitmapImage(requestCode, this, data!!)
            if (bitmap != null) {
                image_vew.setImageBitmap(bitmap)
            }
        }
    }
}

