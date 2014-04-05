package com.example.hello;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import java.io.*;
import java.nio.ByteBuffer;


public class Player {
	private boolean isPlaying;
	private final int BUFFSIZE = 8192;
	Activity activity;
	
	private InputStream is;
	private BufferedInputStream bis;
	private byte[] buff;
	
	private AudioTrack track;

    DelayEffect delay;

    public Player(InputStream is) throws IOException {
		isPlaying = false;

		bis = new BufferedInputStream(is);

        // create byte buffer
		buff = new byte[BUFFSIZE];
		
		//setup audio track
		track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, 
							AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
							32000, AudioTrack.MODE_STREAM);

        // set delay to 1sec
        delay = new DelayEffect(88200);
        delay.setDelayTime(44100);

        // set delay parameters
        delay.setWetGain(.7);
        delay.setDryGain(1);
        delay.setFeedbackGain(.3);
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
                double sample;
				for(int i=0; i < BUFFSIZE/2; i++)
                {
                    if (bis.available() > 0)
                        bis.read(buff,i*2, 2);
                    else
                        buff[i*2] = buff[i*2+1] = 0;

                    sample = bytesToSample(buff, i*2);

                    sample = delay.tick(sample);

                    sampleToBytes(sample, buff, i*2);
				}
				
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

    /**
     * Converts 2 bytes from the buffer, starting at the offset,
     * into an audio sample of type double.
     */
    private double bytesToSample(byte[] buff, int offset)
    {
        return ((buff[offset + 0] & 0xFF) | (buff[offset + 1] << 8) ) / 32768.0;
    }

    /**
     * Converts sample of type double into 2 bytes,
     * and stores into the byte buffer starting at the given offset.
     */
    private void sampleToBytes(double sample, byte[] buff, int offset)
    {
        sample = Math.min(1.0, Math.max(-1.0, sample));
        int nsample = (int) Math.round(sample * 32767.0);
        buff[offset + 1] = (byte) ((nsample >> 8) & 0xFF);
        buff[offset + 0] = (byte) (nsample & 0xFF);
    }
}
