package com.example.camera;

import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyCamera implements SurfaceHolder.Callback {
	
	private static Camera camera;
	private static SurfaceHolder sholder;

	private static ShutterCallback shutterCallback;
	private static PictureCallback rawCallback;
	private static PictureCallback jpegCallback ;
	
	
	private static double density;      // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）  
	private static double densityDPI;     // 屏幕密度（每寸像素：120/160/240/320）  
	
	private static double xdpi;           
	private static double ydpi;  
	
	private static int screenWidthDip;        // 屏幕宽（dip，如：320dip）  
	private static int screenHeightDip;      // 屏幕宽（dip，如：533dip）  
	  
	private static int screenWidth;      // 屏幕宽（px，如：480px）  
	private static int screenHeight;     // 屏幕高（px，如：800px）
	
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
			PictureCallback _jpegCallback,
			DisplayMetrics _dm) {
		shutterCallback = _shutterCallback;
		rawCallback = _rawCallback;
		jpegCallback = _jpegCallback;
		setScreenSize( _dm );
		
		//step 1. Obtain an instance of Camera from open(int).
		try{
			camera = Camera.open();
		}catch (RuntimeException e) {
			Log.v("SB_CAMERA_RE", "NULL");
			//in use
			camera.release();
			camera = Camera.open();
			if( camera == null ) {
				Log.v("SB_CAMERA_NULL", "NULL");
			}
		}
		//step 2. Get existing (default) settings with getParameters().
		Camera.Parameters camPar = camera.getParameters();
		//step 3. If necessary, modify the returned Camera.Parameters object and call setParameters(Camera.Parameters).
		camPar.setJpegQuality( 100 ); /*Jpeg, quality = perfect*/
		/*AUTO_EXPOSURE*/
		if(!camPar.isAutoExposureLockSupported()) camPar.setAutoExposureLock( true );
		camPar.setPictureFormat( camPar.getSupportedPictureFormats().get( 0 ) );/*Pic that supported by the phone*/
		camPar.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO); /*Flash Mode*/
		Size _sz = getOptimalPictureSize( camPar ) ;
		camPar.setPictureSize(_sz.width, _sz.height);
		_sz = getOptimalPreviewSize( camPar ) ;
		camPar.setPreviewSize(_sz.width, _sz.height);
		camPar.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		camera.setParameters( camPar );
		camera.autoFocus( autocallback );
		
		//step 4. If desired, call setDisplayOrientation(int).
		//step 5. Important: Pass a fully initialized SurfaceHolder to _
		//setPreviewDisplay(SurfaceHolder). Without a surface, the camera will be unable to start the preview.
		sholder= surface.getHolder();
		sholder.addCallback( this ) ;
		
		sholder.setFixedSize(_sz.width, _sz.height);
		sholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}
	
	private boolean SizeBetter( Size a, Size b) {
		return a.width * a.height > b.width * b.height;
	}
	
	private boolean SizeFit( Size a, int width, int height) {
		return (a.width <= width && a.height <= height) ||
				(a.width <= height && a.height <= width) ;
	}
	
	private Size getOptimalPictureSize(Camera.Parameters camPar) {
		List<Size> lst = camPar.getSupportedPictureSizes();
		Size candidate = lst.get(0);
		
		for( Size s : lst ) {
			if( SizeFit(s, screenWidth, screenHeight )) {
				if(!SizeBetter(s, candidate)) {
					candidate = s;
				}
			}
		}
		return candidate;
	}
	
	private Size getOptimalPreviewSize(Camera.Parameters camPar) {
		List<Size> lst = camPar.getSupportedPreviewSizes();
		Size candidate = lst.get(0);
		
		for( Size s : lst ) {
			if( SizeFit(s, screenWidth, screenHeight )) {
				if(SizeBetter(s, candidate)) {
					candidate = s;
				}
			}
		}
		return candidate;
	}
	
	private void setScreenSize(DisplayMetrics dm) {
		density  = dm.density;      // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）  
		densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）  
		
		xdpi = dm.xdpi;           
		ydpi = dm.ydpi;  
		
		screenWidthDip = dm.widthPixels;        // 屏幕宽（dip，如：320dip）  
		screenHeightDip = dm.heightPixels;      // 屏幕宽（dip，如：533dip）  
		  
		screenWidth  = (int)(dm.widthPixels * density + 0.5f);      // 屏幕宽（px，如：480px）  
		screenHeight = (int)(dm.heightPixels * density + 0.5f);     // 屏幕高（px，如：800px）  
//		Log.v("WHB = ", Integer.toString( screenWidth ) + " " + Integer.toString( screenHeight ) + "  "
//			 + Double.toString( (double) screenWidth /screenHeight   )	);
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
		if( camera != null) {
//			camera.stopPreview();
//			camera.release();
		}
    }
	
	@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	//This is called immediately after any structural changes (format or size) have been made to the surface.
    }
	
	Camera.AutoFocusCallback autocallback = new Camera.AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// onAutoFocus
		}
	};
}
