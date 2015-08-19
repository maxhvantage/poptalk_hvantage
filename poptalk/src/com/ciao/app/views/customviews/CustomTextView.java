package com.ciao.app.views.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.poptalk.app.R;

/**
 * Created by rajat on 2/3/15.
 */
public class CustomTextView extends TextView {

	public CustomTextView(Context context) {
		super(context);
		setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Regular.ttf"));   
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		
	}

	public CustomTextView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		Typeface myTypeface;
		if (attrs!=null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyTextView);
			String fontName = a.getString(R.styleable.MyTextView_fontName);
			if(fontName==null){
				myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
				setTypeface(myTypeface);	
			}else{
				if (fontName.equalsIgnoreCase("bold")) {
					myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Bold.ttf");
					setTypeface(myTypeface);
				}else if (fontName.equalsIgnoreCase("italic")) {
					myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Italic.ttf");
					setTypeface(myTypeface);
				}	
			}
			
			a.recycle();
		}
	}

}