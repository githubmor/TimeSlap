package com.darzi.morteza.timeslap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*




class MainActivity : AppCompatActivity()  {
    private var mCamera: Camera? = null
    private var mCameraPreview: CameraPreview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mCamera = getCameraInstance()
        mCameraPreview = CameraPreview(this, mCamera!!)
        val preview = findViewById<View>(R.id.camera_preview) as FrameLayout
        preview.addView(mCameraPreview)

        val captureButton = findViewById<View>(R.id.button_capture) as Button
        captureButton.setOnClickListener{
                mCamera!!.takePicture(null, null, mPicture)
        }

        val imageView = findViewById(R.id.myImageView) as ImageView
        imageView.setImageBitmap(getLastTakenImage())
    }

    private fun getCameraInstance(): Camera? {
        var camera: Camera? = null
        try {
            camera = Camera.open()
        } catch (e: Exception) {
            // cannot get camera or does not exist
        }

        return camera
    }

    var mPicture: PictureCallback = PictureCallback { data, camera ->
        val pictureFile = getOutputMediaFile() ?: return@PictureCallback
        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: FileNotFoundException) {


        } catch (e: IOException) {
        }
    }

    private fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"Camera")
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
        mediaFile = File(mediaStorageDir.getPath() + File.separator
                + timeStamp + ".jpg")

        return mediaFile
    }






    private fun getLastTakenImage():Bitmap? {

        val imageLocation = getLastImageTakenAddressLocation()
            val imageFile = File(imageLocation)
            if (imageFile.exists()) {   // TODO: is there a better way to do this?
                var bm = BitmapFactory.decodeFile(imageLocation)
                try {
                    val exif = ExifInterface(imageFile.getAbsolutePath())
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
        val cursor = applicationContext.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")

        // Put it in the image view
        if (cursor.moveToFirst()) {
            return cursor.getString(1)
        }else
            return null
    }


}
