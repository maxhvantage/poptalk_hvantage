package com.ciao.app.interfaces;

import java.util.ArrayList;
import java.util.List;

import com.ciao.app.adapters.AutocompleteContactAdapter;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.CiaoContactBean;
import com.ciao.app.views.activities.CreateGroupChatAutoCompleterView;

import android.R.integer;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class CustomAutoCompleteTextChangedListener implements TextWatcher{

	public static final String TAG = "CustomAutoCompleteTextChangedListener.java";

	private ApplicationDAO contactDAO ; 
	private Context context;
	private ArrayList<CiaoContactBean> selectedContactList;
	public CustomAutoCompleteTextChangedListener(Context context,ArrayList<CiaoContactBean> selectedContactList){
		contactDAO = ApplicationDAO.getInstance(context);
		this.context = context;
		this.selectedContactList = selectedContactList;
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,	int after) {


	}

	@Override
	public void onTextChanged(CharSequence userInput, int start, int before, int count) {
		

	}

	@Override
	public void afterTextChanged(Editable s) {
		if(context instanceof CreateGroupChatAutoCompleterView){
			List<CiaoContactBean> matchedContactList = contactDAO.getContactForAutocompleterView(s.toString().trim(),selectedContactList);
			AutocompleteContactAdapter adapter = new AutocompleteContactAdapter(context, android.R.layout.simple_list_item_1, android.R.id.text1, matchedContactList);
			((CreateGroupChatAutoCompleterView)(context)).updateAutoCompleter(adapter);
		}

	}

	public void updateSelectedContactList(ArrayList<CiaoContactBean> selectedContactList){
		this.selectedContactList = selectedContactList;
	}

}
