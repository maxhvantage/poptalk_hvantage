package com.ciao.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ciao.app.constants.AppDatabaseConstants;

/**
 * Created by rajat on 11/2/15.
 * Create and upgrade application database

 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" +AppDatabaseConstants.DATABASE_NAME, null, AppDatabaseConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_EMAIL);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_PHONE);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_CHAT_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_GROUP_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_GROUP_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_GROUP_CHAT_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_SMS);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_SMS_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_MY_CIAO);
        db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_CAIO_RATES);
        db.execSQL(AppDatabaseConstants.CREATE_CONTACTS_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_PHONE_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_EMAIL_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_CHAT_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_CHAT_DETAILS_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_GROUP_CHAT_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_GROUP_DETAIL_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_GROUP_CHAT_DETAILS_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_SMS_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_SMS_DETAILS_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_MY_CIAO_TABLE);
        db.execSQL(AppDatabaseConstants.CREATE_CIAO_RATES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Versions","Old version is "+oldVersion+" -- new version is "+newVersion);
        if (oldVersion <= 1) {
            db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_CHAT);
            db.execSQL("DROP TABLE IF EXISTS " + AppDatabaseConstants.TABLE_CHAT_DETAILS);
            db.execSQL(AppDatabaseConstants.CREATE_CHAT_TABLE);
            db.execSQL(AppDatabaseConstants.CREATE_CHAT_DETAILS_TABLE);
            db.execSQL("ALTER TABLE " +AppDatabaseConstants.TABLE_PHONE + " ADD COLUMN " + AppDatabaseConstants.KEY_COMM_USER + " TEXT");
            db.execSQL("ALTER TABLE " +AppDatabaseConstants.TABLE_CONTACTS + " ADD COLUMN " + AppDatabaseConstants.KEY_COMM_USER + " TEXT");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        super.onDowngrade(db, oldVersion, newVersion);
        if (newVersion == 1) {
            db.execSQL("ALTER TABLE " +AppDatabaseConstants.TABLE_PHONE + " DROP COLUMN " + AppDatabaseConstants.KEY_COMM_USER);
            db.execSQL("ALTER TABLE " +AppDatabaseConstants.TABLE_CHAT + " DROP COLUMN " + AppDatabaseConstants.KEY_PHONE_NUMBER);
            db.execSQL("ALTER TABLE " +AppDatabaseConstants.TABLE_CONTACTS + " DROP COLUMN " + AppDatabaseConstants.KEY_COMM_USER);
        }
    }

    public void clearMyCiaoNumberDb(SQLiteDatabase db){
        db.execSQL("delete from "+ AppDatabaseConstants.TABLE_MY_CIAO);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
    }
}
