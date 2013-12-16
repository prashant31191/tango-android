package or.tango.android.util;


import android.util.Log;

import or.tango.android.Config;


/**
 * @version 1.0
 * @since JDK1.6, 2011-8-12
 * 2011-11-25
 */
public final class Logger {
	/**
	 * 日志开关
	 */
	
	public static void i(String msg) {
		if(Config.DEBUG_LOG) {
			System.out.println(msg);
		}
	}
	
	public static void i(String tag, String msg) {
		if(Config.DEBUG_LOG) {
			Log.i(tag, msg);
		}
	}
	
	public static void v(String tag, String msg) {
		if(Config.DEBUG_LOG) {
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag, String msg) {
		if(Config.DEBUG_LOG) {
			Log.d(tag, msg);
		}
	}
	
	public static void w(String tag, String msg) {
		if(Config.DEBUG_LOG) {
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag, String msg) {
		if(Config.DEBUG_LOG) {
			Log.e(tag, msg);
		}
	}
	public static void e(String tag, String msg, Throwable tr){
		if(Config.DEBUG_LOG) {
			Log.e(tag, msg, tr);
		}
	}
}
