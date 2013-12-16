package or.tango.android.activity;

import android.widget.EditText;

import or.tango.android.Config;
import or.tango.android.R;
import or.tango.android.util.MyPreferences;


public class SettingActivity extends AbstractBaseActivity {
	private EditText host;
	
	@Override
	protected void setLayout() {
		setContentView(R.layout.activity_setting);
	}

	@Override
	protected void findViews() {
		setTitle("设置");
		host = (EditText) findViewById(R.id.hostapi_input);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		host.setText(MyPreferences.getHost(getApplicationContext()));
	}

	@Override
	protected void onPause() {
		super.onPause();
		//保存
		
		MyPreferences.saveHost(getApplicationContext(), host.getText().toString());
		
		Config.HOST=host.getText().toString();
	}
	
}
