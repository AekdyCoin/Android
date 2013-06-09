package com.example.camera;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageButton;

public class CamMainActivity extends Activity {
	
	private static MyCamera camera ;
	private static Bitmap img ;
	private static ImageButton btn_cap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);/*隐藏标题*/
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);/*全屏，去掉状态栏*/
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);/*保持屏幕亮，别才开几秒就关了*/
		
		setContentView(R.layout.activity_main);
		
		
		camera = new MyCamera(((SurfaceView) this.findViewById( R.id.surView)) , 
				shutterCallback,
				rawCallback,
				jpegCallback) ;
		
		Button.OnClickListener onclicklistener = new Button.OnClickListener() {
			public void onClick(View v) {
					camera.takePicture();
			}
		};
		btn_cap = (ImageButton) this.findViewById( R.id.btn_cap) ;
		btn_cap.setOnClickListener( 
				onclicklistener
		);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			Log.v("SIZE",  Integer.toString( img.getByteCount()>>10 ) + "KB");
			Log.v("X_Y_", Integer.toString( img.getHeight() ) + " " + Integer.toString( img.getWidth()));
			if (camera != null) {
				/*
				 * un-comment the following code to frozen the pic.
				 * */
//					camera.stopPreview();
//					camera.release();
				camera.startPreview();
			}
		}
	};
}
