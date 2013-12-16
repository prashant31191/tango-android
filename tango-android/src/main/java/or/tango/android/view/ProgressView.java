package or.tango.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**显示进度
 *
 */
public class ProgressView extends View {
	private String percent;
	private Paint paint;

	public ProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public ProgressView(Context context) {
		super(context);
		init();
	}
	private void init() {
		paint=new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(68);
		paint.setFakeBoldText(true);
		paint.setColor(0x99CCCCCC);
	}
	public void setText(String percent) {
		this.percent=percent;
		invalidate();
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(percent!=null){
			int textWidth=(int)paint.measureText(percent);
			canvas.drawText(percent, (getWidth()-textWidth)/2, getHeight()/2, paint);
		}
	}
}
