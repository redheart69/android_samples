package com.km.noh.testservice;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class UserService extends Service{

	private Timer timer;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("UserService", "onBind service=======>>>>");
		return null;
	}
	@Override
	public void onCreate() {
		Log.d("UserService", "onCreate service=======>>>>");
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("UserService", "onStart service=======>>>>");
		super.onStart(intent, startId);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("UserService", "onStartCommand service=======>>>>");
		TimerTask tt = new TimerTask() {
			
			@Override
			public void run() {
				try{
					checkIfMicrophoneIsBusy(UserService.this);
					}catch(Exception e){
						e.printStackTrace();
					}				
			}
		};
		
		timer = new Timer();
		//TimerTask를 , 0초후에 , 5초 주기로 반복 
		timer.schedule(tt,0, 5000);
		return super.onStartCommand(intent, flags, startId);
	}
	private void onStop() {
		Log.d("UserService", "onStop service=======>>>>");
	}
	@Override
	public void onDestroy() {
		timer.cancel();
		Log.d("UserService", "onDestroy service=======>>>>");
		super.onDestroy();
	}

  public boolean checkIfMicrophoneIsBusy(Context ctx){
	  Log.d("UserService", "checkIfMicrophoneIsBusy=======>>>>");
        AudioRecord audio = null;
        boolean ready = true;
        try{
            int baseSampleRate = 44100;
            int channel = AudioFormat.CHANNEL_IN_MONO;
            int format = AudioFormat.ENCODING_PCM_16BIT;
            int buffSize = AudioRecord.getMinBufferSize(baseSampleRate, channel, format );
            audio = new AudioRecord(MediaRecorder.AudioSource.MIC, baseSampleRate, channel, format, buffSize );
            audio.startRecording();
            short buffer[] = new short[buffSize];
            int audioStatus = audio.read(buffer, 0, buffSize);
            int state = 0;
            switch (audioStatus) {
			case AudioRecord.ERROR_INVALID_OPERATION:
				state = 1;
				break;
			case AudioRecord.STATE_UNINITIALIZED:
				state = 2;
				/* For Android 6.0 */
				break;
			case AudioRecord.ERROR_BAD_VALUE:
				state = 3;
				break;
			case AudioRecord.ERROR:
				state = 4;
				break;
			default:
				state = 5;
				break;
			}
            Log.d("UserService", "checkIfMicrophoneIsBusy=======>>>>audioStatus:"+audioStatus+"/state:"+state);		
        }
        catch(Exception e){
        	e.printStackTrace();
            ready = false;
        }
        finally {
            try{
                audio.release();
            }
            catch(Exception e){
            	e.printStackTrace();
            }
        }

        return ready;
    }
}
