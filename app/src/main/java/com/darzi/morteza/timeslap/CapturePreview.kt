package com.darzi.morteza.timeslap

import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView


class CapturePreview(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), SurfaceHolder.Callback {
    internal var holder: SurfaceHolder
    internal lateinit var mCamera : Camera
    init {
        holder = getHolder()
        holder.addCallback(this)
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

            val parameters = mCamera.getParameters()
            parameters.getSupportedPreviewSizes()
            mCamera.setParameters(parameters)
            mCamera.startPreview()

    }

    override fun surfaceCreated(holder: SurfaceHolder) {

        try {
            mCamera = Camera.open()
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mCamera.stopPreview()
        mCamera.release()
    }

    companion object {

//        var mBitmap: Bitmap
//lateinit var mCamera: Camera
        /***
         *
         * Take a picture and and convert it from bytes[] to Bitmap.
         *
         */
//        fun takeAPicture() {
//
//            val mPictureCallback = object : Camera.PictureCallback {
//                override fun onPictureTaken(data: ByteArray, camera: Camera) {
//
//                    val options = BitmapFactory.Options()
//                    mBitmap = BitmapFactory.decodeByteArray(data, 0, data.size, options)
//                }
//            }
//            mCamera.takePicture(null, null, mPictureCallback)
//        }
    }
}