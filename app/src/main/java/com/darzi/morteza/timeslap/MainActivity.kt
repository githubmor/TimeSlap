package com.darzi.morteza.timeslap

import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import java.io.File


class MainActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the last picture
        val projection = arrayOf(MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.MIME_TYPE)
        val cursor = applicationContext.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")

// Put it in the image view
        if (cursor.moveToFirst()) {
            val imageView = findViewById(R.id.myImageView) as ImageView
            val imageLocation = cursor.getString(1)
            val imageFile = File(imageLocation)
            if (imageFile.exists()) {   // TODO: is there a better way to do this?
                val bm = BitmapFactory.decodeFile(imageLocation)
                imageView.setImageBitmap(bm)
                imageView.rotation = 90F
//                imageView.scaleType =ImageView.ScaleType.CENTER
//                imageView.adjustViewBounds = true

            }
        }
    }


}
