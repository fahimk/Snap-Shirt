package com.fahimk.spreadshirt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class MediaActivity extends Activity {

	public static final int INTENT_CAMERA = 0;
	Bitmap bmp;
	MainApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (MainApplication) getApplication();
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.tempImage)));
		startActivityForResult(cameraIntent, INTENT_CAMERA); 
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		Log.e("Camera", "Request:" + requestCode + " Result: " + resultCode);
		if (requestCode == INTENT_CAMERA && resultCode == Activity.RESULT_OK) {
			app.setBmp(loadFile(Constants.tempImage));
			startActivity(new Intent(this, ShirtTypeActivity.class));
		}
		this.finish();
	}


	public Bitmap loadFile(String path) {
		int desiredWidth = 400;

		// Get the source image's dimensions
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		int srcWidth = options.outWidth;
		int srcHeight = options.outHeight;

		// Only scale if the source is big enough. This code is just trying to fit a image into a certain width.
		if(desiredWidth > srcWidth)
			desiredWidth = srcWidth;

		// Calculate the correct inSampleSize/scale value. This helps reduce memory use. It should be a power of 2
		// from: http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue/823966#823966
		int inSampleSize = 1;
		while(srcWidth / 2 > desiredWidth){
			srcWidth /= 2;
			srcHeight /= 2;
			inSampleSize *= 2;
		}

		float desiredScale = (float) desiredWidth / srcWidth;

		// Decode with inSampleSize
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inSampleSize = inSampleSize;
		options.inScaled = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(path, options);

		// Resize
		Matrix matrix = new Matrix();
		matrix.postScale(desiredScale, desiredScale);
		Bitmap scaledBitmap = Bitmap.createBitmap(sampledSrcBitmap, 0, 0, sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(), matrix, true);
		sampledSrcBitmap = null;
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(Constants.tempImage);
			scaledBitmap.compress(Bitmap.CompressFormat.PNG, 1, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return scaledBitmap;
	}
}
