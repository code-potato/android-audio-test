package com.example.hello;

import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Recorder {

	public static void record(View view) {
		Log.d("rec", "Recorder Called");
		Button button  = (Button)view;
	}
}
