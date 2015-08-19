package com.ciao.app.views.activities;

import com.poptalk.app.R;
import com.ciao.app.adapters.TutorialPagerAdpater;
import com.ciao.app.views.customviews.viewpagerindicator.CirclePageIndicator;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

//This Activity show the tutorial about the app

public class TutorialActivity extends FragmentActivity{
	private TutorialPagerAdpater mTutorialPagerAdapter;
	private CirclePageIndicator mCirclePageIndicator;
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial_screen);
		mViewPager = (ViewPager) findViewById(R.id.vpPager);
		mTutorialPagerAdapter = new TutorialPagerAdpater(getSupportFragmentManager());
		mCirclePageIndicator = (CirclePageIndicator) findViewById(R.id.vpi_circle);
		mViewPager.setAdapter(mTutorialPagerAdapter);
		mCirclePageIndicator.setViewPager(mViewPager);
		
	}

}
