package com.example.hello;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Player player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			
			// get file from asset folder
			AssetFileDescriptor filedes = getAssets().openFd("emma16.wav");
			// instantiate player object
			player = new Player(filedes);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d("play", "file not found");
		} catch (IOException e) { 
			Log.d("play",e.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// when button is clicked
	public void onClick(View view) {
		//get button object
		Button button = (Button)view;
		
		// if song is currently playing, pause music, and change text to 'play'
		if(player.isPlaying()) {
			button.setText("Play");
			player.pause();
		}
		
		// if song is not playing, play music, and change text to 'pause'
		else {
			button.setText("Pause");
			// playing song must be done in new thread, or app will hang until song is over
			new Thread(new Runnable() {
				public void run() {
					player.play();
				}
			}).start();
		}
	}
}
