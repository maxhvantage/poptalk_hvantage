package com.ciao.app.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.datamodel.CiaoContactBean;
import com.ciao.app.views.customviews.circularimageview.CircularImageView;


/**
 * Created by rajat on 28/2/15.
 * This Adapter is used in the screen where user can select multiple user at once in order to create group.
 */
public class GroupMultipleUserListAdapter extends CursorAdapter {
    private int mIndexDisplayName;
    private int mIndexPhotoUri;
    private static ArrayList<CiaoContactBean> mCheckedState,contactSelected;



    public GroupMultipleUserListAdapter(Context context) {
        super(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mCheckedState = new ArrayList<CiaoContactBean>();
        contactSelected  = new ArrayList<CiaoContactBean>();
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor != null) {
            mIndexDisplayName = newCursor.getColumnIndex(AppDatabaseConstants.KEY_NAME);
            mIndexPhotoUri = newCursor.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC);
        }
        return super.swapCursor(newCursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
    	for(int i = 0; i<cursor.getCount(); i++) {
    		CiaoContactBean  contactBean = new CiaoContactBean();
    	    mCheckedState.add(contactBean);
    	}
        return ViewHolder.create(parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //final String lookupKey = cursor.getString(mIndexLookupKey);
        final String displayName = cursor.getString(mIndexDisplayName);
        final String photoUri = cursor.getString(mIndexPhotoUri);
        String id = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_ID));
        ViewHolder.get(view).bind(displayName, id, photoUri,cursor.getPosition());


    }

    private static class ViewHolder implements OnClickListener{
        private ViewGroup mLayout;
        private CircularImageView mContactPhoto;
        private TextView mContactName;
        private CheckBox mCheckBox;


        public static final ViewGroup create(ViewGroup parent) {
            return new ViewHolder(parent).mLayout;
        }

        public static final ViewHolder get(View parent) {
            return (ViewHolder) parent.getTag();
        }

        private ViewHolder(ViewGroup parent) {
            Context context = parent.getContext();
            mLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.row_group_multiple_user_selection, parent, false);
            mLayout.setTag(this);
            mContactPhoto = (CircularImageView) mLayout.findViewById(android.R.id.icon);
            mContactName = (TextView) mLayout.findViewById(android.R.id.text1);
            mCheckBox = (CheckBox)mLayout.findViewById(R.id.cb_select_contact);
            mCheckBox.setOnClickListener(this);
            mLayout.setOnClickListener(this);
            

        }

        public void bind(String displayName, String id, String photoUri, int position) {
        	CiaoContactBean contactBean = mCheckedState.get(position);
        	
        	if (photoUri != null) {
                mContactPhoto.setImageURI(Uri.parse(photoUri));
            } else {
                mContactPhoto.setImageResource(R.drawable.user_avtar);
            }
            mContactName.setText(displayName);
            mCheckBox.setChecked(contactBean.isSelected());
            contactBean.setId(id);
            mCheckBox.setTag(contactBean);
            mLayout.setTag(this);

        }

		@Override
		public void onClick(View v) {
			if(v instanceof CheckBox){
				CheckBox cb = (CheckBox)v;
				CiaoContactBean bean = (CiaoContactBean)cb.getTag();
				if(cb.isChecked()){
					bean.setSelected(true);
					contactSelected.add(bean);
				}else{
					bean.setSelected(false);
					contactSelected.remove(bean);
				}	
			}else{
				CheckBox cb = (CheckBox)v.findViewById(R.id.cb_select_contact);
				CiaoContactBean bean = (CiaoContactBean)cb.getTag();
				if(cb.isChecked()){
					cb.setChecked(false);
					bean.setSelected(false);
					contactSelected.remove(bean);
				}else{
					cb.setChecked(true);
					bean.setSelected(true);
					contactSelected.add(bean);
				}	
			}
			
			
		}


    }
    
    public ArrayList<CiaoContactBean> getSelectedContact(){
    	return contactSelected;
    }

}
