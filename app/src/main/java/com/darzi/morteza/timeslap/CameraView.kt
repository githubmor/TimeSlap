package com.darzi.morteza.timeslap

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException


class CameraPreview// Constructor that obtains context and camera
(context: Context, private val mCamera: Camera) : SurfaceView(context), SurfaceHolder.Callback {
    private val mSurfaceHolder: SurfaceHolder

    init {
        this.mSurfaceHolder = this.holder
        this.mSurfaceHolder.addCallback(this)
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder)
            mCamera.setDisplayOrientation(90)
            mCamera.startPreview()
        } catch (e: IOException) {
            // left blank for now
        }

    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        mCamera.stopPreview()
        mCamera.release()
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, format: Int,
                                width: Int, height: Int) {
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(surfaceHolder)
            mCamera.startPreview()
        } catch (e: Exception) {
            // intentionally left blank for a test
        }

    }
}