package or.tango.android.view;


import or.tango.android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadingView{
	
	private Context context;
	public LoadingView(Context context){
		this.context = context;
	}
	
	public LinearLayout addLoadingLinearLayout(){
		LayoutInflater mInflater = ((Activity) context).getLayoutInflater();
		LinearLayout layout = new LinearLayout(context);
		View view = mInflater.inflate(R.layout.loading, layout,
				true);
		ImageView iView = (ImageView)view.findViewById(R.id.progressimg);
		final AnimationDrawable animDrawable = (AnimationDrawable)context.getResources().getDrawable(R.anim.loading);
		iView.setBackgroundDrawable(animDrawable);
		iView.post(new Runnable(){
	           @Override
	           public void run() {
	               animDrawable.start();
	           }
	        });
		return layout;
	}
	
	
	
}