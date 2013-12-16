package or.tango.android.pojo;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchData {
 @Expose@SerializedName("success")
 public int state;
 @Expose@SerializedName("data")
 public ArrayList<Item> data;
 
 
 public static class Item{
	 @Expose@SerializedName("title")
	public String title;
	 @Expose@SerializedName("url")
	public String url;
 }
}
