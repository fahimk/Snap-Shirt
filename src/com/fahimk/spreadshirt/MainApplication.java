package com.fahimk.spreadshirt;

import android.app.Application;
import android.graphics.Bitmap;

public class MainApplication extends Application {
	
	final static int S = 2;
	final static int M = 3;
	final static int L = 4;
	final static int XL = 5;
	final static int XXL = 6;
	
	
	private Bitmap bmp;
	private ShirtType shirtType;
	private ShirtColor shirtColor;
	private int shirtSize;
	
	
	
	public ShirtColor getShirtColor() {
		return shirtColor;
	}

	public void setShirtColor(ShirtColor shirtColor) {
		this.shirtColor = shirtColor;
	}

	public int getShirtSize() {
		return shirtSize;
	}

	public void setShirtSize(int shirtSize) {
		this.shirtSize = shirtSize;
	}

	public ShirtType getShirtType() {
		return shirtType;
	}

	public void setShirtType(ShirtType shirtType) {
		this.shirtType = shirtType;
	}

	public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}
	
	
}
