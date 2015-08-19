package com.ciao.app.constants;



/**
 * Created by rajat on 11/2/15.
 * This class is used define table name,columns in tables,query to create table etc.
 */
public class AppDatabaseConstants {


	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "ciao_app.db";
	public static final String TABLE_CONTACTS = "contacts";
	public static final String TABLE_PHONE = "phone";
	public static final String TABLE_EMAIL = "email";
	public static final String TABLE_CHAT = "chat";
	public static final String TABLE_CHAT_DETAILS = "chat_details";
	public static final String TABLE_GROUP_CHAT = "group_chat";
	public static final String TABLE_GROUP_DETAILS = "group_details";
	public static final String TABLE_GROUP_CHAT_DETAILS = "group_chat_details";
	public static final String TABLE_SMS = "sms";
	public static final String TABLE_SMS_DETAILS = "sms_details";
	public static final String TABLE_MY_CIAO = "my_ciao_number";
	public static final String TABLE_CAIO_RATES = "ciao_rates";
	public static final String TABLE_CAIO_CALLS = "ciao_call";
	


	// Contacts Table Columns names
	public static final String KEY_APP_USER_ID = "app_user_id";
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_CONTACT_PIC = "user_pic";
	public static final String KEY_CIAO_USER = "ciao_user";
	public static final String KEY_CIAO_REGISTERED = "ciao_registered_number";
	public static final String KEY_FAVORITE_CONTACT = "is_favorite";
	public static final String KEY_PHONE_NUMBER = "phone_number";
	public static final String KEY_PHONE_TYPE = "phone_type";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_EMAIL_TYPE = "email_type";
	public static final String KEY_CIAO_NUMBER = "ciao_number";
	public static final String KEY_LAST_CALL_DURATAION = "last_call";
	public static final String KEY_CALL_LIMIT_DURATAION = "call_limit";
	public static final String KEY_COMM_USER = "comm_user";


	//Chat  table columns names
	public static final String USER_ID = "user_id";
	public static final String STATUS = "status";
	public static final String USER_CHAT_ID = "chat_id";
	public static final String NAME = "name";
	private static final String COLUMN_ID = "_id";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_MESSAGE_ID = "message_id";
	public static final String COLUMN_TIME_SENT_RECEIVED = "time";
	public static final String MESSAGE_STATUS = "status";
	public static final String COLUMN_CONVERSATION_TYPE = "conversation_type";
	public static final String MESSAGE_TYPE ="message_type";
	public static final String MESSAGE_FILE_PATH ="file";
	public static final String COLUMN_UNREAD_MESSAGE = "un_read_message";
	public static final String COLUMN_LAST_MESSAGE = "last_message";
	public static final String COLUMN_IS_GROUP_CHAT = "is_group_chat";

	//Group chat tables column
	public static final String COLUMN_GROUP_NAME = "group_name";
	public static final String COLUMN_GROUP_JID = "group_jid";
	public static final String COLUMN_GROUP_ICON= "group_icon";
	public static final String CoLUMN_GROUP_USER = "user_jid";
	public static final String COLUMN_GROUP_ADMIN = "group_admin";
	public static final String COLUMN_GROUP_MEMBER = "is_group_member";
	public static final String COLUMN_GROUP_CREATION_TIME = "group_creation_time";
	
	public static final String COLUMN_COUNTRY_CODE = "country_code";
	public static final String COLUMN_SMS_RATES = "sms_rates";
	public static final String COLUMN_CALL_RATES = "call_rates";



	public static final String AUTHORITY = "com.ciao.app.provider";
	public static final String BASE_CONTENT_URI_STRING = "content://" + AUTHORITY;
	public static final String CONTACTS_CONTENT_URI_STRING = BASE_CONTENT_URI_STRING +"/" +TABLE_CONTACTS;
	public static final String PHONE_CONTENT_URI_STRING = BASE_CONTENT_URI_STRING +"/" +TABLE_PHONE;
	public static final String EMAIL_CONTENT_URI_STRING = BASE_CONTENT_URI_STRING +"/" +TABLE_EMAIL;
	public static final String CHAT_USER_CONTENT_URI_STRING = BASE_CONTENT_URI_STRING+ "/" + TABLE_CHAT;
	public static final String CHAT_USER_MESSAGE_CONTENT_URI_STRING = BASE_CONTENT_URI_STRING+ "/" + TABLE_CHAT_DETAILS;
	public static final String GROUP_CHAT_CONTENT_URI_STRING = BASE_CONTENT_URI_STRING+ "/" + TABLE_GROUP_CHAT;
	public static final String GROUP_DETAIL_CONTENT_URI_STRING = BASE_CONTENT_URI_STRING+ "/" + TABLE_GROUP_DETAILS;
	public static final String GROUP_CHAT_DETAIL_CONTENT_URI_STRING = BASE_CONTENT_URI_STRING+ "/" + TABLE_GROUP_CHAT_DETAILS;
	public static final String CONTENT_URI_SMS =  BASE_CONTENT_URI_STRING+ "/" +TABLE_SMS;
	public static final String CONTENT_URI_SMS_DETAIL = BASE_CONTENT_URI_STRING+ "/" +TABLE_SMS_DETAILS;
	public static final String CONTERT_URI_MY_CIAO = BASE_CONTENT_URI_STRING+"/"+TABLE_MY_CIAO;
	public static final String CONTERT_URI_CIAO_RATES = BASE_CONTENT_URI_STRING+"/"+TABLE_CAIO_RATES;
	public static final String CONTERT_URI_CIAO_CALLS = BASE_CONTENT_URI_STRING+"/"+TABLE_CAIO_CALLS;



	public static final int CONTACTS= 1;
	public static final int PHONE= 2;
	public static final int EMAIL = 3;
	public static final int CHAT_USER =4;
	public static final int CHAT_USER_MESSAGE=5;
	public static final int CHAT_GROUPS=6;
	public static final int CHAT_GROUPS_DETAILS=7;
	public static final int CHAT_GROUPS_CHAT_DETAILS=8;
	public static final int SMS = 9;
	public static final int SMS_DETAIL = 10;
	public static final int MY_CIAO = 11;
	public static final int CIAO_RATES = 12;
	public static final int CIAO_CALLS = 13;
	
	



	public static final String CREATE_CONTACTS_TABLE = "CREATE TABLE "
			+ TABLE_CONTACTS
			+ "("
			+ KEY_ID + " TEXT ,"
			+ KEY_NAME + " TEXT,"
			+ KEY_CONTACT_PIC + " TEXT,"
			+KEY_CIAO_USER + " TEXT,"
			+KEY_CIAO_NUMBER + " TEXT,"
			+KEY_FAVORITE_CONTACT + " TEXT,"
			+KEY_COMM_USER + " TEXT "
			+ ")";

	public static final String CREATE_PHONE_TABLE = "CREATE TABLE "
			+TABLE_PHONE
			+"("
			+KEY_ID + " TEXT ,"
			+KEY_PHONE_NUMBER +" TEXT,"
			+KEY_PHONE_TYPE +" TEXT,"
			+KEY_CIAO_REGISTERED +" TEXT,"
			+KEY_CIAO_USER +" TEXT,"
			+KEY_COMM_USER + " TEXT"
			+")";

	public static final String CREATE_EMAIL_TABLE = "CREATE TABLE "
			+TABLE_EMAIL
			+"("
			+KEY_ID + " TEXT ,"
			+KEY_EMAIL +" TEXT,"
			+KEY_EMAIL_TYPE +" TEXT"
			+")";

	public static final String CREATE_CHAT_TABLE = "CREATE TABLE "
			+ TABLE_CHAT
			+ "("
			+ COLUMN_ID
			+ " INTEGER PRIMARY KEY, "
			+KEY_APP_USER_ID
			+" TEXT,"
			+ USER_ID
			+ " TEXT,"
			+ STATUS
			+ " TEXT,"
			+ NAME
			+ " TEXT,"
			+ KEY_CONTACT_PIC
			+ " TEXT,"
			+ COLUMN_TIME_SENT_RECEIVED
			+ " TEXT, "
			+COLUMN_UNREAD_MESSAGE
			+" INTEGER,"
			+KEY_PHONE_NUMBER
			+ " TEXT, "
			+COLUMN_LAST_MESSAGE
			+ " TEXT, "
			+COLUMN_IS_GROUP_CHAT
			+" TEXT "
			+ ")";
	
	public static final String CREATE_CHAT_DETAILS_TABLE = "CREATE TABLE "
			+ TABLE_CHAT_DETAILS
			+ "("
			+ COLUMN_ID
			+ " INTEGER PRIMARY KEY, "
			+KEY_APP_USER_ID
			+" TEXT,"
			+ USER_CHAT_ID
			+ " TEXT,"
			+ COLUMN_MESSAGE
			+ " TEXT, "
			+ COLUMN_MESSAGE_ID
			+ " TEXT, "
			+ COLUMN_TIME_SENT_RECEIVED
			+ " TEXT, "
			+ COLUMN_CONVERSATION_TYPE
			+ " INTEGER, "
			+ MESSAGE_TYPE
			+ " TEXT, "
			+ MESSAGE_FILE_PATH
			+ " TEXT, "
			+ MESSAGE_STATUS
			+ " INTEGER "
			+ ")";

	public static final String CREATE_GROUP_CHAT_TABLE = "CREATE TABLE "
			+TABLE_GROUP_CHAT
			+"("
			+COLUMN_GROUP_NAME
			+" TEXT, "
			+COLUMN_GROUP_JID
			+" TEXT, "
			+COLUMN_GROUP_MEMBER
			+" TEXT, "
			+KEY_APP_USER_ID
			+" TEXT,"
			+COLUMN_GROUP_CREATION_TIME
			+" TEXT, "
			+COLUMN_GROUP_ICON
			+" TEXT "
			+")";
	public static final String CREATE_GROUP_DETAIL_TABLE = "CREATE TABLE "
			+TABLE_GROUP_DETAILS
			+"("
			+COLUMN_GROUP_JID
			+" TEXT, "
			+KEY_APP_USER_ID
			+" TEXT,"
			+COLUMN_GROUP_ADMIN
			+" TEXT, "
			+COLUMN_GROUP_MEMBER
			+" TEXT, "
			+KEY_CONTACT_PIC
			+" TEXT, "
			+USER_ID
			+" TEXT "
			+")";

	public static final String CREATE_GROUP_CHAT_DETAILS_TABLE = "CREATE TABLE "
			+ TABLE_GROUP_CHAT_DETAILS
			+ "("
			+ COLUMN_ID
			+ " INTEGER PRIMARY KEY, "
			+ COLUMN_GROUP_JID
			+ " TEXT,"
			+KEY_APP_USER_ID
			+" TEXT,"
			+ USER_CHAT_ID
			+ " TEXT,"
			+ COLUMN_MESSAGE
			+ " TEXT, "
			+ COLUMN_MESSAGE_ID
			+ " TEXT, "
			+ COLUMN_TIME_SENT_RECEIVED
			+ " TEXT, "
			+ COLUMN_CONVERSATION_TYPE
			+ " INTEGER, "
			+ MESSAGE_TYPE
			+ " TEXT, "
			+ MESSAGE_FILE_PATH
			+ " TEXT, "
			+ MESSAGE_STATUS
			+ " INTEGER "
			+ ")";
	
	public static final String CREATE_SMS_TABLE = "CREATE TABLE "
			+ TABLE_SMS
			+ "("
			+ COLUMN_ID
			+ " INTEGER PRIMARY KEY, "
			+ USER_ID
			+ " TEXT,"
			+KEY_APP_USER_ID
			+" TEXT,"
			+ NAME
			+ " TEXT,"
			+ KEY_CONTACT_PIC
			+ " TEXT,"
			+ COLUMN_TIME_SENT_RECEIVED
			+ " TEXT, "
			+COLUMN_UNREAD_MESSAGE
			+" INTEGER,"
			+COLUMN_LAST_MESSAGE
			+ " TEXT "
			+ ")";
	
	public static final String CREATE_SMS_DETAILS_TABLE = "CREATE TABLE "
			+ TABLE_SMS_DETAILS
			+ "("
			+ COLUMN_ID
			+ " INTEGER PRIMARY KEY, "
			+ USER_CHAT_ID
			+ " TEXT,"
			+KEY_APP_USER_ID
			+" TEXT,"
			+ COLUMN_MESSAGE
			+ " TEXT, "
			+ COLUMN_TIME_SENT_RECEIVED
			+ " TEXT, "
			+ COLUMN_CONVERSATION_TYPE
			+ " INTEGER, "
			+ MESSAGE_STATUS
			+ " INTEGER "
			+ ")";
	public static final String CREATE_MY_CIAO_TABLE= "CREATE TABLE "
			+TABLE_MY_CIAO
			+"("
			+ COLUMN_ID
			+ " INTEGER PRIMARY KEY, "
			+KEY_APP_USER_ID
			+" TEXT,"
			+KEY_CIAO_NUMBER
			+" TEXT "
			+")";
	
	public static final String CREATE_CIAO_RATES = "CREATE TABLE "
			+TABLE_CAIO_RATES
			+"("
			+COLUMN_COUNTRY_CODE
			+" TEXT, "
			+COLUMN_CALL_RATES
			+" TEXT, "
			+COLUMN_SMS_RATES
			+" TEXT "
			+")";
	public static final String CREATE_CIAO_CALLS_TABLE = "CREATE TABLE "
			+TABLE_CAIO_CALLS
			+"("
			+KEY_LAST_CALL_DURATAION
			+" TEXT, "
			+KEY_CALL_LIMIT_DURATAION
			+" TEXT "
			+")";

}
