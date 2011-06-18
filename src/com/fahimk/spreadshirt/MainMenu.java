package com.fahimk.spreadshirt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupViews();
    }
    
    public void setupViews() {
    	Button cameraButton = (Button) findViewById(R.id.main_button_camera);
    	cameraButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, MediaActivity.class));
			}
		});
    }
}