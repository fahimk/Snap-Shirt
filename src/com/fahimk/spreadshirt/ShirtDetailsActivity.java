package com.fahimk.spreadshirt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ShirtDetailsActivity extends Activity {

	private MainApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (MainApplication) getApplication();
		setContentView(R.layout.shirtdetails);
		setupViews();
	}
	
	private void setupViews() {
		final GridView g = (GridView) findViewById(R.id.shirtdetails_colors);
		final ImageView color = (ImageView) findViewById(R.id.shirtdetails_color_swatch);
		final TextView colorName = (TextView) findViewById(R.id.shirtdetails_color_name);
		Button next = (Button) findViewById(R.id.shirtdetails_next);

		
		setupRadios();
		
		g.setAdapter(new ColorAdapter(this, app.getShirtType().colors));

		ShirtColor st = (ShirtColor) (g.getItemAtPosition(0));
		color.setImageDrawable(new ColorDrawable(Color.parseColor(st.getColor())));
		colorName.setText(st.getName());
		app.setShirtColor(st);
		
		g.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v,
					int position, long id) {
				ShirtColor st = (ShirtColor) (g.getItemAtPosition(position));
				color.setImageDrawable(new ColorDrawable(Color.parseColor(st.getColor())));
				colorName.setText(st.getName());
				app.setShirtColor(st);
			}
		});
		
		
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(ShirtDetailsActivity.this, ImageEditActivity.class));
			}
		});
	}

	private void setupRadios() {
		RadioButton radioS = (RadioButton) findViewById(R.id.size_s);
		RadioButton radioM = (RadioButton) findViewById(R.id.size_m);
		RadioButton radioL = (RadioButton) findViewById(R.id.size_l);
		RadioButton radioXL = (RadioButton) findViewById(R.id.size_xl);
		
		radioS.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				app.setShirtSize(MainApplication.S);
			}
		});
		radioM.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				app.setShirtSize(MainApplication.M);
			}
		});
		radioL.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				app.setShirtSize(MainApplication.L);
			}
		});
		radioXL.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				app.setShirtSize(MainApplication.XL);
			}
		});
		
	}

	
}
