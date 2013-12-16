package or.tango.android.activity;

import or.tango.android.R;
import android.content.Intent;
import android.util.Log;
import android.view.View;  
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
public class WizardActivity extends AbstractBaseActivity implements View.OnClickListener{  
    /** Called when the activity is first created. */  
	@Override
	protected void setLayout() {
		setContentView(R.layout.activity_wizard);
	}

	@Override
	protected void findViews() {
		setTitle("Welcome");
		//findViewById(R.id.tr1).setOnClickListener(this);
		//findViewById(R.id.tr2).setOnClickListener(this);
		findViewById(R.id.tr3).setOnClickListener(this);
		ImageView iv1 = (ImageView)findViewById(R.id.tr1);		
		iv1.setOnClickListener(this);
		ImageView iv2 = (ImageView)findViewById(R.id.tr2);		
		iv2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.tr1){
			startActivity(new Intent(this, MainActivity.class));
		}
		if(v.getId() == R.id.tr2){
			
		}
		if(v.getId()==R.id.tr3){
			startActivity(new Intent(this, IntroductionActivity.class));
		}
		
	}
        
        
        
    
}  

