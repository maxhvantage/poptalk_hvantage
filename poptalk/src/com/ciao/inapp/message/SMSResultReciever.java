package com.ciao.inapp.message;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class SMSResultReciever extends ResultReceiver{

	private Receiver mReceiver;

	public interface Receiver {
		public void onReceiveResult(int resultCode, Bundle resultData);
	}

	public SMSResultReciever(Handler handler) {
		super(handler);

	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		super.onReceiveResult(resultCode, resultData);
		if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
	}

	public void setReceiver(Receiver receiver) {
		mReceiver = receiver;
	}


}
