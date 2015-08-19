package com.ciao.app.views.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.poptalk.app.R;
import com.ciao.app.adapters.GroupUserContactsAdapter;
import com.ciao.app.datamodel.CiaoContactBean;
import com.ciao.app.datamodel.Contact;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.customviews.ContactsEditText;

/**
 * Created by rajat on 28/2/15.
 */
public class CreateGroupUsingAutoCompleterActivity extends Activity {
    private ContactsEditText editText;
    private ListView groupUserLV;
    private GroupUserContactsAdapter mAdapter;
    private ArrayList<CiaoContactBean> contactList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group_autocompleter);
        groupUserLV = (ListView)findViewById(R.id.lv_group_user);
        contactList = new ArrayList<CiaoContactBean>();
        mAdapter = new GroupUserContactsAdapter(this,R.layout.contacts_dropdown_item,contactList);
        groupUserLV.setAdapter(mAdapter);
        editText = (ContactsEditText) findViewById(android.R.id.input);
        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	CiaoContactBean contact = (CiaoContactBean) parent.getItemAtPosition(position);
                contactList.add(contact);
                mAdapter.notifyDataSetChanged();
                //Toast.makeText(getApplicationContext(), contact.id+"-" + contact.name, Toast.LENGTH_SHORT).show();
                editText.setText("");
            }
        });

    }

    public void goToSelectMultipleContactsAtOnce(View view){
        AppUtils.showTost(this,"Multiple contact");
    }

    public void goToPreviousScreen(View view){
        finish();
    }
}
