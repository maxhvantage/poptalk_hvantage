package com.ciao.app.views.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;
import com.ciao.app.adapters.GroupMultipleUserListAdapter;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.netwrok.backgroundtasks.GroupCreaterAsyncTask;
import com.ciao.app.utils.AppUtils;
/**
 * Created by rajat on 28/2/15.
 */
public class CreateGroupUsingListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView groupUserLV;
    private GroupMultipleUserListAdapter mAdapter;
    private EditText searchContactET;
    private String groupName,groupIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_using_list);
        searchContactET = (EditText)findViewById(android.R.id.input);
        groupUserLV = (ListView) findViewById(R.id.lv_group_user);
        groupUserLV.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mAdapter = new GroupMultipleUserListAdapter(this);
        groupUserLV.setAdapter(mAdapter);
        Intent intent = getIntent();
        if(intent!=null){
        	Bundle bundle = intent.getExtras();
        	if(bundle!=null){
        		groupName = bundle.getString("group_name");
        		groupIcon = bundle.getString("group_icon");
        	}
        }
        getLoaderManager().restartLoader(0, null, this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        searchContactET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s.toString());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null || constraint.length() == 0) {
                    return getContentResolver().query(
                            Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING),
                            null,
                            null,
                            null,
                            AppDatabaseConstants.KEY_NAME + " ASC"
                    );
                }
                else {
                    return getContentResolver().query(
                            Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING),
                            null,
                            AppDatabaseConstants.KEY_NAME + " LIKE ?",
                            new String[] { "%"+constraint+"%" },
                            AppDatabaseConstants.KEY_NAME + " ASC"
                    );
                }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING), null, AppDatabaseConstants.KEY_CIAO_USER +" = ? ", new String[]{"Y"}, AppDatabaseConstants.KEY_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
       mAdapter.swapCursor(null);
    }
    
    public void goToPreviousScreen(View view){
    	finish();
    }
    
    public void createGroupChat(View view){

        if (!AppSharedPreference.getInstance(CreateGroupUsingListActivity.this).getIsConnectedToInternet()){
            DialogUtils.showInternetAlertDialog(CreateGroupUsingListActivity.this);
            return;
        }

		if(mAdapter.getSelectedContact().size()>0){
			new GroupCreaterAsyncTask(this,mAdapter.getSelectedContact(),groupName,groupIcon).execute();
		}else{
			AppUtils.showTost(this, "Please select atleast one contact");
	   }
		
	}

}
