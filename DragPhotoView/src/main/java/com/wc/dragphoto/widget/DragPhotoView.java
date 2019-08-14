package com.wc.dragphoto.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.wc.dragphoto.photoview.PhotoView;


/**
 * 拖拽PhotoView
 * Created by wing on 2016/12/22.
 */

public class DragPhotoView extends PhotoView {
    private Paint mPaint;
    // downX
    private float mDownX;
    // down Y
    private float mDownY;

    private float mLastTranslateY;
    private float mLastTranslateX;

    private float mTranslateY;
    private float mTranslateX;
    private float mScaleX = 1;
    private float mScaleY = 1;
    private int mWidth;
    private int mHeight;
    private float mMinScale = 0.5f;
    private int mAlpha = 255;
    private final static int MAX_TRANSLATE_Y = 500;
    private final static int MAX_EXIT_Y = 200;

    private final static long DURATION = 300;
    private boolean canFinish = false;
    private boolean isAnimate = false;

    //is event on PhotoView
    private boolean isTouchEvent = false;
    private OnTapListener mTapListener;
    private OnExitListener mExitListener;
    private OnDragListener mDragListener;

    //触发滑动条件
    private int mTouchSlop;
    private boolean isActivityAnimate = false;
    private boolean isFinshAnimate = false;
    private boolean isLongClick = false;

    public DragPhotoView(Context context) {
        this(context, null);
    }

    public DragPhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public DragPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setAlpha(mAlpha);
        canvas.drawRect(0, 0, 2000, 3000, mPaint);
        if (!isActivityAnimate) {
            canvas.translate(mTranslateX, mTranslateY);
            canvas.scale(mScaleX, mScaleY, mWidth / 2, mHeight / 2);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //only scale == 1 can drag
        if (getScale() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mDownY = event.getY();
                    //change the canFinish flag
                    canFinish = !canFinish;
                    isActivityAnimate = false;
                    isAnimate = false;
                    isLongClick = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isLongClick) {//长按事件触发之后不做处理
                        return super.dispatchTouchEvent(event);
                    }
                    //in viewpager
                    final float yDiff = event.getY() - mDownY;
                    //正在滑动中，上一次的TranslateX Y不等于0，Y的移动距离达到了滑动触发点，则把事件交给DragPhotoView
                    if (isTouchEvent || mLastTranslateX != 0 || mLastTranslateY != 0 || yDiff > mTouchSlop) {
//                        && yDiff < mTouchSlop
                        if (mTranslateY == 0 && mTranslateX != 0 && yDiff < mTouchSlop) {
//                            //如果不消费事件，则不作操作
                            if (!isTouchEvent) {
                                mScaleX = 1;
                                mScaleY = 1;
                                return super.dispatchTouchEvent(event);
                            }
                        }
                        //single finger drag  down
                        if (mTranslateY >= 0 && event.getPointerCount() == 1) {
                            onActionMove(event);
                            //如果有上下位移 则不交给viewpager
                            if (mTranslateY != 0) {
                                isTouchEvent = true;
                            }
                            return true;
                        }
                        //防止下拉的时候双手缩放
                        if (mTranslateY >= 0 && mScaleX < 0.95) {
                            return true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //防止下拉的时候双手缩放
                    if (event.getPointerCount() == 1) {
                        onActionUp(event);
                        isTouchEvent = false;
                        isLongClick = false;
                        //judge finish or not
//                        postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (mTranslateX == 0 && mTranslateY == 0 && canFinish) {
//                                    if (mTapListener != null) {
//                                        mTapListener.onTap(DragPhotoView.this);
//                                    }
//                                }
//                                canFinish = false;
//                            }
//                        }, 300);
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void onActionUp(MotionEvent event) {
        if (mTranslateY > MAX_EXIT_Y) {
            if (mExitListener != null) {
                mExitListener.onExit(this, mTranslateX, mTranslateY, mWidth, mHeight, MAX_TRANSLATE_Y);
            } else {
                throw new RuntimeException("DragPhotoView: onExitLister can't be null ! call setOnExitListener() ");
            }
        } else {
            performAnimation();
        }
    }

    private void onActionMove(MotionEvent event) {
        float moveY = event.getY();
        float moveX = event.getX();
        if (mDragListener != null) {
            mDragListener.onDrag(this, moveX, moveY);
        }
        mTranslateX = moveX - mDownX + mLastTranslateX;
        mTranslateY = moveY - mDownY + mLastTranslateY;
        //保证上划到到顶还可以继续滑动
        if (mTranslateY < 0) {
            mTranslateY = 0;
        }
        float percent = mTranslateY / MAX_TRANSLATE_Y /5;
        if (mScaleX >= mMinScale && mScaleX <= 1f) {
            mScaleX = 1 - percent;
            mScaleY = 1 - percent;
            mAlpha = (int) (255 * (1 - percent));
            if (mAlpha > 255) {
                mAlpha = 255;
            } else if (mAlpha < 0) {
                mAlpha = 0;
            }
        }
        if (mScaleX < mMinScale) {
            mScaleX = mMinScale;
            mScaleY = mMinScale;
        } else if (mScaleX > 1f) {
            mScaleX = 1;
            mScaleY = 1;
        }
        invalidate();
    }

    private void performAnimation() {

        if (mScaleX == 1 && mScaleY == 1 && mTranslateX == 0) {
            return;
        }

        //Alpha动画
        ValueAnimator animator = ValueAnimator.ofInt(mAlpha, 255);
        animator.setDuration(DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (isAnimate) {
                    mAlpha = (int) valueAnimator.getAnimatedValue();
                }
            }
        });
        animator.start();
        //TranslateX动画
        animator = ValueAnimator.ofFloat(mTranslateX, 0);
        animator.setDuration(DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (isAnimate) {
                    mTranslateX = (float) valueAnimator.getAnimatedValue();
                    mLastTranslateX = mTranslateX;
                }
            }
        });
        animator.start();
        //TranslateY动画
        animator = ValueAnimator.ofFloat(mTranslateY, 0);
        animator.setDuration(DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (isAnimate) {
                    mTranslateY = (float) valueAnimator.getAnimatedValue();
                    mLastTranslateY = mTranslateY;
                }
            }
        });
        animator.start();

        //Scale动画
        animator = ValueAnimator.ofFloat(mScaleX, 1);
        animator.setDuration(DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (isAnimate) {
                    mScaleX = (float) valueAnimator.getAnimatedValue();
                    mScaleY = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mTapListener != null && isAnimate) {
                    mTapListener.onTap(DragPhotoView.this);
                }
                if (isAnimate) {
                    mScaleX = 1;
                    mScaleY = 1;
                    mTranslateX = 0;
                    mTranslateY = 0;
                    invalidate();
                }
                isAnimate = false;
                animator.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    //修改触发OnClickListener事件的条件
    @Override
    public void setOnClickListener(final View.OnClickListener l) {
        super.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAnimate && !isFinshAnimate) {
                    l.onClick(v);
                }
            }
        });
    }

    @Override
    public void setOnLongClickListener(final View.OnLongClickListener l) {
        super.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isAnimate && !isFinshAnimate) {
                    if (mScaleX == 1 && mScaleY == 1 && mTranslateX == 0) {
                        isLongClick = true;
                        return l.onLongClick(v);
                    }
                }
                return false;
            }
        });
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

    public void setOnTapListener(OnTapListener listener) {
        mTapListener = listener;
    }

    public void setOnExitListener(OnExitListener listener) {
        mExitListener = listener;
    }

    public void setOnDragListener(OnDragListener listener) {
        mDragListener = listener;
    }

    public void setBackgroundAlpha(int alpha) {
        this.mAlpha = alpha;
        invalidate();
    }

    public void setActivityAnimate(boolean isActivityAnimate) {
        this.isActivityAnimate = isActivityAnimate;
    }

    public interface OnTapListener {
        void onTap(DragPhotoView view);
    }

    public interface OnExitListener {
        void onExit(DragPhotoView view, float translateX, float translateY, float w, float h, int maxTranslateY);
    }

    public interface OnDragListener {
        void onDrag(DragPhotoView view, float moveX, float moveY);
    }

    /**
     * 入场动画
     */
    public void performEnterAnimation(float scaleX, float scaleY) {
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(getX(), 0);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(DURATION);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(getY(), 0);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(DURATION);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(scaleY, 1);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(DURATION);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(scaleX, 1);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleXAnimator.setDuration(DURATION);
        scaleXAnimator.start();
    }

    /**
     * 退出动画
     */
    public void finishWithAnimation(final Activity activity, int left, int top, int width, int height) {
        isFinshAnimate = true;
        int[] locationPhoto = new int[2];
        getLocationOnScreen(locationPhoto);
        float targetHeight = (float) getHeight();
        float targetWidth = (float) getWidth();
        float scaleX = (float) width / targetWidth;
        float imageHeight = targetHeight;
        int scale = (int)getResources().getDisplayMetrics().density;
        if (getDrawable() != null) {
            imageHeight = getDrawable().getIntrinsicHeight()*scale;
        }
        float scaleY = (float) height / imageHeight;

        float targetCenterX = locationPhoto[0] + targetWidth / 2;
        float targetCenterY = locationPhoto[1] + targetHeight / 2;
        float translationX = left + width / 2 - targetCenterX;
        float translationY = top + height / 2 - targetCenterY;

        ValueAnimator alphaAnimator = ValueAnimator.ofInt(255, 0);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAlpha = (Integer) valueAnimator.getAnimatedValue();
            }
        });
        alphaAnimator.setDuration(DURATION);
        alphaAnimator.start();

        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0, translationX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTranslateX = (Float) valueAnimator.getAnimatedValue();
            }
        });
        translateXAnimator.setDuration(DURATION);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0, translationY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTranslateY = (Float) valueAnimator.getAnimatedValue();
            }
        });
        translateYAnimator.setDuration(DURATION);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, scaleY);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mScaleY = (Float) valueAnimator.getAnimatedValue();
            }
        });
        scaleYAnimator.setDuration(DURATION);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, scaleX);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mScaleX = (Float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        scaleXAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scaleXAnimator.setDuration(DURATION);
        scaleXAnimator.start();
    }

    /**
     * 滑动达到设定值后的退场动画
     */
    public void performExitAnimation(final Activity activity, int left, int top, int width, int height) {
        isFinshAnimate = true;
        int[] locationPhoto = new int[2];
        getLocationOnScreen(locationPhoto);
        float targetHeight = (float) getHeight();
        float targetWidth = (float) getWidth();
        float scaleX = (float) width / targetWidth;
        float imageHeight = targetHeight;
        int scale = (int)getResources().getDisplayMetrics().density;
        if (getDrawable() != null) {
            imageHeight = getDrawable().getIntrinsicHeight()*scale;
        }
        float scaleY = (float) height / imageHeight;

        float targetCenterX = locationPhoto[0] + targetWidth / 2;
        float targetCenterY = locationPhoto[1] + targetHeight / 2;
        float translationX = left + width / 2 - targetCenterX;
        float translationY = top + height / 2 - targetCenterY;

        ValueAnimator alphaAnimator = ValueAnimator.ofInt(mAlpha, 0);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAlpha = (Integer) valueAnimator.getAnimatedValue();
            }
        });
        alphaAnimator.setDuration(DURATION);
        alphaAnimator.start();

        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(mTranslateX, translationX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTranslateX = (Float) valueAnimator.getAnimatedValue();
            }
        });
        translateXAnimator.setDuration(DURATION);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(mTranslateY, translationY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTranslateY = (Float) valueAnimator.getAnimatedValue();
            }
        });
        translateYAnimator.setDuration(DURATION);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(mScaleY, scaleY);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mScaleY = (Float) valueAnimator.getAnimatedValue();
            }
        });
        scaleYAnimator.setDuration(DURATION);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(mScaleX, scaleX);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mScaleX = (Float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        scaleXAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scaleXAnimator.setDuration(DURATION);
        scaleXAnimator.start();
    }
}

