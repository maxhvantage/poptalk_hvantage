package com.ciao.app.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.poptalk.app.R;

public class HelpActivity extends Activity{
	private TextView emailTV,phoneTV;
	
	/*tncTV.setText(Html.fromHtml("<a href='http://www.ciaotelecom.com/'>I agree to the Terms and Conditions.</a>"));
	tncTV.setMovementMethod(LinkMovementMethod.getInstance());*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poptalk_help);
		emailTV = (TextView)findViewById(R.id.tv_email);
		phoneTV= (TextView)findViewById(R.id.tv_call);
		
	}

	public void goToPreviousScreen(View view){
		finish();
	}
}
