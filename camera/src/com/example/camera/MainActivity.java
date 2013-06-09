package com.example.camera;

import java.io.IOException;
import java.util.List;


import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
	
	private static Camera camera;
	private static SurfaceHolder sholder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.activity_main);
		//step 1. Obtain an instance of Camera from open(int).
		camera = Camera.open();
		//step 2. Get existing (default) settings with getParameters().
		Camera.Parameters camPar = camera.getParameters();
		//step 3. If necessary, modify the returned Camera.Parameters object and call setParameters(Camera.Parameters).
		camPar.setJpegQuality( 100 );
		if(!camPar.isAutoExposureLockSupported()) 
			camPar.setAutoExposureLock( true );
		camPar.setPictureFormat(ImageFormat.JPEG);
		camera.setParameters( camPar );
		//step 4. If desired, call setDisplayOrientation(int).
		//step 5. Important: Pass a fully initialized SurfaceHolder to _
		//setPreviewDisplay(SurfaceHolder). Without a surface, the camera will be unable to start the preview.
		sholder= ((SurfaceView) this.findViewById( R.id.surView)) .getHolder();
		sholder.addCallback( this ) ;
		
		//step 6. Important: Call startPreview() to start updating the preview surface.
		//Preview must be started before you can take a picture.
		camera.startPreview();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (camera != null) {
            	camera.setPreviewDisplay(holder);
            }
        } catch (IOException e) {
        	Log.v("mess",e.getMessage() );
        }
    }
	
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (camera != null) {
        	camera.stopPreview();
        }
    }
	
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	
    }
}
