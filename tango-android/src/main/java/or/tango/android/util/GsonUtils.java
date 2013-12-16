package or.tango.android.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {
	private static Gson gson =  new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    public static Gson getGson(){
    	return gson;
    }
}
