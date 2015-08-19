package com.ciao.app.datamodel;


public class BaseResponseBean {
	
	private String mResponseString;
	private int mErrorCode;
	private String mExceptionName = null;


	/**
	 * @return the mExceptionName
	 */
	public String getmExceptionName() {
		return mExceptionName;
	}

	/**
	 * @param mExceptionName the mExceptionName to set
	 */
	public void setmExceptionName(String mExceptionName) {
		this.mExceptionName = mExceptionName;
	}

	/**
	 * @return the mResponseString
	 */
	public String getmResponseString() {
		return mResponseString;
	}

	/**
	 * @param mResponseString the mResponseString to set
	 */
	public void setmResponseString(String mResponseString) {
		this.mResponseString = mResponseString;
	}

	/**
	 * @return the mErrorCode
	 */
	public int getmErrorCode() {
		return mErrorCode;
	}

	/**
	 * @param mErrorCode the mErrorCode to set
	 */
	public void setmErrorCode(int mErrorCode) {
		this.mErrorCode = mErrorCode;
	}


}
