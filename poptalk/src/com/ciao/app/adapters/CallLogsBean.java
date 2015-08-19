package com.ciao.app.adapters;

import android.os.Parcel;
import android.os.Parcelable;

public class CallLogsBean implements Parcelable {

	private int callType;
	private String contactName, imagePath, callTime, contactNumber,
			callDuration;

	public CallLogsBean(String contactName, String imagePath, String callTime,
			String contactNumber, String callDuration) {
		this.callDuration = callDuration;
		this.callTime = callTime;
		this.imagePath = imagePath;
		this.contactName = contactName;
		this.contactNumber = contactNumber;

	}

	public CallLogsBean() {
	}

	public CallLogsBean(Parcel in) {
		this.callDuration = in.readString();
		this.callTime = in.readString();
		this.imagePath = in.readString();
		this.contactName = in.readString();
		this.contactNumber = in.readString();

	}

	private boolean isBlocked;

	public String getCallDuration() {
		return callDuration;
	}

	public String getCallTime() {
		return callTime;
	}

	public int getCallType() {
		return callType;
	}

	public String getContactName() {
		return contactName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public String getImagePath() {
		return imagePath;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public void setCallDuration(String callDuration) {
		this.callDuration = callDuration;
	}

	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}

	public void setCallType(int callType) {
		this.callType = callType;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<CallLogsBean> CREATOR = new Parcelable.Creator<CallLogsBean>() {
		public CallLogsBean createFromParcel(Parcel in) {
			return new CallLogsBean(in);
		}

		public CallLogsBean[] newArray(int size) {
			return new CallLogsBean[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.callDuration);
		dest.writeString(this.callTime);
		dest.writeString(this.imagePath);
		dest.writeString(this.contactName);
		dest.writeString(this.contactNumber);
	}

}
