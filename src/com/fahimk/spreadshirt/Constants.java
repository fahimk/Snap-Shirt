package com.fahimk.spreadshirt;

import java.io.File;

import android.os.Environment;

public class Constants {
	public static String tempImage = Environment.getExternalStorageDirectory() + File.separator + "design.png";
	public static String shirtImage = Environment.getExternalStorageDirectory() + File.separator + "shirt.png";
	
	public static String shopID = "351307"; 
	
	public static final String mensHeavyShirt = "109"; 
    public static final String API_KEY = "40da1e91-e90d-45c8-8f23-44927474425b";
    public static final String SECRET = "c88e6256-3c9b-4572-8bf8-01461dfdd430";

    public static final String DESIGNS_URL = "http://api.spreadshirt.com/api/v1/shops/"+shopID+"/designs";
    public static final String PRODUCT_URL = "http://api.spreadshirt.com/api/v1/shops/"+shopID+"/products";
    public static final String BASKET_URL = "http://api.spreadshirt.com/api/v1/baskets";
    public static final String PREVIEW_URL = "http://image.spreadshirt.com/image-server/image/product/MyProductID/view/1/producttypecolor/MyProductColor/type/png/width/300/height/300";
}
