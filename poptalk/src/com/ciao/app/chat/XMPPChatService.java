package com.ciao.app.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.ping.provider.PingProvider;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.UserSearch;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.constants.XmppConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.CiaoContactBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.backgroundtasks.GetGroupDetailAsyncTask;
import com.ciao.app.netwrok.backgroundtasks.SaveReceivedMessageInDb;
import com.ciao.app.service.ContactSyncService;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.GroupChatActivity;
import com.poptalk.app.R;

/**
 * Created by rajat on 21/2/15.
 * This class is used to handle the chat related operation
 */
public class XMPPChatService extends Service implements InvitationListener{
	private static XMPPConnection xmppConnection;
	private ConnectionConfiguration conf;
	private Thread mXMPPConnectionThread;
	private Timer mTimer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SmackAndroid.init(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SmackAndroid.init(this);
		initXMPPConnection();
		return START_STICKY;
	}
/*
 * This method is used to establish a connection with chat server in background
 */
	public void initXMPPConnection() {
		if (AppSharedPreference.getInstance(this).getCommUser() != null && AppSharedPreference.getInstance(this).getAppContactSynced()) {
			if (mXMPPConnectionThread == null || (!mXMPPConnectionThread.isAlive() && (xmppConnection == null || !xmppConnection.isAuthenticated() || !xmppConnection.isConnected()))) {
				mXMPPConnectionThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							if (xmppConnection == null) {
								createNewConnection();
								connectAndAuth();
							} else if (!xmppConnection.isConnected() || !xmppConnection.isAuthenticated()) {
								connectAndAuth();
							}

							getRosterEntires();
						} catch (Exception e) {
							// TODO: handle exception
						}


					}
				});
				mXMPPConnectionThread.start();
			}
		}
	}

	public static XMPPConnection getConnection() {
		return xmppConnection;
	}
	private void setConnection(XMPPConnection xmppConnection) {
		this.xmppConnection = xmppConnection;
	}
  /*
   * This method is used to register the user on chat server
   */
	public String register(XMPPConnection con) {
		try {
			if (con == null || AppSharedPreference.getInstance(this).getCommUser() == null) {
				return "0";
			} else {
				SASLAuthentication.supportSASLMechanism("PLAIN", 0);
				String userId = AppSharedPreference.getInstance(this).getUserPhoneNumber();
				userId = AppUtils.formatPhoneNumber(this, userId);
				Registration reg = new Registration();
				reg.setType(IQ.Type.SET);
				reg.setTo(con.getServiceName());
				reg.setUsername(AppSharedPreference.getInstance(this).getCommUser());// Note the createAccount registration, parameter is username, not Jid, is the front part of the "@".
				reg.setPassword(AppSharedPreference.getInstance(this).getCommSecurity());
				reg.addAttribute("android", "geolo_createUser_android");// This addAttribute cannot be empty, otherwise an error. So do mark is Android mobile phone created！！！！！
				PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
				PacketCollector collector = con.createPacketCollector(filter);
				con.sendPacket(reg);
				IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
				// Stop queuing results
				collector.cancel();// Stop request results (whether successful results)
				if (result == null) {
					Log.e("RegistActivity", "No response from server.");
					return "0";
				} else if (result.getType() == IQ.Type.RESULT) {
					AppSharedPreference.getInstance(this).setRegisteredOnChatServer(true);
					login(xmppConnection);
					return "1";
				} else { // if (result.getType() == IQ.Type.ERROR)
					if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
						Log.e("RegistActivity", "IQ.Type.ERROR: " + result.getError().toString());
						AppSharedPreference.getInstance(this).setRegisteredOnChatServer(true);
						login(xmppConnection);
						return "2";
					} else {
						Log.e("RegistActivity", "IQ.Type.ERROR: "
								+ result.getError().toString());
						return "3";
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}
	}
     /*
      * This method is used to login on the chat server ,after successful login user status will be online and user can chat.
      */
	public void login(XMPPConnection xmppConnection) {
		if (AppSharedPreference.getInstance(this).getUserID() != "-1") {
			try {
				xmppConnection.login(AppSharedPreference.getInstance(this).getCommUser(), AppSharedPreference.getInstance(this).getCommSecurity());
				AppSharedPreference.getInstance(this).setLoggedInOnChatServer(true);
				setConnection(xmppConnection);
				setPacketListener();
				registerRosterListener();
				reconnectToXmpp();
				MultiUserChat.addInvitationListener(getConnection(), this);
				joinGroupChatRoom();
			} catch (XMPPException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}


   /*
    * This method is used to re-establish the connection with  chat server if old connection was break due to any reason like network disconnection.
    */
	private void reconnectToXmpp() {
		if (mTimer == null) {
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						if (xmppConnection != null && xmppConnection.isConnected()) {
							joinGroupChatRoom();
						} else {
							initXMPPConnection();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			}, 0, 10000);
		}
	}

	private void registerRosterListener() {
		Roster roster = getRoster();
		roster.addRosterListener(new RosterListener() {
			@Override
			public void entriesAdded(Collection<String> strings) {
				//Log.e("Friend = ", strings.toString());
			}

			@Override
			public void entriesUpdated(Collection<String> strings) {
				//Log.e("Friend = ", strings.toString());
			}

			@Override
			public void entriesDeleted(Collection<String> strings) {
				//Log.e("Friend = ", strings.toString());
			}

			@Override
			public void presenceChanged(Presence presence) {
				//Log.e("Friend = ", presence.toString());
			}
		});

	}


	public Roster getRoster() {
		return xmppConnection.getRoster();
	}

	public void getRosterEntires() {
		ArrayList<UserEntry> arrayList = new ArrayList<UserEntry>();
		Roster roster = getRoster();
		roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
		if (roster != null) {
			Collection<RosterEntry> entries = roster.getEntries();
			getContentResolver().delete(Uri.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING), null, null);
			arrayList.clear();
			for (RosterEntry rosterEntry : entries) {
				parseRosterEntry(rosterEntry);


			}

		}
	}

	private void parseRosterEntry(RosterEntry rosterEntry) {
		String username = rosterEntry.getName();
		String userId = rosterEntry.getUser();
		Presence presence = getRoster().getPresence(rosterEntry.getUser());
		boolean isavailability = presence.isAvailable();
		String Userpresence = "";
		Presence.Type type = presence.getType();
		Log.e("AVAILIBILITY", type.toString());
		if (type == Presence.Type.available) {
			Userpresence = XmppConstants.AVAILABLE;
		} else {
			Userpresence = XmppConstants.UNAVAILABLE;
		}
		saveRoasterInDb(username, userId, Userpresence, null);
	}
   /*
    * This method is used to register the listener for incoming one-to-one and grou chat.
    */
	private void setPacketListener() {
		try {
			if (XMPPChatService.getConnection() != null && XMPPChatService.getConnection().isConnected()) {
				PacketFilter packetFilter = new MessageTypeFilter(Message.Type.chat);
				PacketFilter packetFilter1 = new MessageTypeFilter(Message.Type.groupchat);
				XMPPConnection xmppConnection = XMPPChatService.getConnection();
				xmppConnection.addPacketListener(new PacketListener() {
					@Override
					public void processPacket(Packet packet) {
						Message message = (Message) packet;
						if (message.getBody() != null) {
							receiveMessageAndStoreInDB(packet);
						}
					}
				}, packetFilter);

				xmppConnection.addPacketListener(new PacketListener() {
					@Override
					public void processPacket(Packet packet) {
						final Message message = (Message) packet;
						if (message.getBody() != null) {
							receiveGroupMessageAndStoreInDB(packet);
						}
					}
				}, packetFilter1);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/*
	 * This method is used to receive incoming group message,parse and store it  database.
	 */
	protected void receiveGroupMessageAndStoreInDB(Packet packet){
		new SaveReceivedMessageInDb(XMPPChatService.this, (Message)packet).execute();
	}
	/*
	 * This method is used to store the user friends entries in database.
	 */
	public void saveRoasterInDb(String name, String userId, String availablity, String phoneNumber) {
		ApplicationDAO.getInstance(getApplicationContext()).createChatUser(this, name, userId, availablity, phoneNumber);
	}

	/*
	 *This method is used to receive incoming one to one message,parse and store it  database.
	 */
	private void receiveMessageAndStoreInDB(Packet packet) {
		Message message = (Message) packet;
		String messgageText;
		String fromJid = StringUtils.parseBareAddress(message.getFrom());
		String messageid = StringUtils.parseBareAddress(message.getPacketID());
		String phoneNumber = message.getProperty("phone_number").toString();
		String messageType = message.getSubject();
		if(XmppConstants.TYPE_IMAGE.equalsIgnoreCase(messageType)){
			messgageText = message.getBody();
		}else {
			messgageText = StringUtils.parseBareAddress(message.getBody());
		}
		//Log.e("jeberId.....", fromJid + "Message-----" + messgageText);
		ApplicationDAO.getInstance(this).saveChatMessageInDb(this, messgageText, fromJid, phoneNumber, messageid, XmppConstants.DELIEVERED, XmppConstants.RECEIVED,messageType,"filePath",true);

	}

	/*
	 * This method is used to configure the xmpp connection for this application.
	 */
	private static void configure(ProviderManager providerManager) {

		providerManager.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
		providerManager.addIQProvider("ping", "urn:xmpp:ping", new PingProvider());
		// XEP-184 Message Delivery Receipts
		providerManager.addExtensionProvider("received", "urn:xmpp:receipts", new DeliveryReceipt.Provider());
		providerManager.addExtensionProvider("request", "urn:xmpp:receipts", new DeliveryReceiptRequest.Provider());// added
		try {
			providerManager.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
		}
		// Roster Exchange
		providerManager.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());
		// Message Events
		providerManager.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());
		// Chat State
		providerManager.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		providerManager.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		providerManager.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		providerManager.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		providerManager.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		// XHTML
		providerManager.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
		// Group Chat Invitations
		providerManager.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());
		// Service Discovery # Items
		providerManager.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
		// Service Discovery # Info
		providerManager.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
		// Data Forms
		providerManager.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		providerManager.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
		// MUC Admin
		providerManager.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
		// MUC Owner
		providerManager.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
		// Delayed Delivery
		providerManager.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

		// Version
		try {
			providerManager.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}
		// VCard
		providerManager.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		providerManager.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		providerManager.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
		// Last Activity
		providerManager.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		providerManager.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		providerManager.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		providerManager.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());
		// FileTransfer
		providerManager.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
		providerManager.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
		providerManager.addIQProvider("open", "http://jabber.org/protocol/ibb", new OpenIQProvider());
		providerManager.addIQProvider("close", "http://jabber.org/protocol/ibb", new CloseIQProvider());
		providerManager.addExtensionProvider("data", "http://jabber.org/protocol/ibb", new DataPacketProvider());
		providerManager.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		providerManager.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
		providerManager.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
		providerManager.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
		providerManager.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
		providerManager.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
		providerManager.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
	}

	
	/*
	 * This method is used to create a new connection if no old connection is there.
	 */
	private XMPPConnection createNewConnection() {
		conf = new ConnectionConfiguration(XmppConstants.HOST);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			conf.setTruststoreType("AndroidCAStore");
			conf.setTruststorePassword(null);
			conf.setTruststorePath(null);

		} else {
			conf.setTruststoreType("BKS");
			String path = System.getProperty("javax.net.ssl.trustStore");
			if (path == null) {
				path = System.getProperty("java.home") + File.separator + "etc" + File.separator + "security" + File.separator + "cacerts.bks";
			}
			conf.setTruststorePath(path);
		}
		conf.setReconnectionAllowed(true);
		conf.setSendPresence(true);
		try {
			xmppConnection = new XMPPConnection(conf);

			xmppConnection.addConnectionListener(new ConnectionListener() {

				@Override
				public void reconnectionSuccessful() {
					Log.e("connection listner ", "<<<<<<<< Reconnection Successful ");
				}

				@Override
				public void reconnectionFailed(Exception arg0) {
					arg0.printStackTrace();
					Log.e("connection listner ", "<<<<<<<< Reconnection failed ");
				}

				@Override
				public void reconnectingIn(int arg0) {
					Log.e("connection listner ", "<<<<<<<< Reconnecting  " + arg0);
				}

				@Override
				public void connectionClosedOnError(Exception arg0) {
					arg0.printStackTrace();
					Log.e("connection listner ", "<<<<<<<< Connection closed on errror ");
					xmppConnection = null;
					/*mTimer.cancel();
                    mTimer.purge();
                    mTimer = null;*/

				}

				@Override
				public void connectionClosed() {
					Log.w("connection listner ", "Connection closed");
					xmppConnection = null;
					/* mTimer.cancel();
                    mTimer.purge();
                    mTimer = null;*/
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmppConnection;
	}

	
	/*
	 * This method is used to Authenticate the connection with chat server
	 */
	private void connectAndAuth() {
		try {
			configure(ProviderManager.getInstance());
			if (xmppConnection != null && !xmppConnection.isConnected()) {
				xmppConnection.connect();
				if (xmppConnection != null && xmppConnection.isConnected()) {
					/*if (!AppSharedPreference.getInstance(this).getRegisteredOnChatServer()) {
						
					} else {
						login(xmppConnection);
					}*/
					register(xmppConnection);
					login(xmppConnection);


				}
			}
		} catch (XMPPException e) {
			e.printStackTrace();
			setConnection(null);
		}

	}
	/**
	 * This method is used to create group chat
	 * @param groupName - name of the group
	 * @param friendListToSendInivitation - list of the friends to send invitation
	 * @param invitationMessage 
	 */
	public static void createGroupChatRoom(String groupName,String groupId, List<String> friendListToSendInivitation,String invitationMessage,String myJid){

		if(xmppConnection!=null){

			final MultiUserChat multiUserChat = new MultiUserChat(xmppConnection, groupId+XmppConstants.GROUP_HOST);
			try {
				multiUserChat.create(groupName);
				Form form = multiUserChat.getConfigurationForm();
				Form submitForm = form.createAnswerForm();
				for (Iterator fields = form.getFields(); fields.hasNext();) {
					FormField field = (FormField) fields.next();
					if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
						submitForm.setDefaultAnswer(field.getVariable());
					}
				}
				List<String> owners = new ArrayList<String>();
				owners.add(xmppConnection.getUser());
				submitForm.setAnswer("muc#roomconfig_roomowners", owners);
				submitForm.setAnswer("muc#roomconfig_roomname", groupName);
				submitForm.setAnswer("muc#roomconfig_persistentroom", true);
				submitForm.setAnswer("muc#roomconfig_publicroom", true);
				multiUserChat.sendConfigurationForm(submitForm);
				multiUserChat.join(xmppConnection.getUser(), "Admin");
				for(int i = 0;i<friendListToSendInivitation.size();i++){
					if(!friendListToSendInivitation.get(i).equalsIgnoreCase(myJid)){
						String friendJBRId = friendListToSendInivitation.get(i)+"@"+XmppConstants.HOST;
						multiUserChat.invite(friendJBRId, invitationMessage);
					}
				}
               /* multiUserChat.addParticipantListener(new PacketListener() {
					
					@Override
					public void processPacket(Packet packet) {
						Log.e("Packet update ", "--------");
					}
				});*/
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}else{

		}


	}

	/*
	 * This method is used to join all the group(if already a member of this) after reconnection with chat server.
	 */
	public void joinGroupChatRoom(){
		List<String> allGroupJoined = ApplicationDAO.getInstance(XMPPChatService.this).getAllGroupJoined();
		if(xmppConnection!=null && xmppConnection.isConnected()){
			for(int i=0;i<allGroupJoined.size();i++){
				final MultiUserChat multiUserChat = new MultiUserChat(xmppConnection, allGroupJoined.get(i)+XmppConstants.GROUP_HOST);
				try {
					multiUserChat.join(xmppConnection.getUser(), "");
				} catch (XMPPException e) {
					e.printStackTrace();
				}	
			}	
		}
	}
/*
 * This method is used add new members to existing group chat.
 */
	public static void addParticipantToGroup(String groupJid,String participantJid,String groupName){
		if(xmppConnection!=null && xmppConnection.isConnected()){
			MultiUserChat multiUserChat = new MultiUserChat(xmppConnection, groupJid);
			multiUserChat.invite(participantJid+"@"+XmppConstants.HOST, groupName);
            
		}	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jivesoftware.smackx.muc.InvitationListener#invitationReceived(org.jivesoftware.smack.Connection, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.jivesoftware.smack.packet.Message)
	 */
	@Override
	public void invitationReceived(Connection connection, String room, String inviter,String reason, String unKnown, Message message) {
		final MultiUserChat multiUserChat  = new MultiUserChat(getConnection(), room);
		try {
			multiUserChat.join(xmppConnection.getUser());
			String groupName = "";
			String groupId = room.split("@")[0];
			String[] temp =  groupId.split("_");
			for(int i =0;i<temp.length-1;i++){
				groupName = groupName+temp[i]+" ";
			}
			if(!AppSharedPreference.getInstance(getApplicationContext()).getAppContactSyncing()){
				if(groupId!=null &&groupId.length()>0)
					new GetGroupDetailAsyncTask(XMPPChatService.this, groupId).execute();	
			}

		} catch (XMPPException e) {
			e.printStackTrace();
		}


	}

/*
 * This method is used to update the existing group name
 */

	public static void updateGroupName(String groupName,String groupJid){
		if(xmppConnection!=null && xmppConnection.isConnected()){
			MultiUserChat multiUserChat = new MultiUserChat(xmppConnection, groupJid+XmppConstants.GROUP_HOST);
			try {
				multiUserChat.sendMessage(AppConstants.EDIT_GROUP_NAME+groupName);
				multiUserChat.leave();
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
