package in.lockerapplication.bean;


public class BaseResponseBean {
	
	private String mResponseString;
	private int mErrorCode;
	private int response_error_key;
	private String mExceptionName = null;


	/**
	 * @return the mExceptionName
	 */
	public String getmExceptionName() {
		return mExceptionName;
	}

	public int getResponse_error_key() {
		return response_error_key;
	}

	public void setResponse_error_key(int response_error_key) {
		this.response_error_key = response_error_key;
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
