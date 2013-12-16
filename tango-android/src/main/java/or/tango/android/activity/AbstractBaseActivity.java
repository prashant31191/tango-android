package or.tango.android.activity;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import or.tango.android.R;
import or.tango.android.view.LoadingView;

public abstract class AbstractBaseActivity extends Activity {
	private Button left;//左边按钮
	private TextView center;//头部中间文字
	private Button right;//右侧按钮
	
	private LinearLayout progressLL;//进度条LL
	private TextView errmsgTextView;//进度条图片
	private ImageView progressImgview;//进度条文字
	private AnimationDrawable loadingAnimation;
	
	protected abstract void setLayout();
	protected abstract void findViews();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置样式 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setLayout();
		init();//初始化头部view
		findViews();
	}
	
	//-------------------头部view代码
	private void init() {
		left = (Button) findViewById(R.id.left_btn);
		center = (TextView) findViewById(R.id.center_tv);
		right = (Button) findViewById(R.id.right_btn);
		
		//左侧按钮为默认为返回键，添加默认事件
		if(left!=null)
		left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();//返回
			}
		});
	}
	
	/**设置顶部标题
	 * @param title
	 */
	protected void setTitle(String title) {
		if(center!=null)
			center.setText(title);
	}
	
	/**头部右侧按钮文字
	 * @param str
	 */
	protected void setLeftText(String str) {
		if(left!=null){
			left.setText(str);
			left.setVisibility(View.VISIBLE);
		}
	}

	/**头部右侧按钮文字
	 * @param str
	 */
	protected void setRightText(String str) {
		if(right!=null){
			right.setText(str);
			right.setVisibility(View.VISIBLE);
		}
	}
	
	protected View getRightBtn() {
		return right;
	}
	
	
	/**左侧按钮点击事件，已有默认事件：关闭当前Activity
	 * @param o
	 */
	protected void setLeftOnClick(OnClickListener o) {
		left.setOnClickListener(o);
	}

	/**右侧按钮点击事件
	 * @param o
	 */
	protected void setRightOnClick(OnClickListener o) {
		right.setOnClickListener(o);
	}
	
	/**左边按钮是否可见
	 * @param v true可见 false隐藏
	 */
	protected void setLeftVisible(boolean v) {
		if (v)
			left.setVisibility(View.VISIBLE);
		else
			left.setVisibility(View.INVISIBLE);
	}

	/**右边按钮是否可见
	 * @param v true可见 false隐藏
	 */
	protected void setRightVisible(boolean v) {
		if (v)
			right.setVisibility(View.VISIBLE);
		else
			right.setVisibility(View.INVISIBLE);
	}
	//-------------------头部view代码
	
	
	public void showToastMsg(String msg){
		Toast.makeText(this, msg, 0).show();
	}
	
	/**
	 *载入加载进度条 
	 */
	private void addLoadingProgress(){
    	progressLL = (LinearLayout)findViewById(R.id.loadingLayout);
    	if(progressLL!=null){
	    	progressLL.removeAllViews();
	    	LinearLayout layout = new LoadingView(this).addLoadingLinearLayout();
	    	progressImgview = (ImageView)layout.findViewById(R.id.progressimg);
	    	errmsgTextView = (TextView)layout.findViewById(R.id.noMsg_textView);
	    	if(progressLL!=null){
	    		progressLL.addView(layout);
	    		loadingAnimation = (AnimationDrawable)layout.findViewById(R.id.progressimg).getBackground();
	    	}
    	}
    }
	
	/**显示进度  图片+文字
	 * @param txt
	 */
	public void showProgress(String txt) {
		addLoadingProgress();
		progressLL.setVisibility(View.VISIBLE);
		progressImgview.setVisibility(View.VISIBLE);
		errmsgTextView.setVisibility(View.VISIBLE);
		errmsgTextView.setText(txt);
	}
	
	/**
	 *隐藏进度 
	 */
	public void hideProgress(){
		progressLL.setVisibility(View.INVISIBLE);
	}
}
