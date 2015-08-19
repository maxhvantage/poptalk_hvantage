package com.ciao.app.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.util.Log;
import com.poptalk.app.R;


/**
 * Created by rajat on 22/12/14.
 * This is image loader class use to download image from url and display it in image view
 * if it is not available device else it will create bitmap from local file
 */
public class ImageLoader {
	private static ImageLoader instance = null;
	private Context context;
	MemoryCache memoryCache=new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews= Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	final int stub_id= R.drawable.user_avtar;
	private boolean mIsStubShow;
	private static boolean isFullScreenImage;

	/**
	 * {@link Constructor}
	 * @param context
	 */
	public ImageLoader(Context context)
	{
		if (context == null)
			return;
		fileCache=new FileCache(context);
		executorService= Executors.newFixedThreadPool(5);
		this.context = context;
	}
	
	public static ImageLoader getInstance(Context context)
	{
		if (instance == null || instance.context == null) {
			instance = new ImageLoader(context);
			isFullScreenImage = false;
		}
		return instance;
	}

	/**
	 * this is single instance method use to return instance of class if not created before
	 * @param context
	 * @return {@link com.ciao.app.imageloader.ImageLoader}
	 */
	public static ImageLoader getInstance(Context context,boolean isFullScreen)
	{
		if (instance == null || instance.context == null) {
			instance = new ImageLoader(context);
			isFullScreenImage = isFullScreen;
		}
		return instance;
	}

	/**
	 * This mehtod is use to call to download image from url and put them in queue
	 * @param url
	 * @param imageView
	 * @param isShowStub
	 */
	public void displayImage(String url,ImageView imageView,boolean isShowStub)
	{
		mIsStubShow = isShowStub;
		imageViews.put(imageView, url);
		Bitmap bitmap=memoryCache.get(url);
		if(bitmap!=null)
			imageView.setImageBitmap(bitmap);
		else
		{
			queuePhoto(url, imageView);

			if(isShowStub)
			{
				imageView.setImageResource(stub_id);
			}
			else
			{
				imageView.setImageBitmap(null);
			}
		}
	}


	public void displayProfileImage(String url,ImageView imageView,boolean isShowStub)
	{
		mIsStubShow = isShowStub;
		imageViews.put(imageView, url);
		Bitmap bitmap=memoryCache.get(url);
		new DownloadImageTask(imageView, mIsStubShow).execute(url);
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		String urldisplay;
		Boolean mIsShowStub;

		public DownloadImageTask(ImageView bmImage,boolean isShowStub ) {
			this.bmImage = bmImage;
			this.mIsShowStub = isShowStub;
		}

		protected Bitmap doInBackground(String... urls) {
			urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			if(result != null)
			{
				bmImage.setImageBitmap(result);
			}
			else
			{
				queuePhoto(urldisplay, bmImage);
				if(mIsShowStub)
				{
					bmImage.setImageResource(stub_id);
				}
				else
				{
					bmImage.setImageBitmap(null);
				}
			}

		}
	}

	/**
	 * method is use to put image downloading in a queue if other image is downloading
	 * @param url
	 * @param imageView
	 */
	private void queuePhoto(String url, ImageView imageView)
	{
		PhotoToLoad p=new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	/**
	 * method is use to return bitmap for the url if image is downloaded
	 * @param url
	 * @return {@link android.graphics.Bitmap}
	 */
	private Bitmap getBitmap(String url)
	{
		File f=fileCache.getFile(url);
		//from SD cache
		Bitmap b = decodeFile(f);
		if(b!=null)
			return b;

		//from web

		if(url!=null && (url.contains("http://") || url.contains("https://")))
		{
			try
			{
				Bitmap bitmap=null;
				URL imageUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				conn.setInstanceFollowRedirects(true);
				InputStream is=conn.getInputStream();
				OutputStream os = new FileOutputStream(f);
				Utils.CopyStream(is, os);
				os.close();
				bitmap = decodeFile(f);
				return bitmap;
			} catch (Throwable ex){
				ex.printStackTrace();
				if(ex instanceof OutOfMemoryError)
					memoryCache.clear();
				return null;
			}
		}
		else
		{
			b = decodeFile(new File(url));
			return b;
		}
	}

	/**
	 * decodes image and scales it to reduce memory consumption
	 * @param f
	 * @return {@link android.graphics.Bitmap}
	 */
	private Bitmap decodeFile(File f)
	{
		try
		{
			//decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f),null,o);

			//Find the correct scale value. It should be the power of 2.
			
			final int REQUIRED_SIZE=512;
			int width_tmp=o.outWidth, height_tmp=o.outHeight;
			int scale=1;
			while(true)
			{
				if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
				break;
				width_tmp/=2;
				height_tmp/=2;
				scale*=2;
			}

			//decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {}
		return BitmapFactory.decodeResource(context.getResources(), R.drawable.user_avtar);
	}

	//Task for the queue
	private class PhotoToLoad
	{
		public String url;
		public ImageView imageView;
		public PhotoToLoad(String u, ImageView i){
			url=u;
			imageView=i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;
		PhotosLoader(PhotoToLoad photoToLoad){
			this.photoToLoad=photoToLoad;

		}

		@Override
		public void run() {
			if(imageViewReused(photoToLoad))
				return;
			Bitmap bmp=getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if(imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
			Activity a=(Activity)photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad){
		String tag=imageViews.get(photoToLoad.imageView);
		if(tag==null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	//Used to display bitmap in the UI thread
	private class BitmapDisplayer implements Runnable
	{
		Bitmap bitmap;
		PhotoToLoad photoToLoad;
		public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
		@Override
		public void run()
		{
			if(imageViewReused(photoToLoad))
				return;
			if(photoToLoad.imageView.getTag()!=null)
			{
				if(bitmap!=null)
				{
					photoToLoad.imageView.setImageBitmap(bitmap);
				}
				else
				{
					if(mIsStubShow)
					{
						photoToLoad.imageView.setImageResource(stub_id);
					}
					else
					{
						photoToLoad.imageView.setImageResource(0);
					}
				}
			}
		}
	}

	//	/**
	//	 * This method is used to load image from look up key
	//	 * @param lookupKey
	//	 * @return {@link Drawable}
	//	 */
	//	public Bitmap loadImageForContactUsingId(String lookupKey)
	//	{
	//		try
	//		{
	//			Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,String.valueOf(lookupKey));
	//			if (contactUri == null)
	//			{
	//				return null;
	//			}
	//			if (memoryCache.get(contactUri.toString()) != null)
	//			{
	//				return memoryCache.get(contactUri.toString());
	//			}
	//			//this method is use for api level 14
	//			InputStream contactImageStream = null;
	//			if(Integer.valueOf(Build.VERSION.SDK_INT).intValue() >= VERSION_CODES.ICE_CREAM_SANDWICH)
	//			{
	//				contactImageStream = Contacts.openContactPhotoInputStream(context.getContentResolver(),contactUri,true);
	//			}
	//			else
	//			{
	//				contactImageStream = Contacts.openContactPhotoInputStream(context.getContentResolver(),contactUri);
	//			}
	//
	//			if (contactImageStream != null)
	//			{
	//				Bitmap bitmap = BitmapFactory.decodeStream(contactImageStream);
	//				memoryCache.put(contactUri.toString(),bitmap);
	//				return bitmap;
	//			}
	//			else
	//			{
	//				return null;
	//			}
	//		}
	//		catch (Exception e)
	//		{
	//			Log.e("In Exception", "" + e.toString());
	//			e.printStackTrace();
	//			return null;
	//		}
	//	}

	//	public Bitmap getImageForContactUsingId(String contactId)
	//	{
	//		ContentResolver contentResolver = context.getContentResolver();
	//		Cursor cursor = null;
	//		Bitmap bitmap = null;
	//		try
	//		{
	//			cursor = contentResolver.query(Data.CONTENT_URI, new String[]{Photo.PHOTO_FILE_ID}, Data.CONTACT_ID +" = ? AND "+Data.MIMETYPE +" = ? ", new String[]{contactId,Photo.CONTENT_ITEM_TYPE}, null);
	//			if(cursor!=null && cursor.moveToFirst())
	//			{
	//				long photoFileId = cursor.getLong(cursor.getColumnIndexOrThrow(Photo.PHOTO_FILE_ID));
	//				InputStream inputStream = openDisplayPhoto(photoFileId);
	//				return BitmapFactory.decodeStream(inputStream);
	//			}
	//		}
	//		catch(Exception e)
	//		{
	//			e.printStackTrace();
	//			return bitmap;
	//		}
	//		finally
	//		{
	//			if(cursor!=null && !cursor.isClosed())
	//			{
	//				cursor.close();
	//			}
	//		}
	//		return bitmap;
	//		//		try
	//		//		{
	//		//			Uri contactUri = null;
	//		//			InputStream contactImageStream = null;
	//		//			if(Integer.valueOf(Build.VERSION.SDK_INT).intValue() >= VERSION_CODES.HONEYCOMB)
	//		//			{
	//		//				contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
	//		//				Uri displayPhotoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.PHOTO);
	//		//				try
	//		//				{
	//		//					AssetFileDescriptor fd =context.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
	//		//					contactImageStream = fd.createInputStream();
	//		//				}
	//		//				catch (IOException e)
	//		//				{
	//		//					return null;
	//		//				}
	//		//
	//		//			}
	//		//			else
	//		//			{
	//		//				contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
	//		//				Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
	//		//				Cursor cursor = context.getContentResolver().query(photoUri,
	//		//						new String[] {Contacts.Photo.PHOTO}, null, null, null);
	//		//				if (cursor == null)
	//		//				{
	//		//					return null;
	//		//				}
	//		//				try
	//		//				{
	//		//					if (cursor.moveToFirst())
	//		//					{
	//		//						byte[] data = cursor.getBlob(0);
	//		//						if (data != null)
	//		//						{
	//		//							contactImageStream=  new ByteArrayInputStream(data);
	//		//						}
	//		//					}
	//		//				}
	//		//				finally
	//		//				{
	//		//					cursor.close();
	//		//				}
	//		//
	//		//			}
	//		//			contactImageStream = contentResolver.openInputStream(contactUri);
	//		//			if (contactImageStream != null)
	//		//			{
	//		//				Bitmap bitmap = BitmapFactory.decodeStream(contactImageStream);
	//		//				memoryCache.put(contactUri.toString(),bitmap);
	//		//				return bitmap;
	//		//			}
	//		//		}
	//		//		catch (FileNotFoundException e)
	//		//		{
	//		//			e.printStackTrace();
	//		//			return null;
	//		//		}
	//		//		return null;
	//	}


	//	public Drawable loadImageForContactUsingLookupKey(String lookupKey)
	//	{
	//		try
	//		{
	//			Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,String.valueOf(lookupKey));
	//			if (contactUri == null)
	//			{
	//				return null;
	//			}
	//			if (memoryCache.get(contactUri.toString()) != null)
	//			{
	//				return new BitmapDrawable(context.getResources(),memoryCache.get(contactUri.toString()));
	//			}
	//			InputStream contactImageStream = Contacts.openContactPhotoInputStream(context.getContentResolver(),contactUri);
	//			if (contactImageStream != null)
	//			{
	//				Drawable d = Drawable.createFromStream(contactImageStream,"contact_image");
	//				memoryCache.put(contactUri.toString(),BitmapFactory.decodeStream(contactImageStream));
	//				return d;
	//			}
	//			else
	//			{
	//				return null;
	//			}
	//		}
	//		catch (Exception e)
	//		{
	//			Log.e("In Exception", "" + e.toString());
	//			e.printStackTrace();
	//			return null;
	//		}
	//	}

	//	public Bitmap loadContactPhoto(ContentResolver cr, long id)
	//	{
	//        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
	//
	//        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri,true);
	//        // InputStream input = ContactsContract.Contacts.Photo
	//        if (input == null)
	//        {
	//            return null;
	//        }
	//        return BitmapFactory.decodeStream(input);
	//    }
	//
	//
	//	public InputStream openDisplayPhoto(long photoFileId)
	//	{
	//	     Uri displayPhotoUri = ContentUris.withAppendedId(DisplayPhoto.CONTENT_URI, photoFileId);
	//	     Log.e("photo file id==>>"+photoFileId, "Photo uri==>>"+displayPhotoUri);
	//	     try
	//	     {
	//	         AssetFileDescriptor fd = context.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
	//	         return fd.createInputStream();
	//	     }
	//	     catch (IOException e)
	//	     {
	//	    	 e.printStackTrace();
	//	         return null;
	//	     }
	//	 }

	/**
	 * This method is use to clear cache once it is over loaded
	 * @param none
	 * @return void
	 */
	public void clearCache()
	{
		if(memoryCache!=null)
		{
			memoryCache.clear();
		}
		if(fileCache!=null)
		{
			fileCache.clear();
		}
		System.gc();
	}

}
