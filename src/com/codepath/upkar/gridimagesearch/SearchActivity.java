package com.codepath.upkar.gridimagesearch;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity {
	private final static String tag = "Debug - com.codepath.upkar.gridimagesearch.SearchActivity";
	EditText etQuery;
	GridView gvResults;
	Button btSearch;

	List<ImageResult> imageResults = new ArrayList<ImageResult>();
	private ImageResultsArrayAdapter itemsAdapter;

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

				Intent intent = new Intent(getApplicationContext(),
						ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				intent.putExtra("url", imageResult.getFullUrl());
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	public void onImageSearch(View v) {
		Log.d("Debug", "button clicked.");

		AsyncHttpClient client = new AsyncHttpClient();
		// https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=barack%20obama
		String query = etQuery.getText().toString();
		client.get(
				"https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="
						+ Uri.encode(query), new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						Log.d("Debug", "on success.");
						JSONArray imageJsonResults = null;
						try {
							JSONObject responseData = response
									.getJSONObject("responseData");
							imageJsonResults = responseData
									.getJSONArray("results");
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
}
