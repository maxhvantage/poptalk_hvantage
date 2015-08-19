package com.ciao.app.views.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.poptalk.app.R;
import com.ciao.app.adapters.SmsLogAdapter;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.sms.SmsQuery;
import com.ciao.app.views.activities.CallActivity;

/**
 * Created by rajat on 29/1/15.
 * This class is used to show the SMS log under the SMS tab of application
 */
public class SmsViewFragment extends Fragment {
    private View mView;
    private ListView smsLV;
    private LoaderManager.LoaderCallbacks<Cursor> smsLoader;
    private SmsLogAdapter smsAdapter;
    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sms_view,container,false);
        mActivity =getActivity();
        smsAdapter = new SmsLogAdapter(mActivity);
        intViews(mView);
        intLoader();
        // Simple query to show the most recent SMS messages in the inbox
        getLoaderManager().initLoader(SmsQuery.TOKEN, null, smsLoader);
        return mView;
    }

    private void intViews(View mView) {
        smsLV = (ListView)mView.findViewById(R.id.lv_sms);

        smsLV.setAdapter(smsAdapter);
        smsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mActivity instanceof CallActivity){

                }
            }
        });
    }

    private void intLoader() {
        smsLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == SmsQuery.TOKEN) {
                    // This will fetch all SMS messages in the inbox, ordered by date desc
                	String appUserID = AppSharedPreference.getInstance(mActivity).getUserID();
                    return new CursorLoader(mActivity, Uri.parse(AppDatabaseConstants.CONTENT_URI_SMS), null, AppDatabaseConstants.KEY_APP_USER_ID+" = ? ", new String[]{appUserID},AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED+" DESC");
                } else {
                    return null;
                }
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                smsAdapter.changeCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                smsAdapter.changeCursor(null);
            }
        };
    }
}

