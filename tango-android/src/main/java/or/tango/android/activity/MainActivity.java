package or.tango.android.activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import or.tango.android.R;
import or.tango.android.util.PictureUpload;

public class MainActivity extends AbstractBaseActivity implements View.OnClickListener{
	
	//主Activity入口
	
	private static final int REQUEST_CODE=100;

	@Override
	protected void setLayout() {
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void findViews() {
		setTitle("配料拍摄");
		findViewById(R.id.btn_photo).setOnClickListener(this);
		findViewById(R.id.btn_photo_system).setOnClickListener(this);
		findViewById(R.id.btn_setting).setOnClickListener(this);
		findViewById(R.id.btn_readpic).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// 进入设置界面
		if(v.getId() == R.id.btn_setting){
			//Log.v("set","1");
			startActivity(new Intent(this, SettingActivity.class));
		}
		// 进入从本地上传的界面
		if(v.getId() == R.id.btn_readpic){
			//Log.v("readpic","1");
			startActivity(new Intent(this, ReadFileActivity.class));
		}
		// 进入拍照界面
		if(v.getId()==R.id.btn_photo){
			Intent intent = new Intent(this, CameraActivity.class);
			intent.putExtra("HaveBox", false);
			startActivity(intent);
		}
		// 进入系统摄像机界面
		if(v.getId()==R.id.btn_photo_system){
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/tango/insightech/bitmap.jpg"))); 
            startActivityForResult(intent, REQUEST_CODE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE&&resultCode==RESULT_OK){
			if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				showToastMsg("sd卡不可用");
				return;
			}
			
			Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            //bitmap = ResizeBitmap(bitmap, 640);
            String photoPath = PictureUpload.savePic(bitmap, false);
            
            if(bitmap!=null){
            	bitmap.recycle();
            	bitmap=null;
            }
			
            Intent intent = new Intent(this,PicActivity.class);
        	intent.putExtra(CameraActivity.PHOTO_PATH_KEY, photoPath);
        	
        	startActivity(intent);
        	
		}
	}


}
