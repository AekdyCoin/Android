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


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class VoiceSample implements Runnable{
	private String path ;
	private MediaRecorder  recorder;
	private static final int MAX_BUF_SIZE = 100000;
	private byte[] buffer = new byte[ MAX_BUF_SIZE];
	private int buffer_n ;
	
	private static final int MAX_REC_SIZE = 5;
	private Handler hdl;
	public static final String VOICE_RECOGNITION_RESULTS = "voice_recognition_results";
	public static final String ACTION_POST_RTT_DATA = "action_post_rtt_data";
	
	VoiceSample(String path, Handler hdlRegFinish) {
		this.path = path;
		this.hdl = hdlRegFinish;
	}
	
	public void run() {
		
		recorder  =   new   MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB );
		recorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB) ;
		
		recorder.setOutputFile(path);

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
		MainActivity_timer ct = new MainActivity_timer( 2500, 100) ;
		ct.start();
	}
	
	private void postData() {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream( new File(path) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}

		buffer_n = 0;
		int c;
		try {
			while((c=fin.read()) != -1) {
				buffer[ buffer_n ++ ] = (byte)c;
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
	           
	            out.write(buffer, 0 , buffer_n ) ;

	            out.flush();
	            out.close();
	            BufferedReader br = new BufferedReader(new InputStreamReader(con
	                    .getInputStream()));
	            String line = "";
	            for (line = br.readLine(); line != null; line = br.readLine());
	            Message msg = new Message();
	            Bundle bd = new Bundle();
	            bd.putString("action", ACTION_POST_RTT_DATA);
	            bd.putString( VOICE_RECOGNITION_RESULTS , line) ;
				msg.setData( bd );
	            hdl.sendMessage(msg);
	            
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
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
			postData();
		}
		
		@Override
		public void onTick(long msuntilfinished) {
		}
	}
}
