package or.tango.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
	
	SurfaceHolder maskHolder;
	public static final String TAG = "drawview";
	boolean cleared;
	
	public DrawView(Context context, AttributeSet atts){
		super(context, atts);
		maskHolder = this.getHolder();
		maskHolder.addCallback(this);
	}
	
	public Canvas getCanvas() {
		// TODO Auto-generated method stub
		
		return maskHolder.lockCanvas();
	}
	public void releaseCanvas(Canvas canvas) {
		// TODO Auto-generated method stub
		
		maskHolder.unlockCanvasAndPost(canvas);
	}
	
	

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i("drawview", "====surfaceDestroyed");
		
		//drawLine(BoxWidth, BoxHeight);
		//clearDraw();
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//drawLine(BoxWidth, BoxHeight);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		//clearDraw();
		
		holder.setFormat(PixelFormat.TRANSPARENT);
		//drawLine(BoxWidth, BoxHeight);			
	}
	
	
	public void clearDraw()  
    {  
		try {
			
	        Canvas canvas = maskHolder.lockCanvas();  
	        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);  
	        maskHolder.unlockCanvasAndPost(canvas);  
	        Log.v(TAG,"clearDraw");
	        cleared = true;
		}catch (Exception e){
			e.printStackTrace();
		}
    }  
	
	class DrawPictureTask extends AsyncTask<Integer, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			int width = params[0];
			int height = params[1];
			try{
		        Canvas canvas = maskHolder.lockCanvas();  
		        canvas.drawColor(Color.TRANSPARENT);  
		        int x = canvas.getWidth()/2 - width/2;
		        int y = canvas.getHeight()/2 - height/2;
		        Paint p = new Paint();  
		        p.setAntiAlias(true);  
		        p.setColor(Color.TRANSPARENT);  
		        p.setStyle(Style.FILL);  
		        //canvas.drawPoint(100.0f, 100.0f, p);   
		        //canvas.drawLine(0,110, 500, 110, p);  
		        //canvas.drawCircle(110, 110, 10.0f, p); 
		        
		        Log.v("draw", String.valueOf(x+" "+y+" "+width+" "+height));
		        canvas.drawRect(new RectF(x,y,x+width,y+height), p);
		        p.setColor(Color.YELLOW);
		        p.setStyle(Style.STROKE);
		        p.setStrokeWidth(5);
		        canvas.drawRect(new RectF(x,y,x+width,y+height), p);
		        cleared = false;
		        
		        maskHolder.unlockCanvasAndPost(canvas);  
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	} 
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.v(TAG, "finish drawing");
		}

	}
	
    public void drawLine(int width, int height)  
    {  
    	if (!cleared)clearDraw();
    	DrawPictureTask drawTask = new DrawPictureTask();
    	drawTask.execute(width, height);
    	/*try{
	        Canvas canvas = maskHolder.lockCanvas();  
	        canvas.drawColor(Color.TRANSPARENT);  
	        int x = canvas.getWidth()/2 - width/2;
	        int y = canvas.getHeight()/2 - height/2;
	        Paint p = new Paint();  
	        p.setAntiAlias(true);  
	        p.setColor(Color.TRANSPARENT);  
	        p.setStyle(Style.FILL);  
	        //canvas.drawPoint(100.0f, 100.0f, p);   
	        //canvas.drawLine(0,110, 500, 110, p);  
	        //canvas.drawCircle(110, 110, 10.0f, p); 
	        
	        Log.v("draw", String.valueOf(x+" "+y+" "+width+" "+height));
	        canvas.drawRect(new RectF(x,y,x+width,y+height), p);
	        p.setColor(Color.RED);
	        p.setStyle(Style.STROKE);
	        canvas.drawRect(new RectF(x,y,x+width,y+height), p);
	        
	        maskHolder.unlockCanvasAndPost(canvas);  
    	} catch(Exception e){
    		e.printStackTrace();
    	} */
    }  
	
}

