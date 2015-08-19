package com.ciao.app.inappbilling;

/**
 * This is an object representation of the Passport object in Google Play Here
 * you can store the items ID (its SKU) it's price and any other fields
 * <br/>
 * <b>Id's</b> for Sku should be start with <b>SKU_INAPP</b> for InApp product and <b>SKU_SUBS</b> for InAPP Subscription.
 * 
 * <br/<br/>For Testing purpose you can change the SKU_ID with <b>android.test.purchased</b> but not for subscription 
 * @author Vivek Kumar(Appstudioz)
 
 */
public class SkuIds {

	public static final String SKU_INAPP_5 = "app_inapp_5";//android.test.purchased 
	public static final String SKU_INAPP_10 = "android.test.purchased";
	public static final String SKU_INAPP_CALL_1000 = "calling_messaging_1000";
	public static final String SKU_INAPP_CALL_2500 = "calling_messaging_2500";
	public static final String SKU_INAPP_CALL_350 = "calling_messaging_350";
	public static final String SKU_INAPP_CALL_TEST = "calling_messaging_50";
	public static final String SKU_SUBS_MONTHLY = "app_inapp_subs1";
	public static final String SKU_SUBS_ONE_DAY = "app_inapp_subs_1day";
}
