package com.example.camera;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyCamera implements SurfaceHolder.Callback {
	
	private static Camera camera;
	private static SurfaceHolder sholder;
	private static Bitmap img = null;

	private static ShutterCallback shutterCallback;
	private static PictureCallback rawCallback;
	private static PictureCallback jpegCallback ;
	
	/*
	 * 3 callback
	 	ShutterCallback shutterCallback = new ShutterCallback() {
			public void onShutter() {
				//Called as near as possible to the moment when a photo is captured from the sensor. 
				//This is a good opportunity to play a shutter sound or give other feedback of camera operation. 
				//This may be some time after the photo was triggered, but some time BEFORE the actual data is available.
			}
	 	};	
	 	PictureCallback rawCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				//Called when image data is available after a picture is taken. 
				//The format of the data depends on the CONTEXT of the callback and Camera.Parameters settings.
			}
		};
		PictureCallback jpegCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				//Called when image data is available after a picture is taken. 
				//The format of the data depends on the CONTEXT of the callback and Camera.Parameters settings.
			}
		};
	 * 
	 * 
	 * */
	MyCamera( SurfaceView surface, 
			ShutterCallback _shutterCallback,
			PictureCallback _rawCallback,
			PictureCallback _jpegCallback) {
		shutterCallback = _shutterCallback;
		rawCallback = _rawCallback;
		jpegCallback = _jpegCallback;
		//step 1. Obtain an instance of Camera from open(int).
		camera = Camera.open();
		//step 2. Get existing (default) settings with getParameters().
		Camera.Parameters camPar = camera.getParameters();
		//step 3. If necessary, modify the returned Camera.Parameters object and call setParameters(Camera.Parameters).
		camPar.setJpegQuality( 100 ); /*Jpeg, quality = perfect*/
		/*AUTO_EXPOSURE*/
		if(!camPar.isAutoExposureLockSupported()) camPar.setAutoExposureLock( true );
		camPar.setPictureFormat(ImageFormat.JPEG);/*JPEG*/
		camPar.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO); /*Flash Mode*/
		camera.setParameters( camPar );
		//step 4. If desired, call setDisplayOrientation(int).
		//step 5. Important: Pass a fully initialized SurfaceHolder to _
		//setPreviewDisplay(SurfaceHolder). Without a surface, the camera will be unable to start the preview.
		sholder= surface.getHolder();
		sholder.addCallback( this ) ;
	}
	
	public void takePicture() {
		camera.takePicture(shutterCallback, rawCallback, jpegCallback);
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
		//step 6. Important: Call startPreview() to start updating the preview surface.
		//Preview must be started before you can take a picture.
        try {
            if (camera != null) {
            	camera.setPreviewDisplay(holder);
            	camera.startPreview();
            }
        } catch (IOException e) {
        	Log.v("mess",e.getMessage() );
        }
    }
	
	@Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
    }
	
	@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	//This is called immediately after any structural changes (format or size) have been made to the surface.
    }

}
