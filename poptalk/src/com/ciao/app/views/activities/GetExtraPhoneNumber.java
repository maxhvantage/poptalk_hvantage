package com.ciao.app.views.activities;

import com.poptalk.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class GetExtraPhoneNumber extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_another_ciao_number);
		
	}

	public void goToPreviousScreen(View view){
		finish();
	}
}
