package com.test.dragclosedemo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Laughing on 2017/7/4.
 * <p/>
 * 只有y轴运动才能放大缩小屏幕，往y轴方向 向上提是放大或者不变，往y轴方向 向下拉是缩小，或者缩小到极限
 */
public class ScaleViewPager extends BaseAnimCloseViewPager {

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_MOVING = 1;
    public static final int STATUS_REBACK = 2;
    public static final String TAG = "ScaleViewPager";

    //最多可缩小比例
    public static final float MIN_SCALE_WEIGHT = 0.25f;
    public static final int REBACK_DURATION = 300;//ms
    public static final int DRAG_GAP_PX = 50;

    private int currentStatus = STATUS_NORMAL;

    float mDownX;
    float mDownY;


    public ScaleViewPager(Context context) {
        super(context);
    }

    public ScaleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (currentStatus == STATUS_REBACK)
            return false;
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                addIntoVelocity(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                addIntoVelocity(ev);
                int deltaY = (int) (ev.getRawY() - mDownY);
                if (deltaY <= DRAG_GAP_PX && currentStatus!=STATUS_MOVING)
                    return super.onTouchEvent(ev);
                if (currentPageStatus!=SCROLL_STATE_DRAGGING && (deltaY>DRAG_GAP_PX||currentStatus==STATUS_MOVING)){
                    setupMoving(ev.getRawX(),ev.getRawY());
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (currentStatus!=STATUS_MOVING){
                    iAnimClose.onPictureClick();
                    return super.onTouchEvent(ev);
                }
                final float mUpX = ev.getRawX();//->mDownX
                final float mUpY = ev.getRawY();//->mDownY

                float vY = computeYVelocity();
                if (vY>=1500||Math.abs(mUpY-mDownY)>screenHeight/4){//速度有一定快，或者移动位置超过屏幕一半，那么释放
                    if (iAnimClose !=null)
                        iAnimClose.onPictureRelease(currentShowView);
                }else {
                    setupReback(mUpX,mUpY);
                }


                break;
        }
        return super.onTouchEvent(ev);
    }

    private void setupReback(final float mUpX, final float mUpY){
        currentStatus = STATUS_REBACK;
        if (mUpY!=mDownY) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mUpY, mDownY);
            valueAnimator.setDuration(REBACK_DURATION);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float mY = (float) animation.getAnimatedValue();
                    float percent = (mY - mDownY) / (mUpY - mDownY);
                    float mX = percent * (mUpX - mDownX) + mDownX;
                    setupMoving(mX, mY);
                    if (mY == mDownY) {
                        mDownY = 0;
                        mDownX = 0;
                        currentStatus = STATUS_NORMAL;
                    }
                }
            });
            valueAnimator.start();
        }else if (mUpX!=mDownX){
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mUpX, mDownX);
            valueAnimator.setDuration(REBACK_DURATION);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float mX = (float) animation.getAnimatedValue();
                    float percent = (mX - mDownX) / (mUpX - mDownX);
                    float mY = percent * (mUpY - mDownY) + mDownY;
                    setupMoving(mX, mY);
                    if (mX == mDownX) {
                        mDownY = 0;
                        mDownX = 0;
                        currentStatus = STATUS_NORMAL;
                    }
                }
            });
            valueAnimator.start();
        }else if (iAnimClose !=null)
            iAnimClose.onPictureClick();
    }


    private void setupMoving(float movingX ,float movingY) {
        if (currentShowView == null)
            return;
        currentStatus = STATUS_MOVING;
        float deltaX = movingX - mDownX;
        float deltaY = movingY - mDownY;
        float scale = 1f;
        float alphaPercent = 1f;
        if(deltaY>0) {
            scale = 1 - Math.abs(deltaY) / screenHeight;
            alphaPercent = 1- Math.abs(deltaY) / (screenHeight/2);
        }

        ViewHelper.setTranslationX(currentShowView, deltaX);
        ViewHelper.setTranslationY(currentShowView, deltaY);
        setupScale(scale);
        setupBackground(alphaPercent);
    }


    private void setupScale(float scale) {
        scale = Math.min(Math.max(scale, MIN_SCALE_WEIGHT), 1);
        ViewHelper.setScaleX(currentShowView, scale);
        ViewHelper.setScaleY(currentShowView, scale);
    }
}
