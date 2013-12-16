package or.tango.android.activity;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import or.tango.android.Config;
import or.tango.android.R;
import or.tango.android.pojo.Citiao;
import or.tango.android.pojo.Suggestion;
import or.tango.android.util.MyPreferences;
import or.tango.android.util.PictureUpload;
import or.tango.android.view.ProgressView;

import org.apache.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class PicActivity extends AbstractBaseActivity implements OnClickListener {
    /** Called when the activity is first created. */
	
	public static PicActivity pic_act ;
	public static final int UPLOAD_FLAG=1;
	public static final int UPLOAD_RESPONSE_FLAG=2;
	private static final String TAG="PicActivity";
	private ImageView picImageView;
	private ExecutorService singleThread;
	private File uploadFile /*= new File(Environment.getExternalStorageDirectory() + "/zk/", "idcard.jpg")*/;
	private ProgressView progressView;
	private SlidingDrawer sd;
	private ListView resultLv;
	private MyListAdapter adapter;
	LinkedList<Citiao> users;
	private View noMsgView;
	private ProgressDialog uploadingTipDialog = null;
	
	
	 @SuppressLint("HandlerLeak")
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
						//picImageView.setImageBitmap(BitmapFactory.decodeFile(uploadFile.getAbsolutePath()));
					showToastMsg("上传成功");
				}
			}
			
			if(msg.what==UPLOAD_RESPONSE_FLAG){//响应数据
				Log.i("tango-android:","响应数据");
				if(msg.arg1==HttpStatus.SC_OK){
					
					if(msg.obj!=null && !msg.obj.equals("null\r\n")){
						showToastMsg("图片解析成功");
						sd.setVisibility(View.VISIBLE);
						if(!sd.isOpened())
							sd.toggle();
						
						Log.i("图片解析的数据:",(String)msg.obj);
						Type listType = new TypeToken<LinkedList<Citiao>>(){}.getType();
						Gson gson = new Gson();
						users = gson.fromJson((String)msg.obj, listType);
						if(users.size() == 0){
							noMsgView.setVisibility(View.VISIBLE);
							//users.clear();//清空数据
						}else{
							
							//for(Citiao keyword:users){
								//users.add(keyword);
							//}
						
						
							for (Iterator<Citiao> iterator = users.iterator(); iterator.hasNext();) {
								Citiao user = (Citiao) iterator.next();
								System.out.println(user.getText());
								System.out.println(user.getType());
								System.out.println(user.getZhixinDu());
								for (Iterator<Suggestion> iter = user.getOtherSuggestions().iterator(); iter.hasNext();) {
									Suggestion sg = (Suggestion) iter.next();
									System.out.println(sg.getText());
									System.out.println(sg.getZhixinDu());
								}
							}
							noMsgView.setVisibility(View.INVISIBLE);
							resultLv.refreshDrawableState();
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
		pic_act = this;
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
		
		users = new LinkedList<Citiao>();
		adapter = new MyListAdapter(
                getApplicationContext(),
                R.layout.pic_adapter_item,
                users
                );
        
		resultLv.setAdapter(adapter);
		
		//resultLv.setOnItemClickListener(new ItemClickListener());
		
		Log.i(TAG,"FilePath:"+uploadFile.getAbsolutePath());
		
		
		//初始化图片ip 和 搜索ip		
		Config.HOST=MyPreferences.getHost(getApplicationContext());
		
	}
	
	/*public class ItemClickListener implements AdapterView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View v,
				int position, long id) {
			Citiao keyWord = adapter.getItem(position);
			showToastMsg(keyWord.getText());
			Log.v("click", String.valueOf(v.getContentDescription()));
			
			if(v.getId() == R.id.citiao_button_1 || v.getId() == R.id.item_text){
				Log.v("click", "button1");
			}
			if(v.getId() == R.id.citiao_button_2){
				Log.v("click", "button2");
			}
			if(v.getId() == R.id.citiao_button_3){
				Log.v("click", "button3");
			}
			
			
			Intent intent = new Intent(PicActivity.this, SearchActivity.class);
			intent.putExtra(Config.ACTIVITY_KEY_WORD, keyWord.getText());
			startActivity(intent);
		}
	}*/
	
	public final class ViewHolder{  
        public TextView tv;  
        public Button bt1;  
        public Button bt2;   
        public Button bt3; 
    }  

	
	public class MyListAdapter extends BaseAdapter{   
	    private int resourceId;  
	    private LayoutInflater inflater;
	    public LinkedList<Citiao> baseusers;
	    public MyListAdapter(Context context, int textViewResourceId, LinkedList<Citiao> m_users) {   
	        this.resourceId = textViewResourceId;   
	        this.inflater = LayoutInflater.from(context);
	        this.baseusers = m_users;
	    }   
	    
	       
	    @Override   
	    public View getView(final int position, View convertView, ViewGroup parent){   
	    	Citiao user = users.get(position);
	    	Log.i("pos", position+"");
	    	ViewHolder holder = null;  
            if (convertView == null) {  
                holder=new ViewHolder();   
                LinearLayout userListItem = new LinearLayout(getBaseContext());      
    	        convertView = this.inflater.inflate(resourceId, userListItem, true);  
    	       
                holder.tv = (TextView) convertView.findViewById(R.id.item_text);
		        holder.tv.setText(user.getText());
		        holder.tv.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
						Intent intent = new Intent(PicActivity.this, SearchActivity.class);
						intent.putExtra(Config.ACTIVITY_KEY_WORD, users.get(position).getText());
						startActivity(intent);
					}
		        	
		        });
		        holder.bt1 = (Button)convertView.findViewById(R.id.citiao_button_1);
		        holder.bt1.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(PicActivity.this, SearchActivity.class);
						//Log.v("click","position:"+position+users.get(position).getText());
						intent.putExtra(Config.ACTIVITY_KEY_WORD, users.get(position).getText());
						startActivity(intent);
					}
		        	
		        });
		        holder.bt2 = (Button)convertView.findViewById(R.id.citiao_button_2);
		        holder.bt2.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						final Object[] obj = users.get(position).getOtherSuggestions().toArray();
						String[] show;
						if (obj.length == 0) {
							show = new String[1];
							show[0] = "很抱歉我们暂时没有可推荐词汇，希望您点击【我要推荐】帮我们补充";
						}
						else {
							show= new String[obj.length];
							DecimalFormat df=new DecimalFormat("#.##");
							for (int i=0; i<obj.length; i++){
								show[i] = ((Suggestion)obj[i]).getText() +" 可信指数:"+ df.format(((Suggestion)obj[i]).getZhixinDu());
							}
						}
						
						new AlertDialog.Builder(PicActivity.this)  
						.setTitle("推荐词汇：")  
						.setIcon(android.R.drawable.ic_dialog_info)                  
						.setSingleChoiceItems(show, 0,   
						  new DialogInterface.OnClickListener() {  
						                              
						     public void onClick(DialogInterface dialog, int which) {  
						        dialog.dismiss(); 
						        Intent intent = new Intent(PicActivity.this, SearchActivity.class);
								intent.putExtra(Config.ACTIVITY_KEY_WORD, ((Suggestion)obj[which]).getText());
								startActivity(intent);
						     }  
						  }  
						)  
						.setNegativeButton("取消", null)  
						.show();  

					}
		        	
		        });
		        holder.bt3 = (Button)convertView.findViewById(R.id.citiao_button_3);
		        holder.bt3.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						final EditText et = new EditText(PicActivity.this);
						new AlertDialog.Builder(PicActivity.this)  
						.setTitle("请输入未识别出的配料")  
						.setCancelable(false)
						.setIcon(android.R.drawable.ic_dialog_info)  
						.setView(et) 
						.setPositiveButton("发送", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface d, int v) {
								// TODO Auto-generated method stub
								boolean sendor = true;
								String message =null;
								Log.v("sendor", et.getText().toString().trim());
								if (et.getText() == null || et.getText().toString().trim().equals("")){
									sendor = false;
									message = "请输入内容再点击发送，谢谢！";
								}
								else message = "有了您的热情参与，我们将为大家提供更好的服务";
								
								new AlertDialog.Builder(PicActivity.this)  
					                .setMessage(message)
					                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											Log.v("sendor", "服务器端代码还未添加，敬请期待");
											dialog.dismiss();
										}
									})			                
					                .show();
								d.dismiss();
							}
							
						})  
						.setNegativeButton("取消", null)  
						.show();  

					}
		        	
		        });
                convertView.setTag(holder);  
                return userListItem;
                   
            }else {                       
                holder = (ViewHolder)convertView.getTag();  
                return new LinearLayout(getBaseContext());
            }  
	    }

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			Log.i("pos-Count", users.size()+"");
			return users.size();
		}

		@Override
		public Citiao getItem(int arg0) {
			// TODO Auto-generated method stub
			return users.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
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