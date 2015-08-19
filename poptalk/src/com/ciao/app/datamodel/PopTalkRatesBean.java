package com.ciao.app.datamodel;
//This method is used to store info about poptalk call and sms rates 
public class PopTalkRatesBean {
	String smsRate,mobileRate,landlineRate;

	public PopTalkRatesBean(String smsRate, String mobileRate,String landlineRate) {
		this.smsRate = smsRate;
		this.mobileRate = mobileRate;
		this.landlineRate = landlineRate;
	}

	public String getSmsRate() {
		return smsRate;
	}

	public void setSmsRate(String smsRate) {
		this.smsRate = smsRate;
	}

	public String getMobileRate() {
		return mobileRate;
	}

	public void setMobileRate(String mobileRate) {
		this.mobileRate = mobileRate;
	}

	public String getLandlineRate() {
		return landlineRate;
	}

	public void setLandlineRate(String landlineRate) {
		this.landlineRate = landlineRate;
	}
}
