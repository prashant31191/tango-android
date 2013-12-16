package or.tango.android.activity;

import or.tango.android.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector.OnGestureListener;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.widget.TextView;

public class WelcomeActivity extends Activity implements OnGestureListener {

	/** 
     * Called when the activity is first created. 
     */  
    @Override  
    public void onCreate(Bundle icicle) {  
        super.onCreate(icicle);  
        getWindow().setFormat(PixelFormat.RGBA_8888);  
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);  
        setContentView(R.layout.activity_welcome);  
        //Display the current version number   
        PackageManager pm = getPackageManager();  
        try {  
        	PackageInfo pi = pm.getPackageInfo("or.tango.android", 0);  
            TextView versionNumber = (TextView) findViewById(R.id.versionNumber);  
            versionNumber.setText("Version " + pi.versionName);  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }  
        new Handler().postDelayed(new Runnable() {  
            public void run() {  
            	/* Create an Intent that will start the Main WordPress Activity. */
            	Intent mainIntent = new Intent(WelcomeActivity.this, WizardActivity.class);  
            	startActivity(mainIntent);  
            	finish();  
            }  
        }, 2900); //3000 for release   
    }

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}  

}
