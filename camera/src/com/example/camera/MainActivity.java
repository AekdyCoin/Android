package com.example.camera;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	
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
		camera = new MyCamera( ((SurfaceView) this.findViewById( R.id.surView)) ) ;
		
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

}
