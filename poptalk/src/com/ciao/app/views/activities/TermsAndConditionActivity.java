package com.ciao.app.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.poptalk.app.R;

//This activity show the TNC page of the app
public class TermsAndConditionActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tnc);
		WebView wv= (WebView)findViewById(R.id.webView1);
		wv.loadUrl("file:///android_asset/termsofservice.html");
		wv.getSettings().setJavaScriptEnabled(true);
	}

	public void goToPreviousScreen(View view){
		finish();
	}

}
