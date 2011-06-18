package com.fahimk.spreadshirt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

public class CreateProductActivity extends Activity {
	private static final String SPRD_AUTH = "SprdAuth";
	public static final String API_KEY = "apiKey";
	public static final String SIG = "sig";
	public static final String DATA = "data";
	public static final String TIME = "time";
	public static final String SESSION_ID = "sessionId";

	MainApplication app;
	Bitmap bmp;

	int dpi;
	int width;
	int height;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (MainApplication) getApplication();
		bmp = app.getBmp();
		
		dpi = bmp.getDensity();
		width = bmp.getWidth();
		height = bmp.getHeight();
		
		try {
			createDesign();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private void createDesign() throws Exception {
		String url = Constants.DESIGNS_URL;
		HttpPost httppost = new HttpPost(url);
		final HttpClient httpclient = new DefaultHttpClient();

		StringEntity entity = new StringEntity(fileToString("design.xml"), "UTF-8");
		httppost.setEntity(entity); 

		httppost.addHeader("Authorization", getAuth("POST", url));
		httppost.addHeader("Content-Type", "application/xml");


		HttpResponse response = httpclient.execute(httppost);
		//printResponse(response);
		final String location = response.getFirstHeader("Location").getValue();

		Log.e("location", location);

		HttpGet httpget = new HttpGet(location);
		response = httpclient.execute(httpget);

		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder str = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null) {
			str.append(line);
		}
		in.close();

		final String message = str.toString();

		new Thread() {
			public void run() {
				try {
					uploadImage(message);
					String id = location.substring(location.lastIndexOf("/") + 1);
					String productUrl = uploadProduct(id);
					String basket = createBasket();
					uploadBasketItem(basket, productUrl);
					getCheckoutUrl(basket);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private void getCheckoutUrl(String basketId) throws Exception {
		String url = Constants.BASKET_URL + "/" + basketId + "/checkout";
		HttpGet httpget = new HttpGet(url);
		final HttpClient httpclient = new DefaultHttpClient();
		httpget.setHeader("Authorization", getAuth("GET", url));
		HttpResponse response = httpclient.execute(httpget);
		printResponse(response);
	}
	
	private void uploadBasketItem(String basket, String productUrl) throws Exception {
		String productId = productUrl.substring(productUrl.lastIndexOf("/")+1);
		String url = Constants.BASKET_URL +  "/" + basket + "/items";
		
		HttpPost httppost = new HttpPost(url);
		final HttpClient httpclient = new DefaultHttpClient();

		String item = fileToString("basketitem.xml");
		item = item.replaceAll("producturl", productUrl);
		item = item.replaceAll("productid", productId);
		
		httppost.setEntity(new StringEntity(item, "UTF-8")); 

		httppost.setHeader("Authorization", getAuth("POST", url));
		httppost.setHeader("Content-Type", "application/xml");
		HttpResponse response = httpclient.execute(httppost);
		
		printGZIPResponse(response);
	}
	
	private String createBasket() throws Exception {

		String url = Constants.BASKET_URL;
		HttpPost httppost = new HttpPost(url);
		final HttpClient httpclient = new DefaultHttpClient();

		StringEntity entity = new StringEntity(fileToString("basket.xml"), "UTF-8");
		httppost.setEntity(entity); 

		httppost.setHeader("Authorization", getAuth("POST", url));
		httppost.setHeader("Content-Type", "application/xml");
		HttpResponse response = httpclient.execute(httppost);
		
		String basketUrl = response.getFirstHeader("Location").getValue();
		String basketId = basketUrl.substring(basketUrl.lastIndexOf("/"));

		return basketId;
	}
	
	protected String uploadProduct(String id) throws Exception {
		String url = Constants.PRODUCT_URL;
		HttpPost httppost = new HttpPost(url);
		final HttpClient httpclient = new DefaultHttpClient();

		String file = fileToString("product.xml");
		file = file.replace("printColorIds=\"1\"", "printColorIds=\"1\" designId=\"" + id + "\"");
		file = file.replace("imageWidth", (width * 25.4 / dpi) + "");
		file = file.replace("imageHeight", (height * 25.4 / dpi) + "");
		file = file.replace("offsetX", 60.0 + "");
		file = file.replace("offsetY", 70.0 + "");
		StringEntity entity = new StringEntity(file, "UTF-8");
		httppost.setEntity(entity); 

		httppost.addHeader("Authorization", getAuth("POST", url));
		httppost.addHeader("Content-Type", "application/xml");

		HttpResponse response = httpclient.execute(httppost);
		
		printGZIPResponse(response);
		return response.getFirstHeader("Location").getValue();
	}

	protected void uploadImage(String message) throws Exception {
		int bytesRead, bytesAvailable, bufferSize;
	    byte[] buffer;
	    int maxBufferSize = 1*1024*1024;
	    
		String searchString = "resource xlink:href=\"";
		int index = message.indexOf(searchString);
		final String uploadUrl = message.substring(index + searchString.length(),
				message.indexOf("\"", index + searchString.length() + 1));
		
	    FileInputStream fileInputStream = new FileInputStream(new File(Constants.tempImage) );

	    Log.e("upload url", uploadUrl);
	    URL url = new URL(uploadUrl + "?method=PUT");
	    
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	    // Allow Inputs & Outputs
	    connection.setDoInput(true);
	    connection.setDoOutput(true);
	    connection.setUseCaches(false);
	    
	    // Enable POST method
	    connection.setRequestMethod("PUT");

	    connection.setRequestProperty("Connection", "Keep-Alive");
	    connection.setRequestProperty("Content-Type",  "img/png");
	    connection.setRequestProperty("Authorization", getAuth("PUT", uploadUrl));
	    
	    DataOutputStream outputStream = new DataOutputStream( connection.getOutputStream() );
	    bytesAvailable = fileInputStream.available();
	    bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    buffer = new byte[bufferSize];

	    // Read file
	    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

	    while (bytesRead > 0)
	    {
	        outputStream.write(buffer, 0, bufferSize);
	        bytesAvailable = fileInputStream.available();
	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
	        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	    }
	     int serverResponseCode = connection.getResponseCode();
	     if(serverResponseCode != 200) {
	    	 this.finish();
	     }
	     String serverResponseMessage = connection.getResponseMessage();
	     Log.e("ServerCode",""+serverResponseCode);
	     Log.e("serverResponseMessage",""+serverResponseMessage);
	    fileInputStream.close();
	    outputStream.flush();
	    outputStream.close();
	}


	private String getAuth(String method, String url) {
		String time = Long.toString(System.currentTimeMillis());
		String data = calculateData(method, url, time);
		String sprdAuth = SPRD_AUTH + " " + API_KEY + "=\"" + Constants.API_KEY + "\", " +
		DATA + "=\"" + data + "\", " +
		SIG + "=\"" + calculateSignature(data) + "\"";
		return sprdAuth;
	}
	
	private String getContent (String Url) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(Url);

		HttpResponse response = client.execute(request);

		String html = "";
		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder str = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null)
		{
			str.append(line);
		}
		in.close();
		return str.toString();
	}

	private String printResponse(HttpResponse response) throws Exception {
		
		for(Header h : response.getAllHeaders()) {
			Log.e("header", h.getName() + " " + h.getValue());
		}
		
		
		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder str = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null)
		{
			str.append(line);
		}
		in.close();
		Log.e("response", str.toString());
		return str.toString();
	}
	
	private String printGZIPResponse(HttpResponse response) throws Exception {

		for(Header h : response.getAllHeaders()) {
			Log.e("header", h.getName() + " " + h.getValue());
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(response.getEntity().getContent()), "ISO-8859-1"));
		String sResponse;

		StringBuilder s = new StringBuilder();
		while ((sResponse = reader.readLine()) != null) {
			s = s.append(sResponse);
		}
		Log.e("response", s.toString() + "hi");
		return s.toString();
	}

	private String calculateSignature(String data) {
		return SHA1.getHashAsHex(data + " " + Constants.SECRET);
	}

	private String calculateData(String method, String theUrl, String time) {
		return method + " " + ((theUrl.indexOf('?') != -1)
				? theUrl.substring(0, theUrl.indexOf('?')) : theUrl) + " " + time;
	}

	private String fileToString(String filePath)
	throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		InputStream raw = getAssets().open(filePath);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(raw, "UTF8"));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

}
