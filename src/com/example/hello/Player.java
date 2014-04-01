package com.example.hello;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.*;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.PresetReverb;
import android.os.Build;
import android.util.Log;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;


public class Player {
	private boolean isPlaying;
	private final int BUFFSIZE = 8192;
	Activity activity;
	
	private InputStream is;
	private BufferedInputStream bis;
	private DataInputStream dis;
	private byte[] byteBuff;
//	private short[] shortBuff;
	
	private AudioTrack track;

    DelayLine delayl;
    DelayEffect delay;

    public Player(AssetFileDescriptor descriptor) throws IOException {
		isPlaying = false;
		
		// setup input stream from given file
		is = new FileInputStream(descriptor.getFileDescriptor());
		bis = new BufferedInputStream(is);
		dis = new DataInputStream(bis); //has to do with endien stuff
		
		// crate byte buffer
		byteBuff = new byte[BUFFSIZE];
//		shortBuff = new short[BUFFSIZE];
		
		//setup audio track
		track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, 
							AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
							32000, AudioTrack.MODE_STREAM);

        delayl = new DelayLine(88200);
        delayl.setDelayLineDelay(44100);
        delay = new DelayEffect(88200);
        delay.setDelayTime(44100);

        delay.setWetGain(1);
        delay.setDryGain(1);
        delay.setFeedbackGain(1);
	}
	
	public boolean isPlaying(){
		return isPlaying;
	}
	
	public void play() {
		Log.d("player", "play");
		
		//set to true
		isPlaying = true;
		
		//tell track to be ready to play audio
		track.play();

		ByteBuffer bb = ByteBuffer.allocate(8);
		while(isPlaying){
			try {
				//fill buffer with bytes from file reader
				for(int i=0; i < BUFFSIZE/8; i++){
                    //read float from input and save to byte buffer
					bb.putDouble(0, delay.tick(dis.readDouble()));
					bb.rewind();
                    // future effect chain goes here
					bb.get(byteBuff,i*8,8);
				}
				
				//write buffer to track to play
 				track.write(byteBuff, 0, BUFFSIZE);
//				track.write(shortBuff, 0, BUFFSIZE);
				
			} catch (IOException e) {
				break; //when eof is reached
			}
		}
	}
	
	public void pause() {
		Log.d("player", "pause");
		isPlaying = false;
	}
	
}
