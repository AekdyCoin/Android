package com.example.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class frmAftCamera extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		setContentView(R.layout.frmaftcamera);
		
		ImageView imgview = (ImageView) this.findViewById( R.id.imgShow ) ;
		
		Bundle bundle = this.getIntent().getExtras();
		 
	     final byte[] data = bundle.getByteArray("imgbyte");
	     Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length) ;
	     Log.v("data_in_size", Integer.toString( data.length) );
	     imgview.setImageBitmap( bmp );
	     
	     View.OnClickListener onclicklistener = new OnClickListener() {
	    	 public void onClick( View v) {
	    		 Intent it = new Intent();
	    		 it.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
	    		 it.setClass(frmAftCamera.this, frmCamera.class);
	    		 finish();
	    		 startActivity( it );
	    	 }
	     };
	     
	     ((ImageButton) this.findViewById( R.id.btnno)).setOnClickListener( onclicklistener );
	     
	     ((ImageButton) this.findViewById( R.id.btnyes)).setOnClickListener( new OnClickListener() {
	    	 public void onClick( View v) {
	    		 Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
	    		 Log.v("SIZE",  Integer.toString( img.getByteCount()>>10 ) + "KB");
	 			 Log.v("X_Y_", Integer.toString( img.getHeight() ) + " " + Integer.toString( img.getWidth()));
	 			 Intent it = new Intent();
//	 			 Bundle bud = new Bundle();
//	 			 bud.putByteArray("imgbyte",data);
//	 			 it.putExtras( bud );
	 			 it.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
	    		 it.setClass(frmAftCamera.this, frmMain.class);
	    		 finish();
	    		 startActivity( it );
	    	 }
	    	 
	     });
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {         
			if(keyCode == KeyEvent.KEYCODE_BACK){
				((ImageButton) this.findViewById( R.id.btnno)).callOnClick();
				return true; 
			}else{
				return super.onKeyDown(keyCode, event);
			}
	}
}
