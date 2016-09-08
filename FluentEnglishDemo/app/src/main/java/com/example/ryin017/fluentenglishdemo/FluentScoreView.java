package com.example.ryin017.fluentenglishdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by ryin017 on 9/8/2016.
 */
public class FluentScoreView extends View {

    private String[] mScoreStr = new String[]{
            "0", "起步",
            "2000", "初级",
            "4000", "全速",
            "6000", "MAX",
            "8000",
    };
    // 默认宽高值
    private int defaultSize;
    // view宽度
    private int width;
    // view高度
    private int height;
    //半径
    private int radius;
    // 最小数字
    private int mMinScore = 0;

    private int mMaxScore = 8000;
    //中间文本
    private Paint mTextPaint;

    //外层圆弧
    private Paint mOutArcPaint;

    //内环刻度
    private Paint mInnerArcPaint;

    private RectF mOutRect;

    private RectF mInnerRect;

    // 距离圆环的值
    private int arcDistance;

    // 默认Padding值
    private final static int defaultPadding = 30;

    //  圆环起始角度
    private final static float mStartAngle = 165f;

    // 圆环结束角度
    private final static float mEndAngle = 210f;
    //小刻度画笔
    private Paint mSmallCalibrationPaint;
    //外环text
    private Paint mCalibrationTextPaint;

    // 最外层渐变圆环画笔
//    private Paint mGradientRingPaint;

    //进度圆环画笔
    private Paint mArcProgressPaint;

    // 指针全部进度
    private float mTotalAngle = 210f;

    // 指针当前进度
    private float mCurrentAngle = 0f;


    // 最外层圆环渐变色环颜色
    private final int[] mColors = new int[]{
            0xFFFF0000,
            0xFFFFD600,
            0xFF00FF00
    };

    public FluentScoreView(Context context) {
        this(context, null);
    }

    public FluentScoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FluentScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        defaultSize = dp2px(250);
        arcDistance = dp2px(14);

        //外层圆弧画笔
        mOutArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutArcPaint.setStrokeWidth(8);
        mOutArcPaint.setColor(Color.WHITE);
        mOutArcPaint.setStyle(Paint.Style.STROKE);
        mOutArcPaint.setAlpha(80);

        //内层圆环画笔
        mInnerArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerArcPaint.setStrokeWidth(30);
        mInnerArcPaint.setColor(Color.WHITE);
        mInnerArcPaint.setAlpha(80);
        mInnerArcPaint.setStyle(Paint.Style.STROKE);

        //中间text
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        //圆环小刻度画笔
        mSmallCalibrationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallCalibrationPaint.setStrokeWidth(1);
        mSmallCalibrationPaint.setStyle(Paint.Style.STROKE);
        mSmallCalibrationPaint.setColor(Color.WHITE);
        mSmallCalibrationPaint.setAlpha(130);

        //圆环刻度文本画笔
        mCalibrationTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCalibrationTextPaint.setTextSize(30);
        mCalibrationTextPaint.setAlpha(130);
        mCalibrationTextPaint.setColor(Color.WHITE);

//        mGradientRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        //设置圆环渐变色渲染
//        mGradientRingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
//        float position[] = {0.1f, 0.3f, 0.8f};
//        Shader mShader = new SweepGradient(width / 2, radius, mColors, position);
//        mGradientRingPaint.setShader(mShader);
//        mGradientRingPaint.setStrokeCap(Paint.Cap.ROUND);
//        mGradientRingPaint.setStyle(Paint.Style.STROKE);
//        mGradientRingPaint.setStrokeWidth(40);


        //外层进度画笔
        mArcProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcProgressPaint.setStrokeWidth(40);
        mArcProgressPaint.setColor(Color.BLUE);
        mArcProgressPaint.setStyle(Paint.Style.STROKE);
        mArcProgressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setScore(int score) {
        mMaxScore = score;
        mTotalAngle = 140f;
        startAnim();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveMeasure(widthMeasureSpec, defaultSize),
                resolveMeasure(heightMeasureSpec, defaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius = width / 2;


        mOutRect = new RectF(
                defaultPadding, defaultPadding,
                width - defaultPadding, height - defaultPadding);

        mInnerRect = new RectF(
                defaultPadding + arcDistance,
                defaultPadding + arcDistance,
                width - defaultPadding - arcDistance,
                height - defaultPadding - arcDistance);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCenterText(canvas);
        drawInnerArc(canvas);
        drawSmallCalibration(canvas);
        drawBigCalibration(canvas);
        drawCalibrationAndText(canvas);
        drawRingProgress(canvas);
    }

    private void drawCenterText(Canvas canvas) {
        mTextPaint.setTextSize(60);
        canvas.drawText("口语力", radius, radius - 140, mTextPaint);

        mTextPaint.setTextSize(200);
        mTextPaint.setStyle(Paint.Style.STROKE);
        canvas.drawText(String.valueOf(mMinScore), radius, radius + 70, mTextPaint);

        mTextPaint.setTextSize(60);
        mTextPaint.setStyle(Paint.Style.STROKE);
        canvas.drawText("击败65%的用户", radius, radius + 180, mTextPaint);
    }

    private void drawInnerArc(Canvas canvas) {
        canvas.drawArc(mInnerRect, mStartAngle, mEndAngle, false, mInnerArcPaint);
//        canvas.drawArc(mInnerRect, mStartAngle, mEndAngle, false, mGradientRingPaint);
    }

    /**
     * 绘制外层圆环进度和小圆点
     *
     * @param canvas
     */
    private void drawRingProgress(Canvas canvas)
    {

        Path path = new Path();
        path.addArc(mInnerRect, mStartAngle, mCurrentAngle);
        canvas.drawPath(path, mArcProgressPaint);
    }

    private void drawSmallCalibration(Canvas canvas) {
        //旋转画布
        canvas.save();
        canvas.rotate(-105, radius, radius);
        //计算刻度线的起点结束点
        int startDst = (int) (defaultPadding - mOutArcPaint.getStrokeWidth() / 2 - 1);
        int endDst = (int) (startDst + mOutArcPaint.getStrokeWidth());
        for (int i = 0; i <= 208; i++) {
            //每旋转6度绘制一个小刻度
            canvas.drawLine(radius, startDst, radius, endDst, mSmallCalibrationPaint);
            canvas.rotate(1, radius, radius);
        }
        canvas.restore();
    }

    private void drawBigCalibration(Canvas canvas) {
        canvas.save();
        canvas.rotate(-105, radius, radius);
        int startDst = (int) (defaultPadding / 2 - mOutArcPaint.getStrokeWidth() / 2);
        int endDst = (int) (startDst + mOutArcPaint.getStrokeWidth() + defaultPadding);
        for (int i = 0; i <= 4; i++) {
            //每旋转6度绘制一个小刻度
            canvas.drawLine(radius, startDst, radius, endDst, mSmallCalibrationPaint);
            canvas.rotate(52f, radius, radius);
        }
        canvas.restore();
    }


    private void drawCalibrationAndText(Canvas canvas) {
        canvas.save();
        canvas.rotate(-105, radius, radius);
        int startDst = (int) (defaultPadding + arcDistance - mOutArcPaint.getStrokeWidth() / 2 - 1);
        int endDst = (int) (startDst + mOutArcPaint.getStrokeWidth());
        //刻度旋转的角度
        int rotateAngle = 210 / 8;
        for (int i = 0; i <= 8; i++) {
            // 测量文本的长度
            float textLen = mCalibrationTextPaint.measureText(mScoreStr[i]);
            canvas.drawText(mScoreStr[i], radius - textLen / 2, endDst - 80, mCalibrationTextPaint);
            canvas.rotate(rotateAngle, radius, radius);
        }

        canvas.restore();
    }

    private void startAnim() {
        ValueAnimator scoreAnim = ValueAnimator.ofInt(mMinScore, mMaxScore);
        scoreAnim.setDuration(2500);
        scoreAnim.setInterpolator(new LinearInterpolator());
        scoreAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMinScore = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        scoreAnim.start();

        ValueAnimator mAngleAnim = ValueAnimator.ofFloat(mCurrentAngle, mTotalAngle);
        mAngleAnim.setInterpolator(new BounceInterpolator());
        mAngleAnim.setDuration(2500);
        mAngleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {

                mCurrentAngle = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        mAngleAnim.start();
    }

    /**
     * 根据传入的值进行测量
     *
     * @param measureSpec
     * @param defaultSize
     */
    private int resolveMeasure(int measureSpec, int defaultSize) {

        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {

            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
                //设置warp_content时设置默认值
                result = Math.min(specSize, defaultSize);
                break;
            case MeasureSpec.EXACTLY:
                //设置math_parent 和设置了固定宽高值
                break;
            default:
                result = defaultSize;
        }

        return result;
    }

    /**
     * dp2px
     *
     * @param values
     * @return
     */
    public int dp2px(int values) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5f);
    }
}
