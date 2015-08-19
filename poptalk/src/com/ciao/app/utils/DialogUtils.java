package com.ciao.app.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.ciao.app.views.activities.AddParticipantToGroupActivity;
import com.ciao.app.views.activities.CallActivity;
import com.ciao.app.views.activities.InterstialContactListActivity;
import com.ciao.app.views.activities.RegistrationActivity;
import com.ciao.app.views.fragments.SettingsFragment;

/**
 * Created by rajat on 24/2/15.
 */
public class DialogUtils {

	public static void showContactSyncDialog(final Context context) {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		builder1.setMessage("Sync your contacts with the PopTalk app?");
		builder1.setCancelable(false);
		builder1.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				if(context instanceof CallActivity){
					((CallActivity)context).syncContacts();
				}else if (context instanceof InterstialContactListActivity) {
					((InterstialContactListActivity)context).syncContacts();
				}

			}
		});
		builder1.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alertDialog = builder1.create();
		alertDialog.show();
	}


	public static void showCountryCodeDialog(final Context context) {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		builder1.setMessage("Did you forget to add your friend's country code? Would you like to add country code from list?");
		builder1.setCancelable(false);
		builder1.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				if(context instanceof RegistrationActivity){
					((RegistrationActivity)context).showCountryList();
				}
			}
		});
		builder1.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alertDialog = builder1.create();
		alertDialog.show();
	}


   public static void showAddToGroupDialog(final Context context,String contactName,String groupName){
	   AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		builder1.setMessage("Add "+contactName+" to "+"\""+groupName+"\""+"?");
		builder1.setCancelable(false);
		builder1.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				if(context instanceof AddParticipantToGroupActivity){
					((AddParticipantToGroupActivity)context).addParticipant();
				}
			}
		});
		builder1.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alertDialog = builder1.create();
		alertDialog.show(); 
   }

	public static void showInternetAlertDialog(final Context context){
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Enable Internet");
		alertDialogBuilder.setMessage("To use all features of Poptalk please enable your phone's internet connection.");
		alertDialogBuilder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
			}
		});
		alertDialogBuilder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alertDialogBuilder.create().show();
	}



	



}
