package com.ciao.app.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by rajat on 12/3/15.
 */
public class AnimationUtils {
	public static final int SHABON_DURATION = 2000;
	public static final int SHABON_ALPHA_DURATION = 500;

	public static void rotateCreditCoin(ImageView imageView){
		ObjectAnimator animation = ObjectAnimator.ofFloat(imageView, "rotationY", 0.0f, 360f);
		animation.setDuration(1000);
		animation.setRepeatCount(ObjectAnimator.RESTART);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.start();

	}

	public static Animation syncInProgressAnimation(){
		AnimationSet animSet = new AnimationSet(true);
		animSet.setInterpolator(new LinearInterpolator());
		animSet.setFillAfter(true);
		animSet.setFillEnabled(true);
        animSet.setRepeatCount(Animation.INFINITE);
        animSet.setRepeatMode(Animation.RESTART);
		RotateAnimation animRotate = new RotateAnimation(0.0f,360.0f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);

		animRotate.setFillAfter(true);
		animRotate.setDuration(5000);
		animSet.addAnimation(animRotate);
		return animSet;
	}
	public static Animation createSplashLogoAnimation() {
		AnimationSet set = new AnimationSet(false);

		ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(1000);
		sa.setRepeatCount(5);
		sa.setRepeatMode(Animation.REVERSE);
		set.addAnimation(sa);
		set.setInterpolator(new OvershootInterpolator(1.5f));
		return set;
	}

	public static Animation createKumoAnimation(Context context) {
		AnimationSet set = new AnimationSet(true);

		ScaleAnimation sa = new ScaleAnimation(0.98f, 1.02f, 0.98f, 1.02f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(12000);
		sa.setRepeatMode(Animation.REVERSE);
		sa.setRepeatCount(5);

		TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.05f, Animation.RELATIVE_TO_SELF, 0.05f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
		ta.setDuration(6000);
		ta.setRepeatMode(Animation.REVERSE);
		ta.setRepeatCount(10);

		set.addAnimation(sa);
		set.addAnimation(ta);
		set.setFillAfter(true);

		return set;
	}
	public static Animation createShabonAnimation() {
		AnimationSet set = new AnimationSet(false);
		AlphaAnimation aa = new AlphaAnimation(0f, 1f);
		aa.setDuration(SHABON_ALPHA_DURATION * 4);
		TranslateAnimation ta1 = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -0.1f,
				Animation.RELATIVE_TO_PARENT, 0.1f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		ta1.setDuration(SHABON_DURATION / 5);
		ta1.setRepeatMode(Animation.REVERSE);
		ta1.setRepeatCount(SHABON_DURATION / (SHABON_DURATION / 5) - 1);

		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(SHABON_DURATION - SHABON_ALPHA_DURATION);

		TranslateAnimation ta2 = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, 
				Animation.RELATIVE_TO_PARENT, 0f, 
				Animation.RELATIVE_TO_PARENT, 0.8f,
				Animation.RELATIVE_TO_PARENT, 0f);
		ta2.setDuration(SHABON_DURATION + SHABON_ALPHA_DURATION);
		//右から中心位置に戻るアニメーション
		TranslateAnimation ta3 = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				-0.1f, Animation.RELATIVE_TO_PARENT, 0f,
				Animation.RELATIVE_TO_PARENT, 0f);
		ta3.setDuration(SHABON_DURATION / 5);
		ta3.setStartOffset(SHABON_DURATION);

		set.addAnimation(aa);
		set.addAnimation(ta1);
		set.addAnimation(sa);
		set.addAnimation(ta2);
		set.addAnimation(ta3);
		set.setFillAfter(true);

		return set;
	}

	public static Animation bounceAppLogoAnimation(){
		AnimationSet set = new AnimationSet(false);

		TranslateAnimation topToBottom = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, 
				Animation.RELATIVE_TO_PARENT, 0f, 
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, -0.1f);
		topToBottom.setDuration(2000);

		TranslateAnimation bottomToTop= new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, 
				Animation.RELATIVE_TO_PARENT, 0f, 
				Animation.RELATIVE_TO_PARENT, 0.1f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		bottomToTop.setDuration(500);
		bottomToTop.setStartOffset(2000);

		/*TranslateAnimation topToBottom1 = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, 
				Animation.RELATIVE_TO_PARENT, 0f, 
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.1f);
		topToBottom1.setDuration(500);
		topToBottom1.setStartOffset(2500);*/


		set.addAnimation(topToBottom);
		set.addAnimation(bottomToTop);
		//set.addAnimation(topToBottom1);
		set.setFillAfter(true);
		return set;
	}

	public static Animation leftToRight(){
		TranslateAnimation leftToRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		leftToRight.setDuration(1500);
		leftToRight.setInterpolator(new AccelerateInterpolator());
		return leftToRight;
	}
	public static Animation rightToLeft(){
		TranslateAnimation rightToLeft =  new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		rightToLeft.setDuration(1500);
		rightToLeft.setInterpolator(new AccelerateInterpolator());
		return rightToLeft;
	}

}
