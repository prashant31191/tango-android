package or.tango.android;


public class Config {
	
	public static boolean DEBUG_LOG=true;//调试日志输出开关
	public static String INSIGHT_FOLDER="/insightech";//应用保存目录
	public static final String ACTIVITY_KEY_WORD="keywords";//ACITIVITY传递的keyword
	public static final String ACTIVITY_KEY_URL="url";//ACITIVITY传递的keyword
		
	/******************************接口服务器路径*****************************/
	public static  String HOST = "http://opencode.baidu.com/tango/api?"; //用于本地未设置ip时的默认ip
	
	public static final String BAIKE = "action=Search&method=baike";
	public static final String NEWS ="action=Search&method=news";
	public static final String ZHIDAO = "action=Search&method=zhidao";
	public static final String FILE = "action=File&method=upload";
}
