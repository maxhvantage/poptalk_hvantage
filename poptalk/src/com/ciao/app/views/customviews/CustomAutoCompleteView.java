package com.ciao.app.views.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class CustomAutoCompleteView extends AutoCompleteTextView{

	public CustomAutoCompleteView(Context context) {
		super(context);

	}
	public CustomAutoCompleteView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}
	public CustomAutoCompleteView(Context context, AttributeSet attrs,int defStyleAttr) {
		super(context, attrs, defStyleAttr);

	}
	public CustomAutoCompleteView(Context context, AttributeSet attrs,int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);

	}

	@Override
	protected void performFiltering(final CharSequence text, final int keyCode) {
		String filterText = "";
		super.performFiltering(filterText, keyCode);
	}

}
