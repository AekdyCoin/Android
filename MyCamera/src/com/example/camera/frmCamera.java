package com.example.camera;


import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageButton;

public class frmCamera extends Activity {
	
	private static MyCamera camera ;
	private static Bitmap img ;
	private static ImageButton btn_cap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		setContentView(R.layout.frmcamera);
		
		DisplayMetrics dm = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(dm);  
		  	
		camera = new MyCamera(((SurfaceView) findViewById( R.id.surView)) , 
				shutterCallback,
				rawCallback,
				jpegCallback,
				dm) ;
		
		Button.OnClickListener onclicklistener = new Button.OnClickListener() {
			public void onClick(View v) {
					camera.takePicture();
			}
		};
		btn_cap = (ImageButton) findViewById( R.id.btnno) ;
		btn_cap.setOnClickListener( 
				onclicklistener
		);
		
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			/*Called as near as possible to the moment when a photo is captured from the sensor. 
			 * This is a good opportunity to play a shutter sound or give other feedback of camera operation. 
			 * This may be some time after the photo was triggered, but some time BEFORE the actual data is available.
			*/
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			/*Called when image data is available after a picture is taken. 
			 * The format of the data depends on the CONTEXT of the callback and Camera.Parameters settings.
			*/
		}
	};
	
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			/*Called when image data is available after a picture is taken. 
			 * The format of the data depends on the CONTEXT of the callback and Camera.Parameters settings.
			*/
			//step 8*. After taking a picture, preview display will have stopped.
			//To take more photos, call startPreview() again first.
			//step 9. Call stopPreview() to stop updating the preview surface.
			
			//step 10. Important: Call release() to release the camera for use by other applications.
			//Applications should release the camera immediately in onPause() (and re-open() it in onResume()).
			img = BitmapFactory.decodeByteArray(data, 0, data.length);
//			Log.v("SIZE",  Integer.toString( img.getByteCount()>>10 ) + "KB");
//			Log.v("X_Y_", Integer.toString( img.getHeight() ) + " " + Integer.toString( img.getWidth()));
			// post to an_other activity
			
			Intent it = new Intent(frmCamera.this,  frmAftCamera.class);
			
			Bundle bundle = new Bundle();
			
			bundle.putByteArray("imgbyte", data);
			it.putExtras( bundle );
			it.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//			Log.v("data_size", Integer.toString(data.length) );
			finish();
			startActivity( it ) ;
			
			if (camera != null) {
				/*
				 * un-comment the following code to frozen the pic.
				 * */
//				camera.stopPreview();
//				camera.release();
				camera.startPreview();
			}
			
		}
	};
	
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {         
			if(keyCode == KeyEvent.KEYCODE_BACK){
				Intent it = new Intent();
	    		it.setClass(frmCamera.this, frmMain.class);
	    		finish();
	    		startActivity( it );
				return true; 
			}else{
				return super.onKeyDown(keyCode, event);
			}
	}
}
