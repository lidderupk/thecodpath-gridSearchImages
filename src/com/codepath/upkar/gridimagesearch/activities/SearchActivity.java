package com.codepath.upkar.gridimagesearch.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.upkar.gridimagesearch.R;
import com.codepath.upkar.gridimagesearch.models.ImageResult;
import com.codepath.upkar.gridimagesearch.models.ImageResultsArrayAdapter;
import com.codepath.upkar.gridimagesearch.util.Constants;
import com.codepath.upkar.gridimagesearch.util.ImageSizeEnum;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity {
	private final static String tag = "Debug - com.codepath.upkar.gridimagesearch.SearchActivity";
	EditText etQuery;
	GridView gvResults;
	Button btSearch;

	List<ImageResult> imageResults = new ArrayList<ImageResult>();
	private ImageResultsArrayAdapter itemsAdapter;

	private String queryImageColor;
	private String querySiteFilter;
	private int queryImageSize;
	private String queryImageType;
	private Button btPrevious;
	private Button btNext;

	private int startImageIndex = 0;
	private final int maxImagesInResult = 8;
	private AsyncHttpClient client;
	private String searchText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		client = new AsyncHttpClient();
	}

	private void setupViews() {
		btSearch = (Button) findViewById(R.id.btnSearch);
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvResults = (GridView) findViewById(R.id.gvResults);
		btPrevious = (Button) findViewById(R.id.btPrevious);
		btNext = (Button) findViewById(R.id.btNext);
		resetButtonsAndStartIndex();

		itemsAdapter = new ImageResultsArrayAdapter(getBaseContext(),
				imageResults);
		gvResults.setAdapter(itemsAdapter);

		gvResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View parent,
					int position, long rowId) {
				Intent intent = new Intent(
						SearchActivity.this.getBaseContext(),
						ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				intent.putExtra("url", imageResult.getFullUrl());

				startActivity(intent);
			}
		});

		btPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBtPreviousClick();
			}
		});

		btNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBtNextClick();
			}
		});

		etQuery.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// no op
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// no op
			}

			@Override
			public void afterTextChanged(Editable s) {
				// disable navigation buttons
				if (s.toString() == null || s.toString().length() < 1) {
					resetButtonsAndStartIndex();
				}
			}
		});
	}

	private void onBtNextClick() {
		startImageIndex += maxImagesInResult;
		final String queryString = createQueryString(searchText,
				startImageIndex);
		asyncGetImages(client, queryString);
		btPrevious.setEnabled(true);
		Log.d(tag, "startImageIndex: " + startImageIndex);
	}

	private void onBtPreviousClick() {
		startImageIndex -= maxImagesInResult;

		if (startImageIndex <= 0) {
			startImageIndex = 0;
			btPrevious.setEnabled(false);
		}

		final String queryString = createQueryString(searchText,
				startImageIndex);
		asyncGetImages(client, queryString);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.PREFS_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				queryImageColor = data
						.getStringExtra(Constants.IMAGE_COLOR_DATA);
				querySiteFilter = data
						.getStringExtra(Constants.IMAGE_SITE_FILTER);
				queryImageSize = data.getIntExtra(Constants.IMAGE_SIZE_DATA, 0);
				queryImageType = data.getStringExtra(Constants.IMAGE_TYPE_DATA);

				String logOutput = "imageColor: " + queryImageColor
						+ " siteFilter: " + querySiteFilter + " imageSize: "
						+ queryImageSize + " imageType:" + queryImageType;
				Log.d(tag, logOutput);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_display, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			Intent in = new Intent(getBaseContext(), PrefActivity.class);
			startActivityForResult(in, Constants.PREFS_REQUEST_CODE);
			break;
		default:
			break;
		}
		return true;
	}

	public void onImageSearch(View v) {
		searchText = etQuery.getText().toString();

		if (searchText == null || searchText.length() < 1) { 

			Toast.makeText(getBaseContext(), "Enter text to search",
					Toast.LENGTH_SHORT).show();
			resetButtonsAndStartIndex();
			return;
		}

		resetButtonsAndStartIndex();

		final String queryString = createQueryString(searchText, 0);
		asyncGetImages(client, queryString);
	}

	private void resetButtonsAndStartIndex() {
		btPrevious.setEnabled(false);
		btNext.setEnabled(false);
		startImageIndex = 0;
	}

	private void asyncGetImages(AsyncHttpClient client, final String queryString) {
		client.get(queryString, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject response) {

				JSONArray imageJsonResults = null;
				JSONObject responseData = null;
				try {
					responseData = response.getJSONObject("responseData");
					imageJsonResults = responseData.getJSONArray("results");
					imageResults.clear();
					itemsAdapter.clear();

					List<? extends ImageResult> fromJSONArray = ImageResult
							.fromJSONArray(imageJsonResults);
					itemsAdapter.addAll(fromJSONArray);
					btNext.setEnabled(true);

				} catch (JSONException e) {
					e.printStackTrace();

					if (responseData == null || responseData.length() < 1) {
						btNext.setEnabled(false);
						Toast.makeText(getBaseContext(), "No more results :(",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.d("Debug", "request failed.");
				Log.d("Debug", arg0.getMessage());
			}
		});
	}

	private String createQueryString(String searchText, int startIndex) {
		StringBuffer result = new StringBuffer(
				"https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz="
						+ maxImagesInResult + "&start=" + startImageIndex
						+ "&q=" + Uri.encode(searchText));
		if (queryImageColor != null && queryImageColor.length() > 0)
			result.append("&imgcolor=" + queryImageColor);

		String imageSize = "icon";
		switch (queryImageSize) {
		case 0:
			imageSize = ImageSizeEnum.ICON.getValue();
			break;
		case 1:
			imageSize = ImageSizeEnum.MEDIUM.getValue();
			break;
		case 2:
			imageSize = ImageSizeEnum.XXLARGE.getValue();
			break;
		case 3:
			imageSize = ImageSizeEnum.HUGE.getValue();
			break;
		default:
			break;
		}

		result.append("&imgsz=" + imageSize);

		if (queryImageColor != null && queryImageColor.length() > 0)
			result.append("&imgcolor=" + queryImageColor);

		if (querySiteFilter != null && querySiteFilter.length() > 0)
			result.append("&as_sitesearch=" + querySiteFilter);

		if (queryImageType != null && queryImageType.length() > 0)
			result.append("&imgtype=" + queryImageType);

		// Log.d(tag, "search query: " + result.toString());
		return result.toString();
	}
}
