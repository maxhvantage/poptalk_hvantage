package com.ciao.app.netwrok.backgroundtasks;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.XmppConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.views.activities.GroupChatActivity;

import android.content.Context;
import android.os.AsyncTask;
/*
 * This asyncTask is used to save the messages in background.
 */
public class SaveReceivedMessageInDb extends AsyncTask<Void, Void, Void>{
	private Context context;
	private Message message;
	private String changeInGroup;
	public SaveReceivedMessageInDb(Context context,Message message){
		this.context = context;
		this.message =message;
	}
	@Override
	protected Void doInBackground(Void... params) {
		final String messgageText;
		final String groupJid = StringUtils.parseBareAddress(message.getFrom());
		if(ApplicationDAO.getInstance(context).isMemberOfThisGroup(groupJid.split("@")[0])){
			final String messageid = StringUtils.parseBareAddress(message.getPacketID());
			//Check if incoming message already exist in db
			if(!ApplicationDAO.getInstance(context).isMessageAlreadyExist(messageid.trim())){
				// message not exist in db
				final String messageType = message.getSubject();
				if(messageType!=null){
					if(messageType.equalsIgnoreCase(XmppConstants.TYPE_IMAGE)){
						messgageText = message.getBody();
					}else {
						messgageText = StringUtils.parseBareAddress(message.getBody());
					}	
				}else{
					//messgageText = StringUtils.parseBareAddress(message.getBody());
					messgageText = message.getBody();
				}
				// TODO Auto-generated method stub
				String sender = message.getFrom().split("/")[1];
				String myJid = AppSharedPreference.getInstance(context).getUserCountryCode()+AppSharedPreference.getInstance(context).getUserPhoneNumber();
				sender=sender.split("@")[0];
				if(!sender.equalsIgnoreCase(myJid)){
					sender = ApplicationDAO.getInstance(context).getContactNameFromNumber(sender);
					int unreadMessages = ApplicationDAO.getInstance(context).getUnreadMessageCount(groupJid.split("@")[0]);
					String[] editSalts = messgageText.split("#@!");
					String salt = editSalts[0];
					if(salt.equalsIgnoreCase(AppConstants.SALT)){

						String messageBody  =changeInGroup = editSalts[1];
						if(messageBody.contains("http://")||messageBody.contains("https://")){
							String groupChanged = sender+" has changed group Image "; 
							ApplicationDAO.getInstance(context).updateGroupImage(groupJid.split("@")[0],messageBody);
							ApplicationDAO.getInstance(context).saveGroupChatMessageInDb(context, groupChanged, groupJid.split("@")[0], sender, messageid, XmppConstants.DELIEVERED, XmppConstants.RECEIVED, XmppConstants.TYPE_EDIT_GROUP, "filePath", true);	
							ApplicationDAO.getInstance(context).updateLastMessage(groupJid.split("@")[0], System.currentTimeMillis(), groupChanged, unreadMessages, true,true);	
						}else{
							String groupChanged = sender+" has changed group subject to "+messageBody+"."; 
							ApplicationDAO.getInstance(context).saveGroupChatMessageInDb(context, groupChanged, groupJid.split("@")[0], sender, messageid, XmppConstants.DELIEVERED, XmppConstants.RECEIVED, XmppConstants.TYPE_EDIT_GROUP, "filePath", true);	
							ApplicationDAO.getInstance(context).updateLastMessage(groupJid.split("@")[0], System.currentTimeMillis(), groupChanged, unreadMessages, true,true);	
							ApplicationDAO.getInstance(context).updateGroupName(editSalts[1], groupJid.split("@")[0]);

						}

					}else{
						ApplicationDAO.getInstance(context).saveGroupChatMessageInDb(context, messgageText, groupJid.split("@")[0], sender, messageid, XmppConstants.DELIEVERED, XmppConstants.RECEIVED, messageType, "filePath", true);	
						ApplicationDAO.getInstance(context).updateLastMessage(groupJid.split("@")[0], System.currentTimeMillis(), messgageText, unreadMessages, true,true);	
					}
				}

			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(changeInGroup!=null){
			GroupChatActivity.updateGroupName(changeInGroup);
		}
	}

}
