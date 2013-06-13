package com.example.voicereg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VoiceRegActivity extends Activity{

	// UI
	private TextView txt;
	private MediaRecorder  recorder;
	private ListView lstshow;
	
	//handler
	private Handler hdlNetstate = new Handler() {
		   @Override
		   public void handleMessage(Message msg) {
		      txt.setText( msg.getData().getCharSequence("net_state"));  
		      super.handleMessage(msg);  
		   }  
		  }; 
	private Handler hdlRegFinish = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		      // voice reg done.
		      String res = 
		    		  msg.getData().getString( VoiceSample.VOICE_RECOGNITION_RESULTS ) ;
		      ArrayList<String> lst = Parse(res) ;
		      lstshow.setAdapter( new ArrayAdapter<String>
		      	(VoiceRegActivity.this, android.R.layout.simple_list_item_1, lst )
		      	) ;
		      for(String s : lst) {
		    	  Log.w("ss = ", s );
		      }
		      Log.w("finally I get the result", res );
		   }  
		  }; 
	
    //count down
	private MainActivity_timer ct = new MainActivity_timer( 2500, 100) ;
	private CountDownTimer ct_delay = new CountDownTimer( 200, 50) {

     public void onTick(long millisUntilFinished) {
     }

     public void onFinish() {
     }
  };
  
	//data
	private static final int MAX_BUF_SIZE = 100000;
	private byte[] buffer = new byte[ MAX_BUF_SIZE];
	private int buffer_n ;
	
	private static final int MAX_REC_SIZE = 3;
	private static int ON_RECORDING = 0;
	
	private void setTitleLayout() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_voice_reg);
		
		txt = ( TextView) this.findViewById( R.id.txtShowState ) ;
		
		ON_RECORDING = 0;
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork() // or
																		// .detectAll()
																		// for
																		// all
																		// detectable
																		// problems
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
				.penaltyLog() // 打印logcat
				.penaltyDeath().build());
		
		RelativeLayout rel = (RelativeLayout)this.findViewById( R.id.layout );
		rel.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if( ON_RECORDING  != 0 ) {
					
				}else {
					ON_RECORDING = 1 ;
					RecordThread rc = new RecordThread( );
					rc.start();
				}
				return false;
			}
		}
	  );
		lstshow = (ListView) this.findViewById( R.id.lstshow );
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitleLayout();
		//one new thread to check the netstate
		NetworkState net = new NetworkState();
		new Thread( net).start();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private boolean isConnect() {
		Context context = this.getApplicationContext();
		
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {

					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return false;
	}

	protected class NetworkState implements Runnable {
		public void run() {
			CharSequence[] res = {"NetWork Disconnected!", "Network Connected!"};
			Bundle bd = null;
			while( true) {
				bd = new Bundle();
				if( isConnect() ) {
					bd.putCharSequence("net_state", res[1]); 
				}else {
					bd.putCharSequence("net_state", res[0]);
				}
				Message msg = new Message();
				msg.setData( bd );
				hdlNetstate.sendMessage(msg);
			}
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public String appPath() {
		return getApplicationContext().getFilesDir().getAbsolutePath();
	}
	public String amrSource(boolean absolute) {
		if( absolute)
			return appPath() + File.separator + "tmp.amr";
		return "tmp.amr";
	}

	private class postData implements Runnable {
		public void run() {
			FileInputStream fin = null;
			try {
				fin = new FileInputStream( new File( amrSource(true ) ) );
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ;
			}

			buffer_n = 0;
			try {
				int c;
				while((c=fin.read()) != -1) {
					buffer[ buffer_n ++] = (byte) c ;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ;
			}
			
			String url = "http://www.google.com/speech-api/v1/recognize?xjerr=1&client=chromium&lang=zh-CN&maxresults=" +
			 Integer.toString(MAX_REC_SIZE); 

			 try {
		            URL _url = new URL( url );

		            HttpURLConnection con = (HttpURLConnection) _url.openConnection();
		            con.setDoOutput(true);
		            con.setRequestMethod("POST") ;
		            con.addRequestProperty("Content-Type", "audio/amr; rate=8000");

		            DataOutputStream out = new DataOutputStream(con
		                    .getOutputStream());
		           
		            Log.w("dataLen", Integer.toString( buffer_n) );
		            
		            out.write(buffer, 0 , buffer_n ) ;
		            
		            out.flush();
		            out.close();
		            BufferedReader br = new BufferedReader(new InputStreamReader(con
		                    .getInputStream()));
		            String line = "", res = "";
		            for (line = br.readLine(); line != null; line = br.readLine())
		            	res = line;
		            line = res;
		            Message msg = new Message();
		            Bundle bd = new Bundle();
		            bd.putString("action", VoiceSample.ACTION_POST_RTT_DATA);
		            bd.putString( VoiceSample.VOICE_RECOGNITION_RESULTS , line) ;
					msg.setData( bd );
					hdlRegFinish.sendMessage(msg);
		            
		        } catch (MalformedURLException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			 
			 ON_RECORDING = 0;
		}
	}
	
	protected class RecordThread  extends Thread {  	    
	    public RecordThread() { 
	    }

	    public void run() { 
	    	super.run(); 
	    	ct_delay.start();
	    	recorder  =   new   MediaRecorder();
		    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setOutputFile(amrSource(true ));

			try {
				recorder.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			recorder.start();

        	Log.w("begin_sample_now", "please speak");
        	
			ct.start();
	    }   
	}  
	
	private class MainActivity_timer extends CountDownTimer {
		public MainActivity_timer( long st_time, long interval) {
			super( st_time, interval );
		}
		
		@Override
		public void onFinish() {
			recorder.stop();
			recorder.release();
			postData p = new postData();
//			new Thread( p) .start();
			p.run();
		}
		
		@Override
		public void onTick(long msuntilfinished) {
		}
	}
	
	private static ArrayList<String> Parse( String s) {
		ArrayList<String> lst = new ArrayList<String>();
		
		String buf = "";
		boolean key = false;
		
		for(int i=0;i<s.length(); ++ i) {
			if(s.charAt(i) == '\"') {
				buf = "";
				int j = i + 1;
				for(; j < s.length(); ++ j) {
					if(s.charAt( j ) == '\"' ) break;
					buf += s.charAt( j );
				}
				i = j ;
				if( buf .compareTo("utterance") == 0) {
					key = true ;
				}else {
					if(key) {
						lst.add( buf ) ;
					}
					key = false;
				}
			}
		}
		return lst;
	}
}
