package com.example.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class frmMain extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView( R.layout.activity_main ) ;
		((Button)findViewById( R.id.btn_go_cam)).setOnClickListener( new Button.OnClickListener(){
			public void onClick( View v) {
				Intent it = new Intent();
				it.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				it.setClass(frmMain.this, frmCamera.class);
				finish();
				frmMain.this.startActivity( it );
			}
		}		
		);
		
	}
	
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {         
			if(keyCode == KeyEvent.KEYCODE_BACK){
				

				Dialog dialog=new AlertDialog.Builder(frmMain.this)
				.setTitle("程序退出？")
				.setMessage("您要退出吗")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setNegativeButton("取消", null)
						.create();
				dialog.show();
				
				return true; 
			}else{
				return super.onKeyDown(keyCode, event);
			}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
	}
}
