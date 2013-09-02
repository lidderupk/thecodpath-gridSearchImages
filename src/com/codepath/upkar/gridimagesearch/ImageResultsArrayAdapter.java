package com.codepath.upkar.gridimagesearch;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.loopj.android.image.SmartImageView;

public class ImageResultsArrayAdapter extends ArrayAdapter<ImageResult> {
	public ImageResultsArrayAdapter(Context context, List<ImageResult> itemList) {
		super(context, R.layout.item_image_result, itemList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageResult item = this.getItem(position);
		SmartImageView smartImage;
		if (convertView == null) {
			// inflate the xml file to create a new view
			LayoutInflater inflator = LayoutInflater.from(getContext());
			smartImage = (SmartImageView) inflator.inflate(
					R.layout.item_image_result, parent, false);
		} else {
			// use an existing view
			smartImage = (SmartImageView) convertView;
			smartImage.setImageResource(android.R.color.transparent);
		}
		smartImage.setImageUrl(item.getThumbUrl());
		return smartImage;
	}
}
