package or.tango.android.activity;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import or.tango.android.Config;
import or.tango.android.R;
import or.tango.android.util.MyPreferences;
import or.tango.android.util.PictureUpload;
import or.tango.android.view.ProgressView;

import org.apache.http.HttpStatus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;

public class PicActivity extends AbstractBaseActivity implements OnClickListener {
    /** Called when the activity is first created. */
	
	public static final int UPLOAD_FLAG=1;
	public static final int UPLOAD_RESPONSE_FLAG=2;
	private static final String TAG="PicActivity";
	private ImageView picImageView;
	private ExecutorService singleThread;
	private File uploadFile /*= new File(Environment.getExternalStorageDirectory() + "/zk/", "idcard.jpg")*/;
	private ProgressView progressView;
	private SlidingDrawer sd;
	private ListView resultLv;
	private ArrayAdapter<String> adapter;
	private View noMsgView;
	private ProgressDialog uploadingTipDialog = null;
	
	
	 private Handler uploadProgressHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==UPLOAD_FLAG){//显示进度
				Log.i("tango-android:","显示进度");
				int dataSize = msg.arg1;
				int uploadSize = msg.arg2;
				int percent =(int) ((uploadSize*1.0f/dataSize)*100);
				if(progressView!=null)
					progressView.setText("");
				if(percent==100){
						getRightBtn().setEnabled(true);
						uploadingTipDialog.hide();
						picImageView.setImageBitmap(BitmapFactory.decodeFile(uploadFile.getParent()+"/rec.jpg"));
					showToastMsg("上传成功");
				}
			}
			
			if(msg.what==UPLOAD_RESPONSE_FLAG){//响应数据
				Log.i("tango-android:","响应数据");
				if(msg.arg1==HttpStatus.SC_OK){
					
					if(msg.obj!=null){
						showToastMsg("图片解析成功");
						sd.setVisibility(View.VISIBLE);
						if(!sd.isOpened())
							sd.toggle();
						
						Log.i("图片解析的数据:",(String)msg.obj);
						
						String[] keywords = msg.obj.toString().split(",");
						if(keywords.length == 0){
							noMsgView.setVisibility(View.VISIBLE);
							adapter.clear();//清空数据
						}else{
							noMsgView.setVisibility(View.INVISIBLE);
							for(String keyword:keywords){
								adapter.add(keyword);
							}
						}
						
					}else{
						showToastMsg("图片解析失败");
					}
				}
			}
		};
	};
	

	@Override
	protected void setLayout() {
		setContentView(R.layout.activity_pic);
		
		singleThread = Executors.newSingleThreadExecutor();
		
		String picPath = getIntent().getStringExtra(CameraActivity.PHOTO_PATH_KEY);
		uploadFile=new File(picPath);
	}

	@Override
	protected void findViews() {
		setTitle("图片上传");
		setLeftVisible(true);
		setRightVisible(true);
		getRightBtn().setOnClickListener(this);
		picImageView=(ImageView)findViewById(R.id.pic_iv);
		progressView=(ProgressView)findViewById(R.id.progressView);
		picImageView.setImageBitmap(BitmapFactory.decodeFile(uploadFile.getAbsolutePath()));
		
		noMsgView = (View)findViewById(R.id.noMsgLayout);
		
		//handler=(ImageView)findViewById(R.id.handle);
		sd = (SlidingDrawer)findViewById(R.id.drawer);
		sd.setVisibility(View.INVISIBLE);
		resultLv = (ListView)findViewById(R.id.contentListView);
		
		adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                R.layout.pic_adapter_item,
                R.id.item_text
                );
		
		resultLv.setAdapter(adapter);
		
		resultLv.setOnItemClickListener(new ItemClickListener());
		
		Log.i(TAG,"FilePath:"+uploadFile.getAbsolutePath());
		
		
		//初始化图片ip 和 搜索ip		
		Config.HOST=MyPreferences.getHost(getApplicationContext());
		
	}
	
	public class ItemClickListener implements AdapterView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			String keyWord = adapter.getItem(position);
			showToastMsg(keyWord);
			
			Intent intent = new Intent(PicActivity.this, SearchActivity.class);
			intent.putExtra(Config.ACTIVITY_KEY_WORD, keyWord);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.right_btn){
			if(uploadFile.exists()){//开始上传
				getRightBtn().setEnabled(false);
				
				uploadingTipDialog = ProgressDialog.show (PicActivity.this , "" ,"上传中..." , true );
				uploadingTipDialog.show();
				singleThread.execute(new Runnable() {
					@Override
					public void run() {
						try {
							PictureUpload.upload(uploadFile,uploadProgressHandler,singleThread);
						} catch (Exception e) {
							e.printStackTrace();//网络异常
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									uploadingTipDialog.dismiss();
									getRightBtn().setEnabled(true);
									showToastMsg("网络异常，请检查网络或者ip地址");
								}
							});
						}
					}
				});
			}else{
				showToastMsg("文件不存在");
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		if(sd.isOpened()){
			sd.toggle();
		}else{
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		singleThread.shutdown();
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 3, 3, "输入接口地址");
        return super.onCreateOptionsMenu(menu);
    }
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		String msg="";
		if(item.getItemId() == 3){
            msg="输入接口";
            startActivity(new Intent(this, SettingActivity.class));
        }  
        
        showToastMsg(msg);
        return true;
    }
	
}