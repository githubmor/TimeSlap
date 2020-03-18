package com.darzi.morteza.timeslap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.camerakit.CameraKitView
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity()  {
    private var cameraKitView: CameraKitView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraKitView = findViewById(R.id.camera)

        val captureButton = findViewById<View>(R.id.button_capture) as Button

        captureButton.setOnClickListener {
            cameraKitView!!.captureImage(CameraKitView.ImageCallback { cameraKitView, capturedImage ->
                val savedPhoto = getOutputMediaFile()
                try {
                    val outputStream = FileOutputStream(savedPhoto!!.path)
                    outputStream.write(capturedImage)
                    outputStream.close()
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                }
            })
        }

        val imageView = findViewById<ImageView>(R.id.myImageView)
        imageView.setImageBitmap(getLastTakenImage())
    }

    override fun onStart() {
        super.onStart()
        cameraKitView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        cameraKitView!!.onResume()
    }

    override fun onPause() {
        cameraKitView!!.onPause()
        super.onPause()
    }

    override fun onStop() {
        cameraKitView!!.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"Camera")
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory")
                return null
            }
        }
        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(Date())
        val mediaFile: File
        mediaFile = File(mediaStorageDir.path + File.separator
                + timeStamp + ".jpg")

        return mediaFile
    }






    private fun getLastTakenImage():Bitmap? {

        val imageLocation = getLastImageTakenAddressLocation()
            val imageFile = File(imageLocation)
            if (imageFile.exists()) {   // TODO: is there a better way to do this?
                var bm = BitmapFactory.decodeFile(imageLocation)
                try {
                    val exif = ExifInterface(imageFile.absolutePath)
                    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
                    Log.d("EXIF", "Exif: $orientation")
                    val matrix = Matrix()
                    if (orientation == 6) {
                        matrix.postRotate(90F)
                    } else if (orientation == 3) {
                        matrix.postRotate(180F)
                    } else if (orientation == 8) {
                        matrix.postRotate(270F)
                    }
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true) // rotating bitmap
                } catch (e: Exception) {

                }
                return bm
            }
        return null
    }

    private fun getLastImageTakenAddressLocation(): String? {
        val projection = arrayOf(MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.MIME_TYPE)
        val cursor = applicationContext.contentResolver
                .query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")

        // Put it in the image view
        if (cursor.moveToFirst()) {
            return cursor.getString(1)
        }else
            return null
    }


}
