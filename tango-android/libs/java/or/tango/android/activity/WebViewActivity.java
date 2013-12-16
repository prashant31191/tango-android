package or.tango.android.activity;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import or.tango.android.Config;
import or.tango.android.R;

public class WebViewActivity extends AbstractBaseActivity {
	private WebView webView;
	private String url;
	private ProgressBar pb ;
	final Activity MyActivity = this;

	@Override
	protected void setLayout() {

		setContentView(R.layout.activity_webview);
		url = getIntent().getStringExtra(Config.ACTIVITY_KEY_URL);
		android.util.Log.i("food", "------------>url=" + url);
	}

	@Override
	protected void findViews() {
		setTitle("结果查看");
		setLeftVisible(true);

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
		webView.loadUrl(url);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (webView.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	
}
