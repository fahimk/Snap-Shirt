package com.fahimk.spreadshirt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ShirtTypeActivity extends Activity {

	private MainApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = (MainApplication) getApplication();
		setContentView(R.layout.shirttype);
		setupViews();
	}
	
	private void setupViews() {
		final TextView name = (TextView) findViewById(R.id.shirttype_name);
		final TextView description = (TextView) findViewById(R.id.shirttype_description);
		Button next = (Button) findViewById(R.id.shirtype_next);
		
		final Gallery g = (Gallery) findViewById(R.id.gallery);
		g.setAdapter(new ImageAdapter(this));

		g.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView parent, View v,
					int position, long id) {
				ShirtType st = (ShirtType) (g.getItemAtPosition(position));
				name.setText(st.getName());
				description.setText(st.getDescription());
				
			}

			public void onNothingSelected(AdapterView parent) {
				// TODO Auto-generated method stub

			}
		});
		
		next.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				app.setShirtType(((ShirtType)g.getSelectedItem()));
				startActivity(new Intent(ShirtTypeActivity.this, ShirtDetailsActivity.class));
			}
		});
		
		
	}



}
