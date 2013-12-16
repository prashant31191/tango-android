package or.tango.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**在ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE 在横屏中显示一个竖屏的提示
 *
 */
public class VerticalView extends View {
	private Paint mTextPaint;
    private String mText;
    private int textHeight,textWidth,mAscent;

	public VerticalView(Context context,String msg) {
		super(context);
		initVerticalView(msg);
	}

    private final void initVerticalView(String msg) {
    	mText=msg;
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(18);
        mTextPaint.setColor(0xFFFFFFFF);
        setPadding(6, 6, 6, 6);
        setBackgroundColor(0x90000000);//背景透明90
        
        textHeight=(int) (-mTextPaint.ascent() + mTextPaint.descent());
        textWidth =(int) mTextPaint.measureText(msg);
        mAscent = (int) mTextPaint.ascent();
    }
	
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	setMeasuredDimension(measureHeight(heightMeasureSpec),
    			measureWidth(widthMeasureSpec));
    }
    
    /**
     * Determines the width of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
                    + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    /**
     * Determines the height of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
        	//mAscent基线到文字最高点的距离为负数
        	//descent基线到文字最低点的距离为正数
            result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop()
                    + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.rotate(-90);//注意：canvas旋转90度，但坐标系不变
        canvas.drawText(mText, -(textWidth+getPaddingLeft()), (-mAscent+getPaddingTop()), mTextPaint);
        canvas.restore();
    }
}
