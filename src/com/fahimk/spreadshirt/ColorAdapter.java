package com.fahimk.spreadshirt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;

public class ColorAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;
	private ShirtColor[] shirtColors;

	public ColorAdapter(Context c, ShirtColor[] shirtColors) {
		this.shirtColors = shirtColors;
		mContext = c;
		TypedArray a = c.obtainStyledAttributes(R.styleable.HelloGallery);
		mGalleryItemBackground = a.getResourceId(
				R.styleable.HelloGallery_android_galleryItemBackground, 0);
		a.recycle();
	}

	public int getCount() {
		return shirtColors.length;
	}

	public Object getItem(int position) {
		return shirtColors[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		float dp = mContext.getResources().getDisplayMetrics().density;

		ImageView i = new ImageView(mContext);

		// i.setImageResource(shirtColors[position].drawable);
		ColorDrawable color = new ColorDrawable(Color.parseColor(shirtColors[position].getColor()));
		i.setImageDrawable(color);
		i.setLayoutParams(new GridView.LayoutParams((int) (30 * dp), (int) (30 * dp)));
		i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		i.setAdjustViewBounds(true);
		//i.setBackgroundResource(mGalleryItemBackground);
		

		return i;
	}
}