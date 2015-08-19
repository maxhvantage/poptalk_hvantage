package com.ciao.app.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GenderSpinnerAdapter extends ArrayAdapter<String> {
	Typeface font = Typeface.createFromAsset(getContext().getAssets(),"fonts/OpenSans-Regular.ttf");

	// (In reality I used a manager which caches the Typeface objects)
	// Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);

	public GenderSpinnerAdapter(Context context, int resource, List<String> items) {
		super(context, resource, items);
	}

	// Affects default (closed) state of the spinner
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getView(position, convertView, parent);
		view.setTypeface(font);
		view.setTextSize(15);
		return view;
	}

	// Affects opened state of the spinner
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getDropDownView(position, convertView, parent);
		view.setTypeface(font);
		view.setTextSize(15);
		
		return view;
	}
}
