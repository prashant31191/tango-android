package or.tango.android.activity;

import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import or.tango.android.Config;
import or.tango.android.R;
import or.tango.android.util.HttpUtil;

/**百科
 *
 */
public class BaikeActivity extends AbstractBaseActivity {
	private WebView webView;
	private ProgressBar pb ;
	private String keyWord;

	final Activity MyActivity = this;
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			if(msg.what==200){
				pb.setVisibility(View.VISIBLE);
				hideProgress();
				String data = (String)msg.obj;
				Log.i("food", "------------>"+data);
				
				try {
					JsonObject jo = new JsonParser().parse(data).getAsJsonObject();
					JsonArray ja = jo.get("data").getAsJsonArray();
					JsonObject contentObj =ja.get(0).getAsJsonObject();
					String pageContent =  contentObj.get("firstPage").getAsString();
					
					Log.i("food", "pageContent="+pageContent);
					
					//webView.loadData(pageContent, "text/html; charset=UTF-8", null);
					webView.loadDataWithBaseURL("http://img.baidu.com/", pageContent, "text/html", "UTF-8","");
				} catch (Exception e) {
					showToastMsg("数据加载失败");
					e.printStackTrace();
				}
				
				
				
			}
		};
	};

	@Override
	protected void setLayout() {
		setContentView(R.layout.activity_baike);
	}

	@Override
	protected void findViews() {
		keyWord = getIntent().getStringExtra(Config.ACTIVITY_KEY_WORD);
		

		pb=(ProgressBar)findViewById(R.id.pb);
		webView = (WebView) findViewById(R.id.webView);

		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				pb.setProgress(progress );
				//Logger.i("food", "progress="+progress);
				if (progress >= 100)
					pb.setVisibility(View.INVISIBLE);
			}
		});

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		showProgress("加载中");
		pb.setVisibility(View.INVISIBLE);
		getData();//请求数据
		
	}
	
	
	private void getData(){
		new Thread(){
			public void run() {
				String httpUrl = Config.HOST+Config.BAIKE;
				Map<String, Object> httpParams = new HashMap<String, Object>();
				httpParams.put("word", keyWord);
				Log.i("food", "------------>keyWord="+keyWord+";httpUrl="+httpUrl);
				Map<String, String> headerParams = new HashMap<String, String>();
				String str = HttpUtil.sendRequest(httpUrl, headerParams, httpParams, "post");
				
				Message msg = handler.obtainMessage();
				msg.what=200;
				msg.obj=str;
				handler.sendMessage(msg);
			};
		}.start();

	}
}
