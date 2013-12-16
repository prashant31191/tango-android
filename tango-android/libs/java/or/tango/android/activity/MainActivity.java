package or.tango.android.activity;
import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import or.tango.android.R;
import or.tango.android.util.PictureUpload;

public class MainActivity extends AbstractBaseActivity implements View.OnClickListener{
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
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_setting){
			startActivity(new Intent(this, SettingActivity.class));
		}
		if(v.getId()==R.id.btn_photo){
			Intent intent = new Intent(this, CameraActivity.class);
			intent.putExtra("HaveBox", true);
			startActivity(intent);
		}
		
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
            String photoPath = PictureUpload.savePic(bitmap);
            
            if(bitmap!=null){
            	bitmap.recycle();
            	bitmap=null;
            }
			
            Intent intent = new Intent(this,PicActivity.class);
        	intent.putExtra(CameraActivity.PHOTO_PATH_KEY, photoPath);
        	
        	startActivity(intent);
        	
		}
	}
	
	public static Bitmap ResizeBitmap(Bitmap bitmap, int newWidth) { 
	     int width = bitmap.getWidth(); 
	     int height = bitmap.getHeight(); 
	     float temp = ((float) height) / ((float) width); 
	     int newHeight = (int) ((newWidth) * temp); 
	     float scaleWidth = ((float) newWidth) / width; 
	     float scaleHeight = ((float) newHeight) / height; 
	     Matrix matrix = new Matrix(); 
	     // resize the bit map 
	     matrix.postScale(scaleWidth, scaleHeight); 
	     // matrix.postRotate(45); 
	     Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true); 
	     bitmap.recycle(); 
	     return resizedBitmap; 
	} 

}
