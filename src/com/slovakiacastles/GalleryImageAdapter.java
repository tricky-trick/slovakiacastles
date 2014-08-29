package com.slovakiacastles;

import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

@SuppressWarnings("deprecation")
public class GalleryImageAdapter extends BaseAdapter 
{
    private Context mContext;

    private LinkedList<Integer> mImageIds;

    public GalleryImageAdapter(Context context, LinkedList<Integer> images) 
    {
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
    @SuppressLint("InlinedApi")
	public View getView(int index, View view, ViewGroup viewGroup) 
    {
        // TODO Auto-generated method stub
        ImageView i = new ImageView(mContext);

        i.setImageResource(mImageIds.get(index));
        //i.setLayoutParams(new Gallery.LayoutParams(600, 400));
    
        i.setScaleType(ScaleType.FIT_XY);

        return i;
    }
}
