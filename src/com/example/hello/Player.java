package com.example.hello;

import android.app.Activity;
import android.content.res.*;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.util.Log;
import java.io.*;


public class Player {
	private boolean isPlaying;
	private final int BUFFSIZE = 1024;
	Activity activity;
	
	private InputStream is;
	private BufferedInputStream bis;
	private DataInputStream dis;
	private byte[] buff;
	
	private AudioTrack track;
	
	
	public Player(AssetFileDescriptor descriptor) throws IOException {
		isPlaying = false;
		
		// setup input stream from given file
		is = new FileInputStream(descriptor.getFileDescriptor());
		bis = new BufferedInputStream(is);
		dis = new DataInputStream(bis); //has to do with endien stuff
		
		// crate byte buffer
		buff = new byte[BUFFSIZE];
		
		//setup audio track
		track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, 
							AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, 
							8000, AudioTrack.MODE_STREAM);
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
		
		while(isPlaying){
			try {
				//fill buffer with bytes from file reader
				for(int i=0; i < BUFFSIZE; i++)
					buff[i] = dis.readByte();
				
				// future effect chain goes here
				
				//write buffer to track to play
				track.write(buff, 0, BUFFSIZE);
				
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
