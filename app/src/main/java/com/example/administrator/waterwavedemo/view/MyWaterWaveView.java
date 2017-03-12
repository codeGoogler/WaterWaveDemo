package com.example.administrator.waterwavedemo.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.administrator.waterwavedemo.MainActivity;
import com.example.administrator.waterwavedemo.R;
import com.example.administrator.waterwavedemo.utils.DensityUtil;
import com.example.administrator.waterwavedemo.utils.LogUtil;


/**
 * 类功能描述：</br>
 * 自定义水波纹效果
 * @author yuyahao
 * @version 1.0 </p> 修改时间：</br> 修改备注：</br>
 */
public class MyWaterWaveView extends View {
    private Paint paint;//画笔
    private Path path;//当前的path
    private int waveLenght = 400;//波长
    private int waveHeight = 80;//波峰
    private boolean isRise = false;//是否涨水
    private int  dx;//涨水的dx
    private int dy;//涨水的dy
    private Bitmap bitmap;
    private int width ;//当前的宽度
    private int height ;//当前的高度
    private int waveView_boatBitmap;
    private int diration;//次序的时间
    private int originY;//记录车的高度
    private Region region;
    private Context context;
    private ValueAnimator valueAnimator;

    public MyWaterWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyWaterWaveView);
        if(typedArray != null){
            waveLenght = (int) typedArray.getDimension(R.styleable.MyWaterWaveView_waveLegth,500);
            waveHeight = (int) typedArray.getDimension(R.styleable.MyWaterWaveView_waveHeight,200);
            isRise =  typedArray.getBoolean(R.styleable.MyWaterWaveView_rise,false);
            originY = (int) typedArray.getDimension(R.styleable.MyWaterWaveView_originY,500);
            diration =  typedArray.getInteger(R.styleable.MyWaterWaveView_diration,2000);
             waveView_boatBitmap =  typedArray.getResourceId(R.styleable.MyWaterWaveView_waveView_boatBitmap, 0);
        }
        typedArray.recycle();
        this.context = context;
        initVews();
    }

    private void initVews() {
        paint = new Paint();paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(ContextCompat.getColor(context,R.color.colorAccent));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;//缩放图片
        if(waveView_boatBitmap != 0){
          // bitmap = BitmapFactory.decodeResource(context.getResources(),waveView_boatBitmap,options);
            bitmap = BitmapFactory.decodeResource(context.getResources(),waveView_boatBitmap,options);
        }else{
            bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.yang,options);
        }
        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int  widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);
        width = widthSize;
        height  = heightSize;
        if(originY == 0){
            originY = height;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setpathData();
        canvas.drawPath(path,paint);
        //画小车
        Rect rect = region.getBounds();
       // LogUtil.e("yyh",""+rect.left+"----"+rect.top+"-----"+rect.right+"-----"+rect.bottom);
        LogUtil.e("yyh","originY:  "+originY +"-----"+Math.abs(getMeasuredHeight() - dy)+"-----屏幕：  "+
                DensityUtil.getScreenIntHeight(context));
        /**
         * 当波峰下滑到originY的时候,bounds.top是不断增加的
         * 下半部分，发生增加变化的是 bound.bottom
         */
        if(rect.top < originY){
            canvas.drawBitmap(bitmap,
                    rect.right - bitmap.getWidth() / 2,
                    rect.top - bitmap.getHeight() / 2-100,
                    paint
            );
        }else{
            canvas.drawBitmap(bitmap,
                    rect.right - bitmap.getWidth() / 2,
                    rect.bottom - bitmap.getHeight() / 2-100,
                    paint
            );
        }
    }

    private void setpathData() {
        path.reset();//该方法会不断的去掉用，所有要reset一下
        int halfWaveLength = waveLenght / 2;
        //先把比落在那个位置
        originY = originY - dy;
        path.moveTo(-waveLenght+dx,originY);//半个波长，波纹所在的水平线
        //不断的去画一个波长
        for (int i = -waveLenght; i < width + waveLenght; i+=waveLenght) {
           // path.quadTo();//这里里面仿的是绝对的坐标
            //这里里面仿的是相对的坐标，x1，y1， x2，y2，第一个点和第二个点，第二个永远 相对于最开始的那个点增加了多少的坐标
            path.rQuadTo(halfWaveLength/2,-waveHeight,halfWaveLength,0);//画半个波长
            path.rQuadTo(halfWaveLength/2,waveHeight,halfWaveLength,0);//下半个波长
        }
        float x = width / 2;
        //两个图形相交
        region = new Region();
        Region clipe = new Region((int)(x - 0.1),0,(int)x,height*2);
        //用一个举行区域去切割一个path，得到一个新的举行区域
        region.setPath(path,clipe);//切割举行区域
        //画右边的线
        path.lineTo(width,height);
        path.lineTo(0,height);
        path.close();//画一条封闭的曲线
        //填充的效果是paint的Style
    }


    public void startMyAnamitatin(){
        valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.setDuration(diration);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fracton = (float) valueAnimator.getAnimatedValue();
                dx = (int) (waveLenght *fracton);

                if(isRise){
                    if(originY <= (DensityUtil.getMyTitleBarHeight(MainActivity.activity)+
                            DensityUtil.getStatusHeight(MainActivity.activity))){
                        dy = dy - 5;
                    }else
                    if(originY >= -500){
                        dy = dy + 5;
                    }
                }

                postInvalidate();
            }
        });
        valueAnimator.start();
    }

    /**
     * 是否起风了
     * 是否上线浮动
     */
    public void setMyRise(boolean isRise){
        this.isRise = isRise;
        if(valueAnimator == null){
            startMyAnamitatin();
        }else{
            invalidate();
        }
    }

    /**
     * 设置速度
     * @param diration
     */
    public void setMyDuration(int diration){

        if(this.diration<500){
            this.diration =   500;
        }else{
            this.diration -=   diration;
        }
        if(valueAnimator == null){
            startMyAnamitatin();
        }else{
            valueAnimator.setDuration(this.diration);
            invalidate();
        }
        valueAnimator.resume();
    }

    /**
     * 设置减速
     */
    public void setDelMyDuration(int diration){

        if(this.diration<500){
            this.diration =   500;
        }else{
            this.diration +=   diration;
        }
        if(valueAnimator == null){
            startMyAnamitatin();
        }else{
            valueAnimator.setDuration(this.diration);
            invalidate();
        }
        valueAnimator.resume();
    }

    /**
     * 动画的暂停状态
     */
    public void setValueAnimatorPause(){
        if(valueAnimator != null){
            valueAnimator.pause();
        }

    }

    /**
     * 动画的恢复状态
     */
    public void setValueAnimatorResume(){
        if(valueAnimator != null) {
            valueAnimator.resume();
        }
    }
}
