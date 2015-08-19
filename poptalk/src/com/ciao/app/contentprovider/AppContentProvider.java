package com.ciao.app.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.database.DatabaseHelper;

/**
 * Created by rajat on 12/2/15.
 * This Class is responsible for to execute all database related operation.
 */
public class AppContentProvider extends ContentProvider {

	private static final UriMatcher uriMatcher;
	private SQLiteDatabase mDatabase;
	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY, AppDatabaseConstants.TABLE_CONTACTS, AppDatabaseConstants.CONTACTS);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY, AppDatabaseConstants.TABLE_PHONE, AppDatabaseConstants.PHONE);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_EMAIL,AppDatabaseConstants.EMAIL);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_CHAT,AppDatabaseConstants.CHAT_USER);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_CHAT_DETAILS,AppDatabaseConstants.CHAT_USER_MESSAGE);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_GROUP_CHAT,AppDatabaseConstants.CHAT_GROUPS);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_GROUP_DETAILS,AppDatabaseConstants.CHAT_GROUPS_DETAILS);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_GROUP_CHAT_DETAILS,AppDatabaseConstants.CHAT_GROUPS_CHAT_DETAILS);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_SMS,AppDatabaseConstants.SMS);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_SMS_DETAILS,AppDatabaseConstants.SMS_DETAIL);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_MY_CIAO,AppDatabaseConstants.MY_CIAO);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_CAIO_RATES,AppDatabaseConstants.CIAO_RATES);
		uriMatcher.addURI(AppDatabaseConstants.AUTHORITY,AppDatabaseConstants.TABLE_CAIO_CALLS,AppDatabaseConstants.CIAO_CALLS);
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		mDatabase = databaseHelper.getWritableDatabase();
		return (mDatabase != null);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		switch (uriMatcher.match(uri)){
		case AppDatabaseConstants.CONTACTS:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_CONTACTS);
			break;
		case AppDatabaseConstants.PHONE:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_PHONE);
			break;
		case AppDatabaseConstants.EMAIL:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_EMAIL);
			break;
			
		case AppDatabaseConstants.CHAT_USER:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_CHAT);
			break;
		case AppDatabaseConstants.CHAT_USER_MESSAGE:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_CHAT_DETAILS);
			break;
		case AppDatabaseConstants.CHAT_GROUPS:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_GROUP_CHAT);
			break;
		case AppDatabaseConstants.CHAT_GROUPS_DETAILS:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_GROUP_DETAILS);
			break;
		case AppDatabaseConstants.CHAT_GROUPS_CHAT_DETAILS:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_GROUP_CHAT_DETAILS);
			break;
		case AppDatabaseConstants.SMS:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_SMS);
			break;
		case AppDatabaseConstants.SMS_DETAIL:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_SMS_DETAILS);
			break;
		case AppDatabaseConstants.MY_CIAO:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_MY_CIAO);
			break;
		case AppDatabaseConstants.CIAO_RATES:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_CAIO_RATES);
			break;
		case AppDatabaseConstants.CIAO_CALLS:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_CAIO_CALLS);
			break;	
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		Cursor cursor = queryBuilder.query(mDatabase, projection, selection,selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;

	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public  synchronized Uri insert(Uri uri, ContentValues values) {
		long index = -1;
		switch (uriMatcher.match(uri)){
		case AppDatabaseConstants.CONTACTS:
			index  = mDatabase.insert(AppDatabaseConstants.TABLE_CONTACTS,null,values);
			//Log.e("Contact inserted -",""+index);
			break;
		case AppDatabaseConstants.PHONE:
			index  = mDatabase.insert(AppDatabaseConstants.TABLE_PHONE,null,values);
			//Log.e("Phone inserted -",""+index);
			break;
		case AppDatabaseConstants.EMAIL:
			index  = mDatabase.insert(AppDatabaseConstants.TABLE_EMAIL,null,values);
			//Log.e("Email inserted -",""+index);
			break;
		case AppDatabaseConstants.CHAT_USER:
			index  = mDatabase.insert(AppDatabaseConstants.TABLE_CHAT,null,values);
			break;
		case AppDatabaseConstants.CHAT_USER_MESSAGE:
			index = mDatabase.insert(AppDatabaseConstants.TABLE_CHAT_DETAILS,null,values);
			break;
	    case AppDatabaseConstants.CHAT_GROUPS:
	    	index = mDatabase.insert(AppDatabaseConstants.TABLE_GROUP_CHAT, null, values);
	    	break;
	    case AppDatabaseConstants.CHAT_GROUPS_DETAILS:
	    	index = mDatabase.insert(AppDatabaseConstants.TABLE_GROUP_DETAILS, null, values);
	    	break;
	    case AppDatabaseConstants.CHAT_GROUPS_CHAT_DETAILS:
	    	index = mDatabase.insert(AppDatabaseConstants.TABLE_GROUP_CHAT_DETAILS, null, values);
			break;
	    case AppDatabaseConstants.SMS:
	    	index = mDatabase.insert(AppDatabaseConstants.TABLE_SMS, null, values);
	    	break;
	    case AppDatabaseConstants.SMS_DETAIL:
	    	index = mDatabase.insert(AppDatabaseConstants.TABLE_SMS_DETAILS, null, values);
	    	break;
	    case AppDatabaseConstants.MY_CIAO:
	    	index = mDatabase.insert(AppDatabaseConstants.TABLE_MY_CIAO, null, values);
	    	break;
	    case AppDatabaseConstants.CIAO_RATES:
	    	index =  mDatabase.insert(AppDatabaseConstants.TABLE_CAIO_RATES, null, values);
	    	break;
	    case AppDatabaseConstants.CIAO_CALLS:
	    	index =  mDatabase.insert(AppDatabaseConstants.TABLE_CAIO_CALLS, null, values);
	    	break;	
		}
		Uri tempuri = ContentUris.withAppendedId(uri, index);
		getContext().getContentResolver().notifyChange(tempuri, null);
		return tempuri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		int updateCount;
		switch (uriMatcher.match(uri)){
		case AppDatabaseConstants.CONTACTS:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_CONTACTS);
			updateCount = mDatabase.update(AppDatabaseConstants.TABLE_CONTACTS, values, selection, selectionArgs);
			break;
		case AppDatabaseConstants.PHONE:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_PHONE);
			updateCount = mDatabase.update(AppDatabaseConstants.TABLE_PHONE, values, selection, selectionArgs);
			break;
		case AppDatabaseConstants.EMAIL:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_EMAIL);
			updateCount = mDatabase.update(AppDatabaseConstants.TABLE_EMAIL, values, selection, selectionArgs);
			break;
		case AppDatabaseConstants.CHAT_USER:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_CHAT);
			updateCount = mDatabase.update(AppDatabaseConstants.TABLE_CHAT, values, selection, selectionArgs);
			break;
		case AppDatabaseConstants.CHAT_USER_MESSAGE:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_CHAT_DETAILS);
			updateCount = mDatabase.update(AppDatabaseConstants.TABLE_CHAT_DETAILS, values, selection, selectionArgs);
			break;
			
		case AppDatabaseConstants.SMS:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_SMS);
			updateCount = mDatabase.update(AppDatabaseConstants.TABLE_SMS, values, selection, selectionArgs);
			break;
		case AppDatabaseConstants.SMS_DETAIL:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_SMS_DETAILS);
			updateCount = mDatabase.update(AppDatabaseConstants.TABLE_SMS_DETAILS, values, selection, selectionArgs);
			break;
		case AppDatabaseConstants.CHAT_GROUPS:
			queryBuilder.setTables(AppDatabaseConstants.TABLE_GROUP_CHAT);
			updateCount = mDatabase.update(AppDatabaseConstants.TABLE_GROUP_CHAT, values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return updateCount;
	}
}
