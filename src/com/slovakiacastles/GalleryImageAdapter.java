package com.slovakiacastles;

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

    private Integer[] mImageIds = {
            R.drawable.ukraine_flag,
            R.drawable.poland_flag,
            R.drawable.unitedkingdom_flag
    };

    public GalleryImageAdapter(Context context) 
    {
        mContext = context;
    }

    public int getCount() {
        return mImageIds.length;
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

        i.setImageResource(mImageIds[index]);
        i.setLayoutParams(new Gallery.LayoutParams(500, 500));
    
        i.setScaleType(ScaleType.FIT_XY);

        return i;
    }
}
