package eu.fbk.ict4g.samo.utils;

import android.util.Log;

/**
 * @author pietro
 *
 */
public class SAMoLog {
	private static final boolean debug = true;

	/**
	 * Wrapper method for <i>verbose</i> messages
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void v(String tag, String msg) {
		if (debug)
			Log.v(tag, msg);
	}

	/**
	 * Wrapper method for <i>debug</i> messages
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg) {
		if (debug)
			Log.d(tag, msg);
	}
	
	/**
	 * Wrapper method for <i>warning</i> messages
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void w(String tag, String msg) {
		Log.w(tag, msg);
	}
	
	/**
	 * Wrapper method for <i>error</i> messages
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag, String msg) {
		Log.e(tag, msg);
	}
}
