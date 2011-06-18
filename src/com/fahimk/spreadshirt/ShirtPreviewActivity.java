package com.fahimk.spreadshirt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;


public class ShirtPreviewActivity extends Activity {

	private static final String SPRD_AUTH = "SprdAuth";
	public static final String API_KEY = "apiKey";
	public static final String SIG = "sig";
	public static final String DATA = "data";
	public static final String TIME = "time";
	public static final String SESSION_ID = "sessionId";

	private Handler myHandler = new Handler();

	private MainApplication app;
	private Bitmap bmp;

	int dpi;
	int width;
	int height;

	private ImageView mPinchImageView;

	private ProgressDialog progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progress = new ProgressDialog(this);
		progress.setTitle("Loading");
		progress.setMessage("submitting data to spreadshirt.com ...");
		progress.show();
		app = (MainApplication) getApplication();
		bmp = app.getBmp();
		dpi = bmp.getDensity();
		width = bmp.getWidth();
		height = bmp.getHeight();
		setContentView(R.layout.shirtpreview);
		//mPinchImageView = (ImageView) findViewById(R.id.shirtpreview_image);

		new Thread() {
			public void run() {
				try {
					String[] info = createDesign();
					String message = info[0];
					String location = info[1];
					uploadImage(message);
					String id = location.substring(location.lastIndexOf("/") + 1);
					String productUrl = uploadProduct(id);
					String previewUrl = Constants.PREVIEW_URL;
					previewUrl = previewUrl.replace("MyProductID", productUrl.substring(productUrl.lastIndexOf("/")+1));
					previewUrl = previewUrl.replace("MyProductColor", app.getShirtColor().getAppearanceID() + "");
					Log.e("preview", previewUrl);
					final Bitmap b = getBitmapFromURL(previewUrl);
					Log.e("bwidth", b.getWidth() + "");
					String basket = createBasket();
					uploadBasketItem(basket, productUrl);
					final String checkoutUrl = getCheckoutUrl(basket);
					FileOutputStream out = new FileOutputStream(Constants.shirtImage);
					b.compress(Bitmap.CompressFormat.PNG, 1, out);
					myHandler.post(new Runnable() {
						public void run() {
							if(progress != null && progress.isShowing()) {
								progress.dismiss();
							}
							/* Using WebView to display the full-screen image */
							WebView full = (WebView)findViewById(R.id.shirtpreview_image);

							full.getSettings().setJavaScriptEnabled(true);   
							full.getSettings().setSupportZoom(true);		 
							full.getSettings().setBuiltInZoomControls(true);

							full.setFocusable(true);

							/* Create a new Html that contains the full-screen image */
							String html = new String();
							html = ("<html><center&gt;<img src=\""+Constants.shirtImage+"\"></html>" );

							/* Finally, display the content using WebView */
							full.loadDataWithBaseURL("file:///sdcard/data/data/com.fahimk.spreadshirt/",
									html,
									"text/html",
									"utf-8",
							"");
							Log.e("done", "done");
							Button checkout = (Button) findViewById(R.id.shirtpreview_checkout);
							checkout.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(checkoutUrl));
									startActivity(browserIntent);
								}
							});
							checkout.setVisibility(View.VISIBLE);
						}
					});
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(new InputStreamFix(input));
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String[] createDesign() throws Exception {
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

		return new String[] {str.toString(), location};
	}

	private String getCheckoutUrl(String basketId) throws Exception {
		String url = Constants.BASKET_URL + "/" + basketId + "/checkout";
		HttpGet httpget = new HttpGet(url);
		final HttpClient httpclient = new DefaultHttpClient();
		httpget.setHeader("Authorization", getAuth("GET", url));
		HttpResponse response = httpclient.execute(httpget);

		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder str = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null)
		{
			str.append(line);
		}
		in.close();
		String html = str.toString();
		String key = "xlink:href=\"";
		html = html.substring(html.indexOf(key) + key.length());
		html = html.substring(0, html.indexOf("\""));
		Log.e("response", html);
		return html;
	}

	private void uploadBasketItem(String basket, String productUrl) throws Exception {
		String productId = productUrl.substring(productUrl.lastIndexOf("/")+1);
		String url = Constants.BASKET_URL +  "/" + basket + "/items";

		HttpPost httppost = new HttpPost(url);
		final HttpClient httpclient = new DefaultHttpClient();

		String item = fileToString("basketitem.xml");
		item = item.replaceAll("producturl", productUrl);
		item = item.replaceAll("productid", productId);
		item = item.replace("appearanceID", app.getShirtColor().getAppearanceID() + "");
		item = item.replace("shirtSize", app.getShirtSize() + "");

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
		Log.e("width, height", (width * 25.4 / dpi) + " " + (height * 25.4 / dpi) + "");
		file = file.replace("offsetX", 115.0 + "");
		file = file.replace("offsetY", 140.0 + "");
		file = file.replace("productTypeID", app.getShirtType().typeId + "");
		file = file.replace("appearanceID", app.getShirtColor().getAppearanceID() + "");
		file = file.replace("printTypeID", app.getShirtColor().getPrintType() + "");
		file = file.replace("printAreaID", ((app.getShirtType().typeId == 221) ? 359 : 174) + "");
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
			Log.e("reading bytes", "here");
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
