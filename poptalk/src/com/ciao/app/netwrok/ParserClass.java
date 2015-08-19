package com.ciao.app.netwrok;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.datamodel.BaseResponseBean;
import com.ciao.app.datamodel.GroupInfoBean;
import com.ciao.app.datamodel.MemberBean;
import com.ciao.app.datamodel.NumberResponseBean;

/**
 * Created by rajat on 3/2/15.
 */
public class ParserClass {
	public static  ParserClass parserClass;

	public static ParserClass getInstance(Context context){
		if(parserClass==null){
			parserClass = new ParserClass(context);
		}
		return parserClass;
	}

	public ParserClass(Context context) {
	}

	/*public String parseSignupResponse(String serverResponse){
        //{"response_string":"Signup successfully","error_code":"0","response_error_key":"0","result":{"customer_id":"2","first_name":"ghhg","gender":"female","referral_code":"hj","email":"hhg@ghj.com","country_id":"91","state_id":"91","phone":"9999319927"}}

    }*/
	public NumberResponseBean ParseNumber(String response) {
		NumberResponseBean responseBean = new NumberResponseBean();
		List<String> beanList= new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray jrr= jsonObject.getJSONArray("numbers");

			responseBean.setmErrorCode(0);
			for (int i = 0; i < jrr.length(); i++){

				JSONObject object= jrr.getJSONObject(i);
				beanList.add(object.getString(String.valueOf(AppNetworkConstants.NetworkKeys.msisdn)));
			}

			responseBean.setBeanlist(beanList);

		} catch (JSONException e){
			responseBean.setmErrorCode(1);
			response = "ParsingException";
			responseBean.setmExceptionName(response);
			e.printStackTrace();
		}
		return responseBean;
	}

	public BaseResponseBean ParseBuyNumber(String response) {
		BaseResponseBean responseBean = new BaseResponseBean();
		try {
			JSONObject jsonObject = new JSONObject(response);
			String isError= jsonObject.getString("error-code");

			if(isError.equalsIgnoreCase("200")){
				responseBean.setmErrorCode(0);
				responseBean.setmExceptionName(jsonObject.getString("error-code-label"));

			}else{
				responseBean.setmErrorCode(1);
				responseBean.setmExceptionName(jsonObject.getString("error-code-label"));
			}

		} catch (JSONException e){
			responseBean.setmErrorCode(1);
			response = "ParsingException";
			responseBean.setmExceptionName(response);
			e.printStackTrace();
		}
		return responseBean;
	}


	public BaseResponseBean ParseCall(String response) {
		BaseResponseBean responseBean = new BaseResponseBean();
		try {
			JSONObject jsonObject = new JSONObject(response);
			String status= jsonObject.getString("status");

			responseBean.setmErrorCode(Integer.parseInt(status));

		} catch (JSONException e){
			responseBean.setmErrorCode(1);
			response = "ParsingException";
			responseBean.setmExceptionName(response);
			e.printStackTrace();
		}
		return responseBean;
	}

	public GroupInfoBean parseGroupInfo(String response) {
		GroupInfoBean groupInfoBean = new GroupInfoBean();
		List<MemberBean> memberBeanList = new ArrayList<MemberBean>();
		try {
			JSONObject jsonObject = new JSONObject(response);
			String errorCode = jsonObject.getString("error_code");
			if(errorCode.equalsIgnoreCase("0")){
				JSONObject resultJsonObject = jsonObject.getJSONObject("result");
				String groupOwnerID = resultJsonObject.getString("owner_id");
				String groupJID = resultJsonObject.getString("group_jid");
				String groupName = resultJsonObject.getString("group_name");
				String groupImage = resultJsonObject.getString("group_image");
				JSONArray memberArray = resultJsonObject.getJSONArray("memberSet");
				groupInfoBean.setGroupName(groupName);
				groupInfoBean.setGroupImage(groupImage);
				groupInfoBean.setGroupJID(groupJID);
				groupInfoBean.setGroupOwnerID(groupOwnerID);
				for(int i=0;i<memberArray.length();i++){
				 MemberBean memberBean = new MemberBean();
                 JSONObject memberJsonObject = memberArray.getJSONObject(i);
                 String memberJID = memberJsonObject.getString("member_id");
                 String memberProfilePic = memberJsonObject.getString("profile_image");
                 memberBean = new MemberBean();
                 memberBean.setMemberJID(memberJID);
                 memberBean.setProfilePic(memberProfilePic);
                 if(memberJID.equalsIgnoreCase(groupOwnerID)){
                	 memberBean.setAdmin(true);
                 }
                 memberBeanList.add(memberBean);
                 
				}
				groupInfoBean.setMemberList(memberBeanList);
			}
		} catch (JSONException e) {
			
		}
		return groupInfoBean;

	}
}
