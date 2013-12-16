package or.tango.android.util;

import or.tango.android.Config;

import android.content.Context;

/**
 * ip缓存
 * 
 * 
 */
public class MyPreferences {

	private static final String MY_SHAREDPREFERENCES_NAME = "myPre";

	private static final String KEY_HOST = "host";

	public static String getHost(Context context) {
		if (context == null)
			return Config.HOST;

		String host = context.getSharedPreferences(MY_SHAREDPREFERENCES_NAME,
				Context.MODE_WORLD_READABLE).getString(KEY_HOST, "");

		if ("".equals(host))
			return Config.HOST;
		return host;
	}

	public static boolean saveHost(Context context, String host) {
		if (context == null || host == null || "".equals(host))
			return false;

		return context
				.getSharedPreferences(MY_SHAREDPREFERENCES_NAME,
						Context.MODE_WORLD_WRITEABLE).edit()
				.putString(KEY_HOST, host).commit();
	}

}
