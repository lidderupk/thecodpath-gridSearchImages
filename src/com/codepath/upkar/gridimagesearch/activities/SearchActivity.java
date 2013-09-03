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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.upkar.gridimagesearch.R;
import com.codepath.upkar.gridimagesearch.models.ImageResult;
import com.codepath.upkar.gridimagesearch.util.Constants;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
	}

	private void setupViews() {
		btSearch = (Button) findViewById(R.id.btnSearch);
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvResults = (GridView) findViewById(R.id.gvResults);

		itemsAdapter = new ImageResultsArrayAdapter(getBaseContext(),
				imageResults);
		gvResults.setAdapter(itemsAdapter);

		gvResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View parent,
					int position, long rowId) {
				Log.d(tag, "item clicked !");

				Intent intent = new Intent(
						SearchActivity.this.getBaseContext(),
						ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				intent.putExtra("url", imageResult.getFullUrl());

				startActivity(intent);
			}
		});
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
		Log.d("Debug", "button clicked.");

		AsyncHttpClient client = new AsyncHttpClient();
		// https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=barack%20obama
		String searchText = etQuery.getText().toString();

		if (searchText == null || searchText.length() < 1) {

			Toast.makeText(getBaseContext(), "Enter text to search",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String queryString = createQueryString(searchText);

		client.get(queryString, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				Log.d("Debug", "on success.");
				JSONArray imageJsonResults = null;
				try {
					JSONObject responseData = response
							.getJSONObject("responseData");
					imageJsonResults = responseData.getJSONArray("results");
					imageResults.clear();
					List<? extends ImageResult> fromJSONArray = ImageResult
							.fromJSONArray(imageJsonResults);
					imageResults.addAll(fromJSONArray);
					Log.d(tag, imageResults.toString());
					itemsAdapter.addAll(fromJSONArray);

					// itemsAdapter.notify();

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.d("Debug", "request failed.");
				Log.d("Debug", arg0.getMessage());
			}
		});
	}

	private String createQueryString(String searchText) {
		StringBuffer result = new StringBuffer(
				"https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="
						+ Uri.encode(searchText));
		if (queryImageColor != null && queryImageColor.length() > 0)
			result.append("&imgcolor=" + queryImageColor);

		String imageSize = "icon";
		switch (queryImageSize) {
		case 0:
			imageSize = "icon";
			break;
		case 1:
			imageSize = "small";
			break;
		case 2:
			imageSize = "xxlarge";
			break;
		default:
			break;
		}

		result.append("&imgsz=" + imageSize);

		if (queryImageColor != null && queryImageColor.length() > 0)
			result.append("&imgcolor=" + queryImageColor);

		if (querySiteFilter != null && querySiteFilter.length() > 0)
			result.append("&as_sitesearch=" + querySiteFilter);

		Log.d(tag, "search query: " + result.toString());
		return result.toString();
	}
}
