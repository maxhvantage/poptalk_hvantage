package com.ciao.app.inappbilling;



import android.util.Log;

/**
 * Class is used for printing the log message.
 * @author Vivek Kumar(Appstudioz)
 *
 */
public class Logger {

	public static boolean logstatus = true;
	public static String TAG = "InAppBilling";

	private Logger() {
	}

	public static void warn(final String s) {
		if (logstatus) {
			Log.w(TAG,":->"+ s);
		}
	}

	public static void warn(final String s, final Throwable throwable) {
		if (logstatus) {
			Log.w(TAG, s, throwable);
		}
	}

	public static void warn(final Throwable throwable) {
		if (logstatus) {
			Log.w(TAG, throwable);
		}
	}

	public static void verbose(final String s) {
		if (logstatus) {
			Log.v(TAG,":->"+ s);
		}
	}

	public static void debug(final String s) {
		if (logstatus) {
			Log.d(TAG, ":->"+s);
		}
	}

	public static void info(final String s) {
		if (logstatus) {
			Log.i(TAG, ":->" + s);
		}
	}

	public static void info(final String s, final Throwable throwable) {
		if (logstatus) {
			Log.i(TAG, s, throwable);
		}
	}

	public static void error(final String s) {
		if (logstatus) {
			Log.e(TAG,":->"+ s);
		}
	}

	public static void error(final Throwable throwable) {
		if (logstatus) {
			Log.e(TAG, null, throwable);
		}
	}

	public static void error(final String s, final Throwable throwable) {
		if (logstatus) {
			Log.e(TAG, s, throwable);
		}
	}

}
