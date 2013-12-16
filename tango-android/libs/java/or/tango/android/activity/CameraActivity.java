package or.tango.android.activity;

import java.util.List;

import or.tango.android.R;
import or.tango.android.util.PictureUpload;
import or.tango.android.view.VerticalView;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.RectF;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
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
public class CameraActivity extends AbstractBaseActivity implements
		View.OnClickListener {
	private static String TAG = CameraActivity.class.getSimpleName();
	public static String PHOTO_PATH_KEY = "pic_path";//
	private SurfaceView surfaceView;
	private SurfaceView maskView;
	private SurfaceHolder surfaceHolder;
	private SurfaceHolder maskHolder;
	private Camera camera;
	private Button btnSave;
	private ZoomButton btnadd;
	private ZoomButton btnminus;
	private boolean previewRunning;
	private boolean isAutoFocusing;// 是否正在聚焦
	private Toast toast;
	
	private boolean isHaveBox;
	private int BoxWidth, BoxHeight;

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
		btnSave.setOnClickListener(this);

		surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
		surfaceView.setOnClickListener(this);
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
		
		//end////////////////////////////////////
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(surfaceCallback);
		
		if(isHaveBox) {
			maskView = (SurfaceView) findViewById(R.id.mask_preview);
			maskView.setOnClickListener(this);
			LayoutParams mlp = maskView.getLayoutParams();
			mlp.height = lp.height;//屏幕高度;
			mlp.width = lp.width;
			maskView.setLayoutParams(mlp);
			BoxWidth = lp.width/3;
			BoxHeight = lp.height/5;
			maskHolder = maskView.getHolder();
			maskHolder.setFormat(PixelFormat.TRANSPARENT);
			maskHolder.addCallback(maskCallback);
		}
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.save_pic) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				if (!isAutoFocusing) {
					isAutoFocusing = true;
					camera.autoFocus(new MyAutoFocusCallback(true));// 自动聚焦，完成后拍照
				}
			} else
				showToastMsg("sd卡不可用");
		}

		if (v.getId() == R.id.camera_preview) {
			if (camera != null) {
				if (!isAutoFocusing) {
					isAutoFocusing = true;
					camera.autoFocus(new MyAutoFocusCallback(false));// 自动聚焦
				}
			}
		}
		
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
			new SavePictureTask().execute(data);
		}
	};

	private String photoPath;// 照片路径

	class SavePictureTask extends AsyncTask<byte[], String, Boolean> {
		@Override
		protected Boolean doInBackground(byte[]... params) {
			boolean result = true;
			photoPath = PictureUpload.savePic(params[0]);
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
		Intent intent = new Intent(CameraActivity.this, PicActivity.class);
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
				
				parameters.set("rotation", 90);
				List<Size> sizes = parameters.getSupportedPreviewSizes();
				Size optimalSize = getOptimalPreviewSize(sizes, width, height);// 匹配相机支持的屏幕尺寸
				if (optimalSize != null) {
					Log.i(TAG, "====camera use w=" + optimalSize.width + ";h="
							+ optimalSize.height);
					parameters.setPictureSize(optimalSize.width,
							optimalSize.height);
					parameters.setPreviewSize(optimalSize.width,
							optimalSize.height);
				}
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				camera.setParameters(parameters);
				camera.startPreview();
				
				// 缩放 变焦
				Zoom(5);
				
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
	
	SurfaceHolder.Callback maskCallback = new SurfaceHolder.Callback() {
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			Log.i(TAG, "====surfaceDestroyed");
			drawLine(BoxWidth, BoxHeight);		
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			drawLine(BoxWidth, BoxHeight);
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			clearDraw();
			drawLine(BoxWidth, BoxHeight);			
		}
		
		void clearDraw()  
	    {  
	        Canvas canvas = maskHolder.lockCanvas();  
	        canvas.drawColor(PixelFormat.TRANSPARENT);  
	        maskHolder.unlockCanvasAndPost(canvas);  
	    }  
	    public void drawLine(int width, int height)  
	    {  
	    	try{
		        Canvas canvas = maskHolder.lockCanvas();  
		        canvas.drawColor(Color.TRANSPARENT);  
		        int x = canvas.getWidth()/2 - width/2;
		        int y = canvas.getHeight()/2 - height/2;
		        Paint p = new Paint();  
		        p.setAntiAlias(true);  
		        p.setColor(Color.RED);  
		        p.setStyle(Style.STROKE);  
		        //canvas.drawPoint(100.0f, 100.0f, p);   
		        //canvas.drawLine(0,110, 500, 110, p);  
		        //canvas.drawCircle(110, 110, 10.0f, p); 
		        
		        Log.v("draw", String.valueOf(x+" "+y+" "+width+" "+height));
		        canvas.drawRect(new RectF(x,y,x+width,y+height), p);
		        maskHolder.unlockCanvasAndPost(canvas);  
	    	} catch(Exception e){
	    		
	    	}
	          
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
}
