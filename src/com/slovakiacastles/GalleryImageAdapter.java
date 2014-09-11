package com.slovakiacastles;

import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class GalleryImageAdapter extends BaseAdapter {
	private Context mContext;

	private LinkedList<Integer> mImageIds;

	public GalleryImageAdapter(Context context, LinkedList<Integer> images) {
		mContext = context;
		mImageIds = images;
	}

	public int getCount() {
		return mImageIds.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	// Override this method according to your need
	@SuppressLint({ "InlinedApi", "NewApi" })
	public View getView(int index, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ImageView i = new ImageView(mContext);

		i.setImageResource(mImageIds.get(index));
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		int height = metrics.heightPixels;
		//int width = metrics.widthPixels;
		if (height <= 800) {
			i.setLayoutParams(new Gallery.LayoutParams(500, 300));
		} else if (height > 800 && height <= 1300) {
			i.setLayoutParams(new Gallery.LayoutParams(700, 500));
		} else {
			i.setLayoutParams(new Gallery.LayoutParams(1000, 800));
		}

		// i.setScaleType(ScaleType.CENTER);
		// i.setPadding(0, 0, 0, 0);
		// i.setCropToPadding(true);

		return i;
	}
}
