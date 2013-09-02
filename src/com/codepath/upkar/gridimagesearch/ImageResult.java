package com.codepath.upkar.gridimagesearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageResult {
	private final static String tag = "Debug - com.codepath.upkar.gridimagesearch.ImageResult";
	private String fullUrl;
	private String thumbUrl;

	public ImageResult() {
	}

	public ImageResult(JSONObject json) {
		try {
			this.fullUrl = json.getString("url");
			this.thumbUrl = json.getString("tbUrl");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	@Override
	public String toString() {
		return thumbUrl;
	}

	public static List<? extends ImageResult> fromJSONArray(
			JSONArray imageJsonResults) {
		List<ImageResult> result = new ArrayList<ImageResult>();
		for (int i = 0; i < imageJsonResults.length(); i++) {
			try {
				JSONObject imageObject = imageJsonResults.getJSONObject(i);
				ImageResult imageResult = new ImageResult(imageObject);
				result.add(imageResult);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
