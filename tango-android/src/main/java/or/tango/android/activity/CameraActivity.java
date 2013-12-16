package or.tango.android.activity;

import java.io.ByteArrayOutputStream;
import java.util.List;

import or.tango.android.R;
import or.tango.android.util.PictureUpload;
import or.tango.android.view.DrawView;
import or.tango.android.view.VerticalView;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ZoomButton;

/**
 * 拍照
 * 
 * 
 */

public class CameraActivity extends AbstractBaseActivity {
	private static String TAG = CameraActivity.class.getSimpleName();
	public static String PHOTO_PATH_KEY = "pic_path";//
	private SurfaceView surfaceView;
	private DrawView maskView;
	private SurfaceHolder surfaceHolder;
	//private SurfaceHolder maskHolder;
	private Camera camera;
	private Button btnSave;
	private ZoomButton btnadd;
	private ZoomButton btnminus;
	private Button btnmode;
	private boolean previewRunning;
	private boolean isAutoFocusing;// 是否正在聚焦
	private boolean photohasbeentaken;
	private Toast toast;
	
	private boolean isZoom;
	private boolean isHaveBox;
	private int BoxWidth, BoxHeight;
	private int picwidth,picheight;
	private int cameraWidth, camaraHeight;
	private int canvasWidth, canvasHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		isHaveBox = this.getIntent().getBooleanExtra("HaveBox", false);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.activity_camera);
	}

	@Override
	protected void findViews() {
		btnSave = (Button) findViewById(R.id.save_pic);
		btnSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Log.v(TAG, "click save_pic");
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					if (!isAutoFocusing) {
						isAutoFocusing = true;
						camera.autoFocus(new MyAutoFocusCallback(true));// 自动聚焦，完成后拍照
						//==================test============================
						//photoPath = "1111";
						//enterPicActivity();
						//==================test end==========================
						photohasbeentaken = true;
					}
				} else
					showToastMsg("sd卡不可用");
			}
			
		});

		surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
		surfaceView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v.getId()!= R.id.camera_preview) {
					Log.v(TAG, "touch wrong view ");
					return false;
				}
		        // TODO Auto-generated method stub   
		        switch(event.getActionMasked()){  
		        
			        case MotionEvent.ACTION_DOWN:  
			        	Log.v(TAG, "click preview: "+ photohasbeentaken);
						if (camera != null && photohasbeentaken == false) {
							if (!isAutoFocusing) {
								isAutoFocusing = true;
								camera.autoFocus(new MyAutoFocusCallback(false));// 自动聚焦
							}
						} 
			            break;  
			        default:  
			            break;  
		          
		        }  
				return false;
			}
			
		});
		
		surfaceHolder = surfaceView.getHolder();
		//test//////////////////////////////////
		//surfaceHolder.setFixedSize(600, 600);
		//test2
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		LayoutParams lp = surfaceView.getLayoutParams();
		lp.height = dm.heightPixels;//屏幕高度;
		lp.width = (int)(lp.height*4/3);
		surfaceView.setLayoutParams(lp);               
		
		// 重新设置button大小
		btnadd = (ZoomButton) findViewById(R.id.zoombutton_add);
		LayoutParams btn_lp = btnadd.getLayoutParams();
		btn_lp.width = (dm.widthPixels - lp.width)/2;
		btn_lp.height = btn_lp.width;
		btnadd.setLayoutParams(btn_lp);
		btnadd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Log.v("camera-zoom", "add");
				if (!Zoom(2)){
					showToastMsg("不能再放大了");
				}
			}
		});
		btnminus = (ZoomButton) findViewById(R.id.zoombutton_minus);
		btnminus.setLayoutParams(btn_lp);
		btnminus.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Log.v("camera-zoom", "minus");
				if (!Zoom(-2)){
					showToastMsg("不能再缩小了");
				}
			}
		});
	
		btnmode = (Button) findViewById(R.id.button_mode);
		btn_lp = btnmode.getLayoutParams();
		btn_lp.width = (dm.widthPixels - lp.width)/2;
		btn_lp.height = btn_lp.width;
		btnmode.setLayoutParams(btn_lp);
		btnmode.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if (isHaveBox){
					btnmode.setBackgroundResource(R.drawable.user_define_mode_button_selector);
					isHaveBox = false;
					maskView.clearDraw();
				}
				else {
					btnmode.setBackgroundResource(R.drawable.user_define_mode2_button_selector);
					isHaveBox = true;
					maskView.clearDraw();
					Log.v("draw", BoxWidth + "+" + BoxHeight);
					maskView.drawLine(BoxWidth, BoxHeight);
				}
			}
		});
		
		//end////////////////////////////////////
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(surfaceCallback);
		
		
		maskView = (DrawView) findViewById(R.id.mask_preview);
		
		canvasWidth = lp.width;
		canvasHeight = lp.height;
		
		
		//maskView.setZOrderOnTop(true);
		maskView.setOnTouchListener(new OnTouchListener(){
			int old_X0=0,old_Y0=0,old_X1=0,old_Y1=0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v.getId()!= R.id.mask_preview) {
					Log.v(TAG, "touch wrong view ");
					return false;
				}
				
		        // TODO Auto-generated method stub   
		        switch(event.getActionMasked()){  
		        
			        case MotionEvent.ACTION_DOWN:  
			            Log.v(TAG+"-mask", "onTouchEvent__________ action down");  
			            break;  
			        case MotionEvent.ACTION_POINTER_UP:  
			            isZoom = false;  
			            Log.v(TAG+"-mask", "onTouchEvent__________ action pointer up");  
			            break;  
			        
			        case MotionEvent.ACTION_POINTER_DOWN:  
			            isZoom = true;  
			            old_X0 = (int)event.getX(0);
			            old_X1 = (int)event.getX(1);
			            old_Y0 = (int)event.getY(0);
			            old_Y1 = (int)event.getY(1);
			            Log.v(TAG+"-mask", "onTouchEvent__________ action pointer down"); 
			            break;  
			        case MotionEvent.ACTION_MOVE:  
			            if(isZoom){  			  
			            	if (old_X1 != old_X0 && old_Y1 != old_Y0){
				            	int tempWidth = (int)(BoxWidth*(
				            			((event.getX(1)- event.getX(0))/
				            			(old_X1 - old_X0) -1 )/12 +1
				            			));
				            	int tempHeight = (int)(BoxHeight*(
				            			((event.getY(1)-event.getY(0))/
				            			(old_Y1 - old_Y0) -1 )/12 +1
				            			));
				            	if (tempWidth>10 && tempWidth<canvasWidth-10 &&
				            			tempHeight>10 && tempHeight<canvasHeight-10){	
				            		maskView.clearDraw();
				            		maskView.drawLine(tempWidth, tempHeight);
				            		BoxWidth = tempWidth;
				            		BoxHeight = tempHeight;
				            		
				            	}
				            	Log.v("Canvas size", canvasWidth + " "+ canvasHeight);
				            	Log.v("Camera size", cameraWidth + " "+ camaraHeight);
				            	Log.v("Picture size", picwidth + " "+ picheight);
				            	Log.v(TAG+"-mask", old_X1 + " " +old_Y1 + " " + old_X0 
				            			+ " " + old_Y0 );
				            	Log.v(TAG+"-mask", (int)event.getX(1) + " " 
				            			+(int)event.getY(1) + " " +(int)event.getX(0) 
				            			+ " " + (int)event.getY(0) );
				            		
				                Log.v(TAG+"-mask", "onTouchEvent__________ action move" +
				                		" : space = "+BoxWidth+ " "+BoxHeight);
			            	}			                
			            }  
			            break;    
			        default:  
			            break;  
		          
		        }  
				return true;
			}
			
		});
		LayoutParams mlp = maskView.getLayoutParams();
		mlp.height = lp.height;//屏幕高度;g
		mlp.width = lp.width;
		maskView.setLayoutParams(mlp);
		BoxWidth = lp.width/3;
		BoxHeight = lp.height/5;
		//maskHolder = maskView.getHolder();
		//maskHolder.setFormat(PixelFormat.TRANSPARENT);
		//maskHolder.addCallback(maskCallback);
		
	}
	
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.v("restart", "!!");
	}
	

	private boolean Zoom(int value){
		if (camera.getParameters().isZoomSupported())
        {
			try
            {
                Parameters params = camera.getParameters();
                final int MAX = params.getMaxZoom();
                if(MAX==0)return false;

                int zoomValue = params.getZoom();
                Log.i("camera-zoom", "-----------------MAX:"+MAX+"   params : "+zoomValue);
                zoomValue += value;
                if(zoomValue > MAX || zoomValue < 0) return false;
                params.setZoom(zoomValue);
                camera.setParameters(params);
                Log.i("camera-zoom", "Is support Zoom " + params.isZoomSupported());
            }
            catch (Exception e)
            {
                Log.i("camera-zoom","--------exception zoom");
                e.printStackTrace();
            }
			return true;
        }
		else {
			Log.i("camera-zoom", "-------------do not support");
		}
		return false;
	}

	class MyAutoFocusCallback implements AutoFocusCallback {
		private boolean isSave;// 是否保存拍照

		/**
		 * 聚焦后是否拍照
		 * 
		 * @param isCapture
		 */
		public MyAutoFocusCallback(boolean isCapture) {
			this.isSave = isCapture;
		}

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			Log.v(TAG, "AutoFocusCallback" + success);
			isAutoFocusing = false;// 聚焦完成
			if (success) {
				if (isSave) {// 拍照
					Camera.Parameters Parameters = camera.getParameters();
					Parameters.setPictureFormat(ImageFormat.JPEG);// 设置图片格式
					camera.setParameters(Parameters);
					camera.takePicture(null, null, pictureCallback);// 调用拍照
				} else {
					showToastMsg("聚焦成功");
				}
			} else {
				showToastMsg("聚焦失败");
			}
		}
	}

	// Photo call back
	Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
		// @Override
		public void onPictureTaken(byte[] data, Camera camera) {
			previewRunning = false;// 拍照后 已经stopPreview
			if (isHaveBox) {
				Log.v("crop", data.length+"");
				Bitmap prebm = null;
				if(data.length!=0){   
	                prebm = BitmapFactory.decodeByteArray(data, 0, data.length);  
	                //bm = ResizeBitmap(prebm, picwidth);
				}  
				else return;
				Log.v("Canvassize", canvasWidth+"+"+canvasHeight);
				Log.v("Picsize", picwidth+"+"+picheight);
				float cropWidth = BoxWidth*((float)picwidth/canvasWidth);
				float cropHeight = BoxHeight*((float)picheight/canvasHeight);
		        int x = (int)(picwidth/2 - cropWidth/2);
		        int y = (int)(picheight/2 - cropHeight/2);
//		        Log.v("BitmapSize", bm.getWidth() + " " + bm.getHeight());
				Bitmap result = Bitmap.createBitmap(prebm, x, y, (int)cropWidth, (int)cropHeight);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			    result.compress(Bitmap.CompressFormat.PNG, 100, baos);  
			    data = baos.toByteArray(); 
			    Log.v("Boxsize", BoxWidth +" "+BoxHeight);
			    Log.v("crop", data.length+" "+ x +" "+ y + " " + cropWidth+ " " + cropHeight);
			    
//			    if(bm != null && !bm.isRecycled()){  
//			        // 回收并且置为null
//			        bm.recycle();  
//			        bm = null;  
//			    }  
			    if(prebm != null && !prebm.isRecycled()){  
			        // 回收并且置为null
			        prebm.recycle();  
			        prebm = null;  
			    }  
			    if(result != null && !result.isRecycled()){  
			        // 回收并且置为null
			        result.recycle();  
			        result = null;  
			    }  
			}
			
			
			new SavePictureTask().execute(data);
		}
	};

	private String photoPath;// 照片路径

	class SavePictureTask extends AsyncTask<byte[], String, Boolean> {
		@Override
		protected Boolean doInBackground(byte[]... params) {
			boolean result = true;
			photoPath = PictureUpload.savePic(params[0], isHaveBox);
			//photoPath = "111";
			Log.v(TAG, "save pic path: "+ photoPath);
			if (TextUtils.isEmpty(photoPath))
				result = false;
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				enterPicActivity();
			} else {
				showToastMsg("文件保存失败");
				camera.startPreview();
				previewRunning = true;
			}
		}

	}

	private void enterPicActivity() {
		Intent intent = new Intent(this, PicActivity.class);
		intent.putExtra(PHOTO_PATH_KEY, photoPath);
		startActivity(intent);
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

		/*
		 * create在activity创建后调用一次
		 */
		public void surfaceCreated(SurfaceHolder holder) {
			Log.i(TAG, "surfaceCallback====");
			// 实例化相机
			try {
				camera = Camera.open(); // Turn on the camera
				camera.setPreviewDisplay(surfaceHolder); // Set Preview
			} catch (Exception e) {
				camera.release();// release camera
				camera = null;
				showToastMsg("照相机异常!");
			}
		}

		/*
		 * onResumu后调用
		 */
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.i(TAG, "====surfaceChanged w=" + width + ";h=" + height);
			if (camera != null) {
				if (previewRunning) {
					camera.stopPreview();
				}
				Camera.Parameters parameters = camera.getParameters();
				
				//parameters.set("rotation", 90);
				parameters.set("orientation", "horizontal");
				List<Size> sizes = parameters.getSupportedPreviewSizes();
				List<Size> picsizes = parameters.getSupportedPictureSizes();
				Size optimalcamarasize = getOptimalPreviewSize(sizes, width, height);// 匹配相机支持的屏幕尺寸
				Size optimalpicsize = getOptimalPreviewSize(picsizes, width, height);// 匹配相机支持的屏幕尺寸
				if (optimalcamarasize != null) {
					Log.i(TAG, "====camera use w=" + optimalcamarasize.width + ";h="
							+ optimalcamarasize.height);
					parameters.setPreviewSize(optimalcamarasize.width,
							optimalcamarasize.height);
				}
				if (optimalpicsize != null) {
					Log.i(TAG, "====picture use w=" + optimalpicsize.width + ";h="
							+ optimalpicsize.height);
					parameters.setPictureSize(optimalpicsize.width,
							optimalpicsize.height);
				}
				cameraWidth = optimalcamarasize.width;
				camaraHeight = optimalcamarasize.height;
				picwidth = optimalpicsize.width;
				picheight = optimalpicsize.height;
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				camera.setParameters(parameters);
				camera.startPreview();
				
				// 缩放 变焦
				if (isHaveBox){
					Zoom(5);
				}
				
				camera.autoFocus(new MyAutoFocusCallback(false));	
				previewRunning = true;
				
				
			}
		}
		

		/*
		 * onPause后调用
		 */
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i(TAG, "====surfaceDestroyed");
			if (camera != null) {
				if (previewRunning)
					camera.stopPreview();// stop preview
				camera.setPreviewCallback(null);
				camera.release(); // Release camera resources
				camera = null;
			}
		}

		private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
			final double ASPECT_TOLERANCE = 0.05;
			double targetRatio = (double) w / h;
			Log.i("camera", "support sizes==null" + (sizes == null));
			if (sizes == null)
				return null;

			Size optimalSize = null;
			double minDiff = Double.MAX_VALUE;

			int targetHeight = h;

			// Try to find an size match aspect ratio and size
			Log.i("camera", "support sizes.length=" + sizes.size());
			for (Size size : sizes) {
				double ratio = (double) size.width / size.height;
				Log.i("camera", "support ratio=" + ratio + ";size.width="
						+ size.width + ";size.height=" + size.height);
				if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
					continue;
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
			// Cannot find the one match the aspect ratio, ignore the
			// requirement
			if (optimalSize == null) {
				minDiff = Double.MAX_VALUE;
				for (Size size : sizes) {
					if (Math.abs(size.height - targetHeight) < minDiff) {
						optimalSize = size;
						minDiff = Math.abs(size.height - targetHeight);
					}
				}

			}
			return optimalSize;
		}
		
	};
	
	
	
	
	

	@Override
	public void showToastMsg(String msg) {
		// 将toast显示为竖屏样式
		toast = new Toast(this);
		toast.setGravity(Gravity.CENTER_VERTICAL, getWindow().getDecorView()
				.getWidth() / 6, 0);// 居中，距离中心1/6屏幕处
		toast.setView(new VerticalView(this, msg));
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();

	};

	@Override
	protected void onPause() {
		super.onPause();
		if (toast != null)
			toast.cancel();
	}

	// 检测摄像头是否存在的私有方法
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// 摄像头存在
			return true;
		} else {
			// 摄像头不存在
			return false;
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
	     return resizedBitmap; 
	} 
}
