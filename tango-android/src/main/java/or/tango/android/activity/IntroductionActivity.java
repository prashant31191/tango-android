package or.tango.android.activity;

import or.tango.android.R;


public class IntroductionActivity extends AbstractBaseActivity {
	
	@Override
	protected void setLayout() {
		setContentView(R.layout.activity_introduction);
	}

	@Override
	protected void findViews() {
		setTitle("Tango简介");
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//保存
		
	}
	
}
