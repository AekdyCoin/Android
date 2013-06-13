package com.example.voicereg;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class RecordThread  extends Thread {  
    private AudioRecord ar;  
    private int bs;  
    private static int SAMPLE_RATE_IN_HZ = 8000;  
    private static int SAMPLE_GAP = 2 ; // ms
    private static int THRESH_HOLD = 2900 ;

    private boolean isRun = false;  
    private Handler hdlStartVoicesample ;
    
    public RecordThread(Handler hdlStartVoicesample) {  
        super();  
        bs = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,  
                AudioFormat.CHANNEL_CONFIGURATION_MONO,  
                AudioFormat.ENCODING_PCM_16BIT);
        
        ar = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,  
                AudioFormat.CHANNEL_CONFIGURATION_MONO,  
                AudioFormat.ENCODING_PCM_16BIT, bs);  
        
        this.hdlStartVoicesample = hdlStartVoicesample ;
        Log.w("assign_ok", "");
    }
   
    public void run() { 
    	super.run(); 
        ar.startRecording();  
        byte[] buffer = new byte[bs];  
        isRun = true; 
        while ( isRun ) {  
            int r = ar.read(buffer, 0, bs);

            double v = 0;  
            for (int i = 0; i < r; i++) {  
                v += buffer[i] * buffer[i];  
            } 
            
            int value = (int)(v/r);
            Log.w("DB", Integer.toString( value ));
            if( value > THRESH_HOLD) {
            	ar.stop();
            	Message msg = new Message();
            	Log.w("POST", "");
            	hdlStartVoicesample.sendMessage(msg);
            	isRun = false;
            }
            try {
				sleep( SAMPLE_GAP );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        ar.stop();  
    }  
   

    public void pause() {  
                // 在调用本线程的 Activity 的 onPause 里调用，以便 Activity 暂停时释放麦克风  
    	ar.stop();
        isRun = false;  
    }  
   
    public void start() {  
                // 在调用本线程的 Activity 的 onResume 里调用，以便 Activity 恢复后继续获取麦克风输入音量  
        if (!isRun) {  
            super.start();  
        }  
    }  
  
}  
