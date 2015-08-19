package com.ciao.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.poptalk.app.R;
import com.ciao.app.imageloader.ImageLoader;
/**
 * Created by vinod on 16/4/15.
 * This Adapter is used in the screen where user can see full screen image
 */
public class FullScreenImageAdapter  extends PagerAdapter{

	private Context context;
	private ImageLoader mImageLoader;
	private ArrayList<String> imageList;
	public FullScreenImageAdapter(Context context ,ArrayList<String> imageList) {
		this.context  = context;
		this.imageList = imageList;
		mImageLoader = ImageLoader.getInstance(context,true);
	}
	@Override
	public int getCount() {

		return imageList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View)object);
	}
	@Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
         
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View viewItem = inflater.inflate(R.layout.acitivity_full_screen_image, container, false);
        ImageView imageView = (ImageView) viewItem.findViewById(R.id.iv_chat_image);
        mImageLoader.displayImage(imageList.get(position),imageView,false);
        ((ViewPager)container).addView(viewItem);
         
        return viewItem;
    }
}
