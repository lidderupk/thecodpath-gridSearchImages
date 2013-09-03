package com.codepath.upkar.gridimagesearch.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.codepath.upkar.gridimagesearch.R;
import com.loopj.android.image.SmartImageView;

public class ImageDisplayActivity extends Activity {

	private static final String tag = "Debug - com.codepath.upkar.gridimagesearch.ImageDisplayActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(tag, "onCreate started");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		String fullUrl = getIntent().getStringExtra("url");
		Log.d(tag, "fullUrl: " + fullUrl);
		SmartImageView smartImage = (SmartImageView) findViewById(R.id.ivSmartImage);
		smartImage.setImageUrl(fullUrl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_display, menu);
		return true;
	}
}
