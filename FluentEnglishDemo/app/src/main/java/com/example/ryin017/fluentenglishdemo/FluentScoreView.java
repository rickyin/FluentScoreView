package com.example.ryin017.fluentenglishdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.text.DecimalFormat;

/**
 * Created by ryin017 on 9/8/2016.
 */
public class FluentScoreView extends View {

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
    //中间灰色文本
    private Paint mTextGrayPaint;

    //外层圆弧
    private Paint mOutArcPaint;

    //内环刻度
    private Paint mInnerArcPaint;


    private RectF mInnerRect;

    // 距离圆环的值
    private int arcDistance;

    // 默认Padding值
    private final static int defaultPadding = 30;

    //  圆环起始角度
    private final static float mStartAngle = 160f;

    // 圆环扫过角度
    private final static float mSweepAngle = 220f;
    //小刻度画笔
    private Paint mSmallCalibrationPaint;
    //外环text
    private Paint mCalibrationTextPaint;


    //进度圆环画笔
    private Paint mArcProgressPaint;

    // 指针全部进度
    private float mTotalAngle = 220f;

    // 指针当前进度
    private float mCurrentAngle = 0f;

    private String mPercent = "";

    private String[] mScoreStr = new String[]{
            "0", "起步",
            "2000", "初级",
            "4000", "全速",
            "6000", "MAX",
            "8000",
    };

    // 最外层圆环渐变色环颜色
    private final int[] mColors = new int[]{
            0xFF4CAF50,
            0xFFFFC107,
            0xFFCDDC39,
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
        arcDistance = dp2px(26);

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


        //中间灰色text
        mTextGrayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextGrayPaint.setColor(Color.WHITE);
        mTextGrayPaint.setTextAlign(Paint.Align.CENTER);
        mTextGrayPaint.setAlpha(130);

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

        //外层进度画笔
        mArcProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcProgressPaint.setStrokeWidth(30);
        mArcProgressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        float position[] = {0.1f, 0.2f, 0.3f};
        Shader mShader = new SweepGradient(width / 2, radius, mColors, position);
        mArcProgressPaint.setShader(mShader);
        mArcProgressPaint.setStrokeCap(Paint.Cap.SQUARE);
        mArcProgressPaint.setStyle(Paint.Style.STROKE);
    }

    public void setScore(int score) {
        float percent = (float) score / mMaxScore;
        DecimalFormat df = new DecimalFormat("0.00%");
        mPercent = "击败percent的用户".replace("percent", df.format(percent));
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
        mTextGrayPaint.setTextSize(60);
        canvas.drawText("口语力", radius, radius - 140, mTextGrayPaint);

        mTextPaint.setTextSize(200);
        mTextPaint.setStyle(Paint.Style.STROKE);
        canvas.drawText(String.valueOf(mMinScore), radius, radius + 70, mTextPaint);

        mTextGrayPaint.setTextSize(60);
        mTextPaint.setStyle(Paint.Style.STROKE);
        canvas.drawText(mPercent, radius, radius + 180, mTextGrayPaint);
    }

    private void drawInnerArc(Canvas canvas) {
        canvas.drawArc(mInnerRect, mStartAngle, mSweepAngle, false, mInnerArcPaint);
    }

    /**
     * 绘制外层圆环进度和小圆点
     *
     * @param canvas
     */
    private void drawRingProgress(Canvas canvas) {

        Path path = new Path();
        path.addArc(mInnerRect, mStartAngle, mCurrentAngle);
        canvas.drawPath(path, mArcProgressPaint);
    }

    private void drawSmallCalibration(Canvas canvas) {
        //旋转画布
        canvas.save();
        canvas.rotate(-110, radius, radius);
        //计算刻度线的起点结束点
        int startDst = (int) (defaultPadding + mOutArcPaint.getStrokeWidth() / 2 + 30);
        int endDst = (int) (startDst + mOutArcPaint.getStrokeWidth());
        for (int i = 0; i <= 220; i++) {
            if (i % 55 != 0) {
                canvas.drawLine(radius, startDst, radius, endDst, mSmallCalibrationPaint);
            }
            canvas.rotate(1, radius, radius);
        }
        canvas.restore();
    }

    private void drawBigCalibration(Canvas canvas) {
        canvas.save();
        canvas.rotate(-110, radius, radius);
        int startDst = (int) (defaultPadding - mOutArcPaint.getStrokeWidth() / 2 + 20);
        int endDst = (int) (startDst + mOutArcPaint.getStrokeWidth() + defaultPadding);
        for (int i = 0; i <= 4; i++) {
            //每旋转55度绘制一个大刻度
            canvas.drawLine(radius, startDst, radius + 1, endDst, mSmallCalibrationPaint);
            canvas.rotate(55f, radius, radius);
        }
        canvas.restore();
    }


    private void drawCalibrationAndText(Canvas canvas) {
        canvas.save();
        canvas.rotate(-110, radius, radius);
        int startDst = (int) (defaultPadding + arcDistance - mOutArcPaint.getStrokeWidth() / 2 - 1);
        int endDst = (int) (startDst + mOutArcPaint.getStrokeWidth());
        //刻度旋转的角度
        int rotateAngle = 220 / 8;
        for (int i = 0; i <= 8; i++) {
            // 测量文本的长度
            float textLen = mCalibrationTextPaint.measureText(mScoreStr[i]);
            canvas.drawText(mScoreStr[i], radius - textLen / 2 + 6, endDst - 100, mCalibrationTextPaint);
            canvas.rotate(rotateAngle, radius, radius);
        }
        canvas.restore();
    }

    private void startAnim() {
        ValueAnimator scoreAnim = ValueAnimator.ofInt(mMinScore, mMaxScore);
        scoreAnim.setDuration(2000);
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
        mAngleAnim.setInterpolator(new LinearInterpolator());
        mAngleAnim.setDuration(2000);
        mAngleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

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
