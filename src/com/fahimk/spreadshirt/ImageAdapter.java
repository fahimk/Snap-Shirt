package com.fahimk.spreadshirt;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;

    private ShirtType[] shirtTypes = {
            new ShirtType(18311100, 109, "Men's Heavyweight T-Shirt", R.drawable.mensheavy, "The seamless rib collar with double-needle cover-stitching, shoulder to shoulder taping, double-needle sleeve and bottom hem make it a durable wear. Made from 100% cotton with a fabric weight of 6.1 oz.", HardCodedColors.mens),
            new ShirtType(18331061, 221, "Women's Heavyweight T-Shirt", R.drawable.womensheavy, "With plenty of extra room this shirt is ideal for all body types and is double-needle stitched for extra durability. The fabric is made from 100% pre-shrunk cotton and has a fabric weight of 6.1 oz.", HardCodedColors.womens)
    };

    public ImageAdapter(Context c) {
        mContext = c;
        TypedArray a = c.obtainStyledAttributes(R.styleable.HelloGallery);
        mGalleryItemBackground = a.getResourceId(
                R.styleable.HelloGallery_android_galleryItemBackground, 0);
        a.recycle();
    }

    public int getCount() {
        return shirtTypes.length;
    }

    public Object getItem(int position) {
        return shirtTypes[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	float dp = mContext.getResources().getDisplayMetrics().density;
    	
        ImageView i = new ImageView(mContext);

        i.setImageResource(shirtTypes[position].drawable);
        i.setLayoutParams(new Gallery.LayoutParams((int) (200 * dp), (int) (200 * dp)));
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setBackgroundResource(mGalleryItemBackground);

        return i;
    }
}
