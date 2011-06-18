package com.fahimk.spreadshirt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ImageEditActivity extends Activity {

	private MainApplication app;
	private Bitmap bmp;
	private Bitmap original;
	public ImageView image;
	int currentRotate = 0;

	private GestureDetector mGestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (MainApplication) getApplication();
		setContentView(R.layout.imageedit);
		setupViews();
		
		
	}

	private void setupViews() {
		image = (ImageView) findViewById(R.id.image_rotate);
		original = app.getBmp();
		bmp = original;
		image.setImageBitmap(original);

		Button rotateLeft = (Button) findViewById(R.id.rotate_left);
		Button rotateRight = (Button) findViewById(R.id.rotate_right);
		Button next = (Button) findViewById(R.id.imageedit_next);

		next.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Button b = (Button) v;
				b.setEnabled(false);
				app.setBmp(bmp);
				Log.e("width, height", bmp.getWidth() + " " + bmp.getHeight());
				FileOutputStream out;
				try {
					Matrix m = new Matrix();
					float scale = 1000.0f / bmp.getWidth();
					m.postScale(scale, scale);
					bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
					Log.e("width, height", bmp.getWidth() + " " + bmp.getHeight());
					out = new FileOutputStream(Constants.tempImage);
					bmp.compress(Bitmap.CompressFormat.PNG, 1, out);
					app.setBmp(bmp);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				startActivity(new Intent(ImageEditActivity.this, ShirtPreviewActivity.class));
			}
		});

		rotateLeft.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				rotate(currentRotate-10);
			}
		});

		rotateRight.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				rotate(currentRotate+10);
			}
		});
	}

	protected void rotate(int i) {
		currentRotate = i;
		Matrix m = new Matrix();
		m.postRotate(i);
		bmp = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), m, false);
		image.setImageBitmap(bmp);
	}


}

