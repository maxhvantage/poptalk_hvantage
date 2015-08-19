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
import com.ciao.app.adapters.MessageAdapter;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.views.activities.CallActivity;

/**
 * Created by rajat on 29/1/15.
 */
public class MessageViewFragment extends Fragment {
    private View mView;
    private ListView messagesLV;
    private LoaderManager.LoaderCallbacks<Cursor> chatLoader;
    private MessageAdapter messageAdapter;
    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_message_view, container, false);
        mActivity = getActivity();
        messageAdapter = new MessageAdapter(mActivity);
        intViews(mView);
        intLoader();
        getLoaderManager().initLoader(0, null, chatLoader);
        return mView;
    }

    private void intLoader() {
        chatLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            	String appUserID = AppSharedPreference.getInstance(mActivity).getUserID();
                return new CursorLoader(mActivity, Uri.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING), null, AppDatabaseConstants.KEY_APP_USER_ID+" = ? ", new String[]{appUserID}, AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED+ " DESC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                messageAdapter.changeCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                messageAdapter.changeCursor(null);
            }
        };
    }

    private void intViews(View mView) {
        messagesLV = (ListView) mView.findViewById(R.id.lv_message);
        messagesLV.setAdapter(messageAdapter);
        messagesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActivity instanceof CallActivity) {

                }
            }
        });
    }
}
