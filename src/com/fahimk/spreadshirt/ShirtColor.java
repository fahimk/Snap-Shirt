package com.fahimk.spreadshirt;

import android.graphics.Color;

public class ShirtColor {
	String name;
	String color;
	int printType;
	int appearanceID;
	
	public ShirtColor(String name, String color, int printType, int appearanceID) {
		super();
		this.name = name;
		this.color = color;
		this.printType = printType;
		this.appearanceID = appearanceID;
	}
	
	public int getAppearanceID() {
		return appearanceID;
	}


	public void setAppearanceID(int appearanceID) {
		this.appearanceID = appearanceID;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getPrintType() {
		return printType;
	}
	public void setPrintType(int printType) {
		this.printType = printType;
	}
	
	
	
}
