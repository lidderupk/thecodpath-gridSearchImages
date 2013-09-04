package com.codepath.upkar.gridimagesearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.upkar.gridimagesearch.R;
import com.codepath.upkar.gridimagesearch.util.Constants;
import com.codepath.upkar.gridimagesearch.util.ImageSizeEnum;

public class PrefActivity extends Activity {

	private Spinner spImageColor;
	private SeekBar sbImageSize;
	private Button btDone;
	private static final String tag = "Debug - com.codepath.upkar.gridimagesearch.PrefActivity";
	private EditText etSiteFilter;
	private Spinner spImageTypeFilter;
	private SharedPreferences mPrefs;
	private TextView tvSizeLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pref);

		setupViews();
	}

	private void setupViews() {
		spImageColor = (Spinner) findViewById(R.id.spImageColor);
		sbImageSize = (SeekBar) findViewById(R.id.sbSizeBar);
		etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
		spImageTypeFilter = (Spinner) findViewById(R.id.spImageTypeFilter);
		tvSizeLabel = (TextView) findViewById(R.id.tvSizeLabel);

		setTvSizeLabelText(tvSizeLabel);

		btDone = (Button) findViewById(R.id.btDone);
		// final Activity activity = this;
		btDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra(Constants.IMAGE_COLOR_DATA, spImageColor
						.getSelectedItem().toString());
				i.putExtra(Constants.IMAGE_SITE_FILTER, etSiteFilter.getText()
						.toString());
				i.putExtra(Constants.IMAGE_SIZE_DATA, sbImageSize.getProgress());
				i.putExtra(Constants.IMAGE_TYPE_DATA, spImageTypeFilter
						.getSelectedItem().toString());
				setResult(Activity.RESULT_OK, i);
				finish();
			}
		});
		
		sbImageSize.setOnSeekBarChangeListener(new  OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// no op
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// no op
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				setTvSizeLabelText(PrefActivity.this.tvSizeLabel);
			}
		});
	}

	private void setTvSizeLabelText(TextView tvSizeLabel) {
		switch (sbImageSize.getProgress()) {
		case 0:
			tvSizeLabel.setText(ImageSizeEnum.ICON.getValue());
			break;
		case 1:
			tvSizeLabel.setText(ImageSizeEnum.MEDIUM.getValue());
			break;
		case 2:
			tvSizeLabel.setText(ImageSizeEnum.XXLARGE.getValue());
			break;
		case 3:
			tvSizeLabel.setText(ImageSizeEnum.HUGE.getValue());
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pref, menu);
		return true;
	}

	@Override
	protected void onPause() {
		Log.d(tag, "onPause");

		if (mPrefs == null)
			Log.d(tag, "mPrefs is NULL");
		else
			Log.d(tag, "mPrefs is NOT NULL");

		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putString(Constants.IMAGE_COLOR_DATA, spImageColor.getSelectedItem()
				.toString());
		ed.putString(Constants.IMAGE_SITE_FILTER, etSiteFilter.getText()
				.toString());
		ed.putString(Constants.IMAGE_TYPE_DATA, spImageTypeFilter
				.getSelectedItem().toString());
		ed.putInt(Constants.IMAGE_SIZE_DATA, sbImageSize.getProgress());

		ed.commit();
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.d(tag, "onResume");
		mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
		int imageSize = mPrefs.getInt(Constants.IMAGE_SIZE_DATA, 0);
		sbImageSize.setProgress(imageSize);

		String imageColor = mPrefs.getString(Constants.IMAGE_COLOR_DATA, "red");
		ArrayAdapter<String> myAdap = (ArrayAdapter<String>) spImageColor
				.getAdapter();
		int spinnerPosition = myAdap.getPosition(imageColor);
		spImageColor.setSelection(spinnerPosition);

		String imageSiteFilter = mPrefs.getString(Constants.IMAGE_SITE_FILTER,
				"");
		etSiteFilter.setText(imageSiteFilter);

		String imageTypeDate = mPrefs.getString(Constants.IMAGE_TYPE_DATA, "");
		ArrayAdapter<String> spImageTypeAdap = (ArrayAdapter<String>) spImageTypeFilter
				.getAdapter();
		spinnerPosition = spImageTypeAdap.getPosition(imageTypeDate);
		spImageTypeFilter.setSelection(spinnerPosition);

		super.onResume();
	}
}
